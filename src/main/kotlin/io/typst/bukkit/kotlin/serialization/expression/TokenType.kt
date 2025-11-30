package io.typst.bukkit.kotlin.serialization.expression

enum class TokenType {
    NUMBER,
    IDENTIFIER,
    OPERATOR,
    LEFT_PAREN,
    RIGHT_PAREN,
    EOF,
    COMMA,
}