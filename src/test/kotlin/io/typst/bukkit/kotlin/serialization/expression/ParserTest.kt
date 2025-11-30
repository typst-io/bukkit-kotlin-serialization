package io.typst.bukkit.kotlin.serialization.expression

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun parserTest() {
        // lit
        assertEquals(
            Expression.Literal(1.0),
            Parser.parse(Lexer.lexAll("1").get()).get()
        )
        // var
        assertEquals(
            Expression.Variable("x"),
            Parser.parse(Lexer.lexAll("x").get()).get()
        )

        // unary
        assertEquals(
            Expression.Unary(UnaryOpType.PLUS, Expression.Literal(1.0)),
            Parser.parse(Lexer.lexAll("+1").get()).get()
        )
        assertEquals(
            Expression.Unary(UnaryOpType.MINUS, Expression.Literal(1.0)),
            Parser.parse(Lexer.lexAll("-1").get()).get()
        )
        assertEquals(
            0,
            Parser.parse(listOf(Token(TokenType.OPERATOR, "@", 0, 1))).left.index
        )

        // paren
        assertEquals(
            Expression.Unary(UnaryOpType.MINUS, Expression.Literal(1.0)),
            Parser.parse(Lexer.lexAll("-(1)").get()).get()
        )
        assertEquals(
            3,
            Parser.parse(Lexer.lexAll("-(1").get()).left.index
        )
        assertEquals(
            1,
            Parser.parse(Lexer.lexAll("-)1").get()).left.index
        )

        // binary
        assertEquals(
            Expression.Binary(BinaryOpType.MINUS, Expression.Literal(1.0), Expression.Literal(1.0)),
            Parser.parse(Lexer.lexAll("1-1").get()).get()
        )
        assertEquals(
            2,
            Parser.parse(Lexer.lexAll("1-)").get()).left.index
        )
        assertEquals(
            Expression.Binary(
                BinaryOpType.MINUS,
                Expression.Literal(1.0),
                Expression.Binary(BinaryOpType.MULTIPLY, Expression.Literal(2.0), Expression.Literal(3.0))
            ),
            Parser.parse(Lexer.lexAll("1-2*3").get()).get()
        )
        assertEquals(
            1,
            Parser.parse(
                listOf(
                    Token(TokenType.NUMBER, "1", 0, 1),
                    Token(TokenType.OPERATOR, "@", 1, 2),
                    Token(TokenType.NUMBER, "2", 2, 3),
                )
            ).left.index
        )
        assertEquals(
            Expression.Binary(BinaryOpType.MINUS, Expression.Binary(BinaryOpType.MULTIPLY, Expression.Literal(1.0), Expression.Literal(2.0)), Expression.Literal(3.0)),
            Parser.parse(Lexer.lexAll("1*2-3").get()).get()
        )
        assertEquals(
            Expression.Binary(
                BinaryOpType.MULTIPLY,
                Expression.Binary(BinaryOpType.MINUS, Expression.Literal(1.0), Expression.Literal(2.0)),
               Expression.Literal(3.0)
            ),
            Parser.parse(Lexer.lexAll("(1-2)*3").get()).get()
        )
        assertEquals(
            0,
            Evaluator().evaluate("0/0").left.index
        )
        assertEquals(
            1,
            Evaluator().evaluate("1 2").left.index
        )
    }
}