package io.typecraft.bukkit.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

typealias VectorSerializable = @Serializable(VectorSerializer::class) Vector

class VectorSerializer : KSerializer<Vector> {
    override val descriptor: SerialDescriptor
        get() = ConfigSerializableSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Vector) =
        encoder.encodeSerializableValue(ConfigSerializableSerializer, value)

    override fun deserialize(decoder: Decoder): Vector {
        val serializable = decoder.decodeSerializableValue(ConfigSerializableSerializer)
        return serializable as Vector
    }
}
