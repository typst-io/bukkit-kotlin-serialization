package io.typst.bukkit.kotlin.serialization.expression

data class Token(
    val tokenType: TokenType,
    val lexeme: String,
    val start: Int,
    val end: Int,
) {
    companion object {
        fun ofEOF(size: Int): Token = Token(TokenType.EOF, "", size, size)
    }
}