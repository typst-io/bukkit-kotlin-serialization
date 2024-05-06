package io.typst.bukkit.kotlin.serialization.time

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration
import java.time.Period

typealias PeriodAsString = @Serializable(PeriodSerializer::class) Period
object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JavaPeriod", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Period {
        return Period.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }
}