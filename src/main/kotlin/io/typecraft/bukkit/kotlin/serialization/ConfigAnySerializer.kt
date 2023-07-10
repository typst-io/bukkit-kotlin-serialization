package io.typecraft.bukkit.kotlin.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal object ConfigAnySerializer : KSerializer<Any?> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("BukkitConfigAny", PolymorphicKind.OPEN)

    override fun deserialize(decoder: Decoder): Any? {
        val jsonDecoder = decoder as? JsonDecoder ?: return null
        val element = jsonDecoder.decodeJsonElement()
        return exactValue(element)
    }

    private fun exactValue(x: JsonElement): Any? =
        when (x) {
            is JsonPrimitive ->
                if (x.isString) {
                    x.content
                } else x.intOrNull ?: x.longOrNull ?: x.double

            JsonNull -> null
            is JsonArray -> x.map(::exactValue)
            is JsonObject -> {
                val map = x.map { (k, v) ->
                    k to exactValue(v)
                }.toMap()
                if (map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    ConfigurationSerialization.deserializeObject(map)
                } else map
            }
        }

    override fun serialize(encoder: Encoder, value: Any?) {
        if (value == null) {
            encoder.encodeNull()
            return
        }
        val roundedValue = roundValue(value)
        val serializer = findSerializer(roundedValue)
        encoder.encodeSerializableValue(serializer, roundedValue)
    }

    private fun roundValue(x: Any): Any =
        if (x is Number) {
            if (x is Double || x is Float) {
                x.toDouble()
            } else x.toLong()
        } else if (x is StringBuilder || x is StringBuffer) {
            x.toString()
        } else if (x is Collection<*>) {
            x.toList()
        } else if (x is Map<*, *>) {
            LinkedHashMap(x)
        } else x

    @Suppress("UNCHECKED_CAST")
    private fun findSerializer(x: Any): KSerializer<Any> =
        when (x) {
            is Boolean -> Boolean.serializer()
            is Double -> Double.serializer()
            is Long -> Long.serializer()
            is String -> String.serializer()
            is Map<*, *> ->
                if (x.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    ConfigSerializableSerializer
                } else MapSerializer(String.serializer(), ConfigAnySerializer)

            is Collection<*> -> ListSerializer(ConfigAnySerializer)
            is ConfigurationSerializable -> ConfigSerializableSerializer
            else -> throw IllegalArgumentException("Unknown type ${x.javaClass.name} $x")
        } as KSerializer<Any>
}
