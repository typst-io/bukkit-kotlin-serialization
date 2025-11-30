package io.typst.bukkit.kotlin.serialization.expression

import io.vavr.control.Either

object Lexer {
    fun lex(xs: String, i: Int): Either<Failure, Token> {
        var index = i
        // skip white spaces
        while (index < xs.length) {
            val ch = xs[index]
            if (ch.isWhitespace()) {
                index++
            } else {
                break
            }
        }

        if (index >= xs.length) {
            return Either.right(Token.ofEOF(xs.length))
        }
        val ch = xs[index]
        if (ch.isDigit()) {
            var hasDot = false
            var end = index + 1
            // find digit end
            while (end < xs.length) {
                val c = xs[end]
                if (c == '.') {
                    if (!hasDot) {
                        hasDot = true
                        end++
                    } else {
                        return Either.left(Failure("lex - Non-digit char: $c", end))
                    }
                } else if (c.isDigit()) {
                    end++
                } else break
            }
            val digit = xs.substring(index, end)
            return Either.right(Token(TokenType.NUMBER, digit, index, end))
        }

        if (ch.isLetter()) {
            var end = index + 1
            // find letter end
            while (end < xs.length) {
                val c = xs[end]
                if (c.isLetterOrDigit()) {
                    end++
                } else break
            }
            val letter = xs.substring(index, end)
            return Either.right(Token(TokenType.IDENTIFIER, letter, index, end))
        }

        val tokenType = when (ch) {
            '+', '-', '*', '/', '^' -> TokenType.OPERATOR
            '(' -> TokenType.LEFT_PAREN
            ')' -> TokenType.RIGHT_PAREN
            ',' -> TokenType.COMMA
            else -> null
        }
        return if (tokenType != null) {
            Either.right(Token(tokenType, ch.toString(), index, index + 1))
        } else {
            Either.left(Failure("lex - Unknown char: $ch", index))
        }
    }

    fun lexAll(xs: String): Either<Failure, List<Token>> {
        var index = 0
        val ret = mutableListOf<Token>()
        while (true) {
            val either = lex(xs, index)
            if (either is Either.Right) {
                val token = either.get()
                ret.add(token)
                index = token.end
                if (token.tokenType == TokenType.EOF) {
                    break
                }
            } else {
                return Either.left(either.left)
            }
        }
        return Either.right(ret)
    }
}
