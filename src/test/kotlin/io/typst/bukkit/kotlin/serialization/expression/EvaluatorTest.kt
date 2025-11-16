package io.typst.bukkit.kotlin.serialization.expression

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EvaluatorTest {
    @Test
    fun evaluate() {
        assertEquals(
            1.0,
            Evaluator.evaluate("1.0", emptyMap()).get()
        )
        assertEquals(
            1.0,
            Evaluator.evaluate(Expression.Literal(1.0), emptyMap()).get()
        )
        assertEquals(
            1.0,
            Expression.Literal(1.0).evaluate(emptyMap()).get()
        )
        assertEquals(
            1.0,
            Evaluator.evaluate(Expression.Variable("xa"), mapOf("xa" to 1.0)).get()
        )
        assertEquals(
            0,
            Evaluator.evaluate(Expression.Variable("xa"), emptyMap()).left.index
        )

        // unary
        assertEquals(
            1.0,
            Evaluator.evaluate(Expression.Unary(UnaryOpType.PLUS, Expression.Literal(1.0)), emptyMap()).get()
        )
        assertEquals(
            -1.0,
            Evaluator.evaluate(Expression.Unary(UnaryOpType.MINUS, Expression.Literal(1.0)), emptyMap()).get()
        )

        // binary
        assertEquals(
            3.0,
            Evaluator.evaluate(Expression.Binary(BinaryOpType.PLUS, Expression.Literal(1.0), Expression.Literal(2.0)), emptyMap()).get()
        )
        assertEquals(
            -1.0,
            Evaluator.evaluate(Expression.Binary(BinaryOpType.MINUS, Expression.Literal(1.0), Expression.Literal(2.0)), emptyMap()).get()
        )
        assertEquals(
            2.0,
            Evaluator.evaluate(Expression.Binary(BinaryOpType.MULTIPLY, Expression.Literal(1.0), Expression.Literal(2.0)), emptyMap()).get()
        )
        assertEquals(
            0.5,
            Evaluator.evaluate(Expression.Binary(BinaryOpType.DIVIDE, Expression.Literal(1.0), Expression.Literal(2.0)), emptyMap()).get()
        )
        assertEquals(
            0,
            Evaluator.evaluate("x", emptyMap()).left.index
        )
        assertEquals(
            0,
            Evaluator.evaluate("a+b", emptyMap()).left.index
        )
        assertEquals(
            0,
            Evaluator.evaluate("1+a", emptyMap()).left.index
        )
        assertEquals(
            0,
            evaluate("+a", emptyMap()).left.index
        )
    }
}