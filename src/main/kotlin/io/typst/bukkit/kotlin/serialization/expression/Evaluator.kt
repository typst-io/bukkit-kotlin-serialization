package io.typst.bukkit.kotlin.serialization.expression

import io.typst.bukkit.kotlin.serialization.expression.Expression.*
import io.vavr.control.Either

fun Expression.evaluate(env: Map<String, Double>): Either<Failure, Double> {
    return Evaluator.evaluate(this, env)
}

fun evaluate(xs: String, env: Map<String, Double>): Either<Failure, Double> =
    Evaluator.evaluate(xs, env)

object Evaluator {
    fun evaluate(xs: String, env: Map<String, Double>): Either<Failure, Double> =
        Lexer.lexAll(xs).flatMap {
            Parser.parse(it)
        }.flatMap {
            evaluate(it, env)
        }

    fun evaluate(expr: Expression, env: Map<String, Double>): Either<Failure, Double> {
        when (expr) {
            is Literal -> {
                return Either.right(expr.value)
            }

            is Variable -> {
                val v = env[expr.name]
                return if (v != null) {
                    Either.right(v)
                } else {
                    Either.left(Failure("Unknown variable: ${expr.name} in env: $env", 0))
                }
            }

            is Unary -> {
                val valueEither = evaluate(expr.operand, env)
                if (valueEither is Either.Left) {
                    return valueEither
                }
                val value = valueEither.get()
                return when (expr.op) {
                    UnaryOpType.PLUS -> Either.right(+value)
                    UnaryOpType.MINUS -> Either.right(-value)
                }
            }

            is Binary -> {
                val leftEither = evaluate(expr.left, env)
                if (leftEither is Either.Left) {
                    return leftEither
                }
                val rightEither = evaluate(expr.right, env)
                if (rightEither is Either.Left) {
                    return rightEither
                }
                val left = leftEither.get()
                val right = rightEither.get()

                val result = when (expr.op) {
                    BinaryOpType.PLUS -> left + right
                    BinaryOpType.MINUS -> left - right
                    BinaryOpType.MULTIPLY -> left * right
                    BinaryOpType.DIVIDE -> if (right == 0.0) {
                        return Either.left(Failure("Division by zero", 0))
                    } else {
                        left / right
                    }
                }
                return Either.right(result)
            }
        }
    }
}