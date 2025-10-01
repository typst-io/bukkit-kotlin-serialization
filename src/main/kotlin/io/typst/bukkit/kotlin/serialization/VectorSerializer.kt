package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

typealias VectorSerializable = @Serializable(VectorSerializer::class) Vector

class VectorSerializer : KSerializer<Vector> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.bukkit.util.Vector", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Vector) =
        encoder.encodeString("${value.x},${value.y},${value.z}")

    override fun deserialize(decoder: Decoder): Vector {
        val pieces = decoder.decodeString().split(",")
        return Vector(
            pieces[0].toDouble(),
            pieces[1].toDouble(),
            pieces[2].toDouble()
        )
    }
}
