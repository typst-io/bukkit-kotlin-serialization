
package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack

typealias ItemStackSerializable = @Serializable(ItemStackSerializer::class) ItemStack

class ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor
        get() = ConfigSerializableSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) =
        encoder.encodeSerializableValue(ConfigSerializableSerializer, value)

    override fun deserialize(decoder: Decoder): ItemStack {
        val serializable = decoder.decodeSerializableValue(ConfigSerializableSerializer)
        return serializable as ItemStack
    }
}
