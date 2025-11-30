package io.typst.bukkit.kotlin.serialization.expression

import kotlin.math.min

enum class FunctionType(val label: String, val argumentSize: Int) {
    MIN("min", 2),
    MAX("max", 2),
    LOG("log", 2),
    LOG10("log10", 1),
    SQRT("sqrt", 1),
    ;

    companion object {
        val registry: Map<String, FunctionType> = FunctionType.entries.associateBy { it.label }
    }
}