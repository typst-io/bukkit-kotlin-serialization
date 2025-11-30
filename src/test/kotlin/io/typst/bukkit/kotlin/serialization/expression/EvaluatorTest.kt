package io.typst.bukkit.kotlin.serialization.expression

import io.vavr.control.Either
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EvaluatorTest {
    @Test
    fun function() {
        val result =
            evaluate("20 * (1.0 / r)^0.4 * (log10(max(g/1000, 0.1) * 10) + 1)", mapOf("r" to 0.1, "g" to 100_000.0))
        if (result is Either.Left) {
            throw RuntimeException(result.left.toString())
        }
        assertEquals(
            33.302128296074926,
            result.get()
        )
    }

    @Test
    fun evaluate() {
        assertEquals(
            1.0,
            Evaluator().evaluate("1.0").get()
        )
        assertEquals(
            1.0,
            Evaluator().evaluate(Expression.Literal(1.0)).get()
        )
        assertEquals(
            1.0,
            Expression.Literal(1.0).evaluate(emptyMap()).get()
        )
        assertEquals(
            1.0,
            Evaluator(mapOf("xa" to 1.0)).evaluate(Expression.Variable("xa")).get()
        )
        assertEquals(
            0,
            Evaluator().evaluate(Expression.Variable("xa")).left.index
        )

        // unary
        assertEquals(
            1.0,
            Evaluator().evaluate(Expression.Unary(UnaryOpType.PLUS, Expression.Literal(1.0))).get()
        )
        assertEquals(
            -1.0,
            Evaluator().evaluate(Expression.Unary(UnaryOpType.MINUS, Expression.Literal(1.0))).get()
        )

        // binary
        assertEquals(
            3.0,
            Evaluator().evaluate(Expression.Binary(BinaryOpType.PLUS, Expression.Literal(1.0), Expression.Literal(2.0)))
                .get()
        )
        assertEquals(
            -1.0,
            Evaluator().evaluate(
                Expression.Binary(
                    BinaryOpType.MINUS,
                    Expression.Literal(1.0),
                    Expression.Literal(2.0)
                )
            ).get()
        )
        assertEquals(
            2.0,
            Evaluator().evaluate(
                Expression.Binary(
                    BinaryOpType.MULTIPLY,
                    Expression.Literal(1.0),
                    Expression.Literal(2.0)
                )
            ).get()
        )
        assertEquals(
            0.5,
            Evaluator().evaluate(
                Expression.Binary(
                    BinaryOpType.DIVIDE,
                    Expression.Literal(1.0),
                    Expression.Literal(2.0)
                )
            ).get()
        )
        assertEquals(
            0,
            Evaluator().evaluate("x").left.index
        )
        assertEquals(
            0,
            Evaluator().evaluate("a+b").left.index
        )
        assertEquals(
            0,
            Evaluator().evaluate("1+a").left.index
        )
        assertEquals(
            0,
            evaluate("+a", emptyMap()).left.index
        )
    }
}