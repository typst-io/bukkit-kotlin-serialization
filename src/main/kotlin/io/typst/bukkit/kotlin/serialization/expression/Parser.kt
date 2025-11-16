package io.typst.bukkit.kotlin.serialization.expression

import io.vavr.control.Either

object Parser {
    fun parse(tokens: List<Token>): Either<Failure, Expression> {
        return parseExpression(0, tokens, 0).flatMap { (expr, index) ->
            val token = tokens.getOrNull(index)
            if (token?.tokenType != TokenType.EOF) {
                Either.left(Failure("Unexpected token: ${token?.lexeme}", index))
            } else {
                Either.right(expr)
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
                Either.right(Expression.Variable(current.lexeme) to (i + 1))
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
                    Either.left(Failure("Unexpected prefix operator: $op", i))
                }
            }

            TokenType.LEFT_PAREN -> {
                parseExpression(0, tokens, i + 1)
                    .flatMap { (expr, newIndex) ->
                        if (tokens[newIndex].tokenType != TokenType.RIGHT_PAREN) {
                            Either.left(Failure("Expected ')' to close '('", newIndex))
                        } else Either.right(expr to (newIndex + 1))
                    }
            }

            else -> {
                Either.left(Failure("Unexpected token: ${current.lexeme}", i))
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
                ?: return Either.left(Failure("Unknown operator: $op", newIndex))
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
