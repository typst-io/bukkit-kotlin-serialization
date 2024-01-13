package io.typst.bukkit.kotlin.serialization.ktstd

import io.typst.bukkit.kotlin.serialization.substringOrEmpty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


typealias LongRangeAsString = @Serializable(LongRangeSerializer::class) LongRange

// 0..1 -> [0, 1]
private fun serializeLongRange(xs: LongRange): String {
    return "[${xs.first}, ${xs.last}]"
}

// [0, 1] -> 0..1
private fun deserializeLongRange(xs: String): LongRange {
    val pieces = xs.split(",").map { it.trim() }
    val leftNum = pieces.getOrNull(0)?.substringOrEmpty(1)?.toLongOrNull()
    val right = pieces.getOrNull(1)
    val rightNum = right?.substringOrEmpty(0, right.length - 1)?.toLongOrNull()
    if (leftNum == null || rightNum == null) {
        return LongRange.EMPTY
    }
    return leftNum..rightNum
}

object LongRangeSerializer : KSerializer<LongRange> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LongRange", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LongRange {
        return deserializeLongRange(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: LongRange) {
        encoder.encodeString(serializeLongRange(value))
    }
}