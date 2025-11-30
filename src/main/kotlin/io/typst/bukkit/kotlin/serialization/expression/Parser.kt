package io.typst.bukkit.kotlin.serialization.expression

import io.vavr.control.Either

object Parser {
    fun parse(tokens: List<Token>): Either<Failure, Expression> {
        return parseExpression(0, tokens, 0).flatMap { (expr, index) ->
            val token = tokens.getOrNull(index)
            if (token?.tokenType != TokenType.EOF) {
                Either.left(Failure("parser - Unexpected token: ${token?.lexeme}", index))
            } else {
                Either.right(expr)
            }
        }
    }

    private fun parseFunctionCall(
        name: String,
        tokens: List<Token>,
        i: Int,
    ): Either<Failure, Pair<Expression, Int>> {
        val firstToken = tokens.getOrNull(i)
            ?: return Either.left(Failure("parser - Unterminated function call: $name", i))
        if (firstToken.tokenType == TokenType.RIGHT_PAREN) {
            return Either.right(Expression.FunctionCall(name, emptyList()) to (i + 1))
        }
        val args = mutableListOf<Expression>()

        var index = i
        while (true) {
            val either = parseExpression(0, tokens, index)
            if (either is Either.Left) {
                return either
            }
            val (argExpr, nextIndex) = either.get()
            args.add(argExpr)
            index = nextIndex
            val token = tokens.getOrNull(index)
                ?: return Either.left(Failure("parser - Unterminated argument list in call to $name", index))
            when (token.tokenType) {
                TokenType.COMMA -> {
                    index += 1
                }

                TokenType.RIGHT_PAREN -> return Either.right(
                    Expression.FunctionCall(name, args) to (index + 1)
                )

                else -> return Either.left(Failure("parser - Expected ',' or ')' in argument list of $name", index))
            }
        }
    }

    private fun parsePrefix(tokens: List<Token>, i: Int): Either<Failure, Pair<Expression, Int>> {
        val current = tokens[i]
        return when (current.tokenType) {
            TokenType.NUMBER -> {
                val value = current.lexeme.toDouble()
                Either.right(Expression.Literal(value) to (i + 1))
            }

            TokenType.IDENTIFIER -> {
                val name = current.lexeme
                val next = tokens.getOrNull(i + 1)
                if (next?.tokenType == TokenType.LEFT_PAREN) {
                    parseFunctionCall(name, tokens, i + 2)
                } else {
                    Either.right(Expression.Variable(current.lexeme) to (i + 1))
                }
            }

            TokenType.OPERATOR -> {
                val op = current.lexeme
                val unaryOp = UnaryOpType.from(op[0])
                if (unaryOp != null) {
                    parseExpression(unaryOp.bindingPower, tokens, i + 1)
                        .map { (operand, newIndex) ->
                            Expression.Unary(unaryOp, operand) to newIndex
                        }
                } else {
                    Either.left(Failure("parser - Unexpected prefix operator: $op", i))
                }
            }

            TokenType.LEFT_PAREN -> {
                parseExpression(0, tokens, i + 1)
                    .flatMap { (expr, newIndex) ->
                        if (tokens[newIndex].tokenType != TokenType.RIGHT_PAREN) {
                            Either.left(Failure("parser - Expected ')' to close '('", newIndex))
                        } else Either.right(expr to (newIndex + 1))
                    }
            }

            else -> {
                Either.left(Failure("parser - Unexpected token: ${current.lexeme}", i))
            }
        }
    }

    private fun parseExpression(
        minBp: Int,
        tokens: List<Token>,
        i: Int,
    ): Either<Failure, Pair<Expression, Int>> {
        val leftEither = parsePrefix(tokens, i)
        if (leftEither is Either.Left) {
            return leftEither
        }
        var (left, newIndex) = leftEither.get()

        while (true) {
            val token = tokens[newIndex]
            if (token.tokenType != TokenType.OPERATOR) {
                break
            }
            val op = token.lexeme
            val binaryOp = BinaryOpType.from(op[0])
            val (leftBp, rightBp) = binaryOp?.bindingPowers
                ?: return Either.left(Failure("parser - Unknown operator: $op", newIndex))
            if (leftBp < minBp) break
            val rightEither = parseExpression(rightBp, tokens, newIndex + 1)
            if (rightEither is Either.Left) {
                return rightEither
            }
            val (right, theIndex) = rightEither.get()
            newIndex = theIndex
            left = Expression.Binary(binaryOp, left, right)
        }
        return Either.right(left to newIndex)
    }
}
