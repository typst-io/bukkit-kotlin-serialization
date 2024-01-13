package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Location

typealias LocationSerializable = @Serializable(LocationSerializer::class) Location

class LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor
        get() = ConfigSerializableSerializer.descriptor

    override fun deserialize(decoder: Decoder): Location {
        val serializable = decoder.decodeSerializableValue(ConfigSerializableSerializer)
        return serializable as Location
    }

    override fun serialize(encoder: Encoder, value: Location) =
        encoder.encodeSerializableValue(ConfigSerializableSerializer, value)
}
