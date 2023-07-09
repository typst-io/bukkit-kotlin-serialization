@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package io.typecraft.bukkit.kotlin.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

fun encodeBukkitObjectToJsonElement(x: Any): JsonElement =
    when (x) {
        is Boolean -> JsonPrimitive(x)
        is Number -> JsonPrimitive(x)
        is String -> JsonPrimitive(x)
        is Map<*, *> -> JsonObject(
            x.mapNotNull { (k, v) ->
                if (k != null && v != null) {
                    k.toString() to encodeBukkitObjectToJsonElement(v)
                } else null
            }.toMap()
        )

        is Collection<*> -> JsonArray(
            x.mapNotNull {
                if (it != null) {
                    encodeBukkitObjectToJsonElement(it)
                } else null
            }.toList()
        )

        is Array<*> -> JsonArray(
            x.mapNotNull {
                if (it != null) {
                    encodeBukkitObjectToJsonElement(it)
                } else null
            }.toList()
        )

        is ConfigurationSerializable -> {
            val map = x.serialize()
            val alias = ConfigurationSerialization.getAlias(x.javaClass)
            encodeBukkitObjectToJsonElement(
                map + (ConfigurationSerialization.SERIALIZED_TYPE_KEY to alias)
            )
        }

        else -> throw IllegalArgumentException("Unknown type ${x.javaClass.name} $x")
    }

fun decodeBukkitObjectFromJsonElement(x: JsonElement): Any? {
    return when (x) {
        is JsonArray -> x.map {
            decodeBukkitObjectFromJsonElement(it)
        }.toList()

        is JsonObject -> {
            val map: Map<String, Any?> = x.map { (k, v) ->
                k to decodeBukkitObjectFromJsonElement(v)
            }.toMap()
            val bukkitObject = ConfigurationSerialization.deserializeObject(map)
            bukkitObject ?: map
        }

        JsonNull -> null

        is JsonPrimitive -> {
            return if (x.isString) {
                x.content
            } else x.intOrNull ?: x.longOrNull ?: x.doubleOrNull
        }

    }
}

object ConfigSerializableSerializer : KSerializer<ConfigurationSerializable> {
    private val serializer: KSerializer<Map<String, JsonElement>> = MapSerializer(
        String.serializer(),
        serializer<JsonElement>()
    )
    override val descriptor: SerialDescriptor
        get() = serializer.descriptor

    override fun serialize(encoder: Encoder, value: ConfigurationSerializable) {
        val map = value.serialize().map { (k, v) ->
            k to encodeBukkitObjectToJsonElement(v)
        }.toMap()
        val alias = ConfigurationSerialization.getAlias(value.javaClass)
        return serializer.serialize(
            encoder,
            map + (ConfigurationSerialization.SERIALIZED_TYPE_KEY to JsonPrimitive(alias))
        )
    }

    override fun deserialize(decoder: Decoder): ConfigurationSerializable {
        val map = serializer.deserialize(decoder).map { (k, v) ->
            k to decodeBukkitObjectFromJsonElement(v)
        }.toMap()
        return ConfigurationSerialization.deserializeObject(map)
            ?: throw IllegalStateException(map.toString())
    }
}
