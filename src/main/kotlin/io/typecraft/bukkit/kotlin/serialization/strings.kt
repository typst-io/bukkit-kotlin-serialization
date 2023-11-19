package io.typecraft.bukkit.kotlin.serialization

internal fun String.substringOrEmpty(x: Int): String =
    if (x <= length) {
        substring(x)
    } else ""

internal fun String.substringOrEmpty(start: Int, endExclusive: Int): String {
    val range = 0..length
    return if (start !in range || endExclusive !in range) {
        ""
    } else substring(start, endExclusive)
}