package io.typst.bukkit.kotlin.serialization.expression

import io.typst.bukkit.kotlin.serialization.expression.Expression.*
import io.vavr.control.Either
import kotlin.math.*

fun Expression.evaluate(env: Map<String, Double>): Either<Failure, Double> {
    return Evaluator(env).evaluate(this)
}

fun evaluate(xs: String, env: Map<String, Double>): Either<Failure, Double> =
    Evaluator(env).evaluate(xs)

data class Evaluator(
    val env: Map<String, Double> = emptyMap(),
) {
    fun evaluate(xs: String): Either<Failure, Double> {
        return Lexer.lexAll(xs).flatMap {
            Parser.parse(it)
        }.flatMap {
            evaluate(it)
        }
    }

    fun evaluate(expr: Expression): Either<Failure, Double> {
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
                val valueEither = evaluate(expr.operand)
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
                val leftEither = evaluate(expr.left)
                if (leftEither is Either.Left) {
                    return leftEither
                }
                val rightEither = evaluate(expr.right)
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

                    BinaryOpType.POW -> left.pow(right)
                }
                return Either.right(result)
            }

            is FunctionCall -> {
                val functionType = FunctionType.registry[expr.name]
                    ?: return Either.left(Failure("Unknown function: ${expr.name}", 0))
                if (expr.arguments.size != functionType.argumentSize) {
                    return Either.left(
                        Failure(
                            "Expected function argument size ${functionType.argumentSize} but ${expr.arguments.size}",
                            0
                        )
                    )
                }
                return when (functionType) {
                    FunctionType.MIN ->
                        evaluate(expr.arguments[0])
                            .flatMap { a ->
                                evaluate(expr.arguments[1]).map { b ->
                                    min(a, b)
                                }
                            }

                    FunctionType.MAX ->
                        evaluate(expr.arguments[0])
                            .flatMap { a ->
                                evaluate(expr.arguments[1]).map { b ->
                                    max(a, b)
                                }
                            }

                    FunctionType.LOG ->
                        evaluate(expr.arguments[0])
                            .flatMap { a ->
                                evaluate(expr.arguments[1]).map { b ->
                                    log(a, b)
                                }
                            }

                    FunctionType.LOG10 ->
                        evaluate(expr.arguments[0]).map(::log10)

                    FunctionType.SQRT ->
                        evaluate(expr.arguments[0]).map(::sqrt)
                }
            }
        }
    }
}