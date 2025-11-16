package io.typst.bukkit.kotlin.serialization.expression

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LexerTest {
    @Test
    fun lex() {
        // eof
        assertEquals(listOf(Token(TokenType.EOF, "", 0, 0)), Lexer.lexAll("").get())
        assertEquals(listOf(Token(TokenType.EOF, "", 1, 1)), Lexer.lexAll(" ").get())

        // digit
        assertEquals(tokenOf(3, Token(TokenType.NUMBER, "123", 0, 3)), Lexer.lexAll("123").get())
        assertEquals(tokenOf(4, Token(TokenType.NUMBER, "123", 1, 4)), Lexer.lexAll(" 123").get())
        assertEquals(tokenOf(5, Token(TokenType.NUMBER, "123", 1, 4)), Lexer.lexAll(" 123 ").get())
        assertEquals(tokenOf(6, Token(TokenType.NUMBER, "123.0", 1, 6)), Lexer.lexAll(" 123.0").get())
        assertEquals(tokenOf(6, Token(TokenType.NUMBER, "123.0", 1, 6)), Lexer.lexAll(" 123.0").get())
        assertEquals(6, Lexer.lexAll(" 123.0.1").left.index)

        // letter
        assertEquals(tokenOf(3, Token(TokenType.IDENTIFIER, "abc", 0, 3)), Lexer.lexAll("abc").get())
        assertEquals(
            tokenOf(3, Token(TokenType.IDENTIFIER, "a", 0, 1), Token(TokenType.NUMBER, "1", 2, 3)),
            Lexer.lexAll("a 1").get()
        )

        // unary
        listOf(
            "+", "-", "*", "/"
        ).forEach {
            assertEquals(
                tokenOf(2, Token(TokenType.OPERATOR, it, 0, 1), Token(TokenType.NUMBER, "1", 1, 2)),
                Lexer.lexAll("${it}1").get()
            )
        }
        assertEquals(
            0,
            Lexer.lexAll("@").left.index
        )

        // paren
        assertEquals(
            tokenOf(
                3,
                Token(TokenType.LEFT_PAREN, "(", 0, 1),
                Token(TokenType.NUMBER, "1", 1, 2),
                Token(TokenType.RIGHT_PAREN, ")", 2, 3)
            ),
            Lexer.lexAll("(1)").get()
        )
    }

    fun tokenOf(size: Int, vararg tokens: Token): List<Token> {
        return listOf(*tokens) + Token.ofEOF(size)
    }
}