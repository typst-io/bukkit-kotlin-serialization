package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack

typealias ItemStackSerializable = @Serializable(ItemStackSerializer::class) ItemStack

object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = BukkitConfigSerializableSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) {
        BukkitConfigSerializableSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        return BukkitConfigSerializableSerializer.deserialize(decoder) as ItemStack
    }
}
