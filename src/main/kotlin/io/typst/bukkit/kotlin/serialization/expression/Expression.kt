package io.typst.bukkit.kotlin.serialization.expression

sealed interface Expression {
    data class Literal(val value: Double) : Expression
    data class Variable(val name: String) : Expression
    data class Unary(
        val op: UnaryOpType,
        val operand: Expression,
    ) : Expression

    data class Binary(
        val op: BinaryOpType,
        val left: Expression,
        val right: Expression,
    ) : Expression
}