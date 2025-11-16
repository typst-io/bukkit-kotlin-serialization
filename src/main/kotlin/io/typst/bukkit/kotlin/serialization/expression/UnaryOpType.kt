package io.typst.bukkit.kotlin.serialization.expression

enum class UnaryOpType(val bindingPower: Int) {
    PLUS(100), MINUS(100),
    ;

    companion object {
        fun from(ch: Char): UnaryOpType? {
            return when (ch) {
                '+' -> PLUS
                '-' -> MINUS
                else -> null
            }
        }
    }
}