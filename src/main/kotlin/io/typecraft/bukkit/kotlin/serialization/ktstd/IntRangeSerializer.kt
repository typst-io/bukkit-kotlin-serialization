package io.typecraft.bukkit.kotlin.serialization.ktstd

import io.typecraft.bukkit.kotlin.serialization.substringOrEmpty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias IntRangeAsString = @Serializable(IntRangeSerializer::class) IntRange

// 0..1 -> [0, 1]
internal fun serializeIntRange(xs: IntRange): String {
    return "[${xs.first}, ${xs.last}]"
}

// [0, 1] -> 0..1
internal fun deserializeIntRange(xs: String): IntRange {
    val pieces = xs.split(",").map { it.trim() }
    val leftInt = pieces[0].substringOrEmpty(1).toIntOrNull()
    val right = pieces.getOrNull(1)
    val rightInt = right?.substringOrEmpty(0, right.length - 1)?.toIntOrNull()
    if (leftInt == null || rightInt == null) {
        return IntRange.EMPTY
    }
    return leftInt..rightInt
}

object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): IntRange {
        return deserializeIntRange(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: IntRange) {
        encoder.encodeString(serializeIntRange(value))
    }
}