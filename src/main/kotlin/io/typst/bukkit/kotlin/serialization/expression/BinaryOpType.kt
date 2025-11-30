package io.typst.bukkit.kotlin.serialization.expression

enum class BinaryOpType(val bindingPowers: Pair<Int, Int>) {
    PLUS(10 to 11),
    MINUS(10 to 11),
    MULTIPLY(20 to 21),
    DIVIDE(20 to 21),
    POW(20 to 21),
    ;

    companion object {
        fun from(ch: Char): BinaryOpType? {
            return when (ch) {
                '+' -> PLUS
                '-' -> MINUS
                '*' -> MULTIPLY
                '/' -> DIVIDE
                '^' -> POW
                else -> null
            }
        }
    }
}