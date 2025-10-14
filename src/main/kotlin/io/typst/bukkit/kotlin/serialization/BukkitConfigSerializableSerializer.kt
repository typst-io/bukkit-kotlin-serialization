package io.typst.bukkit.kotlin.serialization

import com.charleskorn.kaml.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

private val ROOT = YamlPath.root

object BukkitConfigSerializableSerializer : KSerializer<ConfigurationSerializable> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("BukkitConfigurationSerializable")

    fun serialize(value: ConfigurationSerializable): Map<String, Any?> {
        val map: MutableMap<String, Any?> = value.serialize()
        val alias = ConfigurationSerialization.getAlias(value.javaClass)
        return map + (ConfigurationSerialization.SERIALIZED_TYPE_KEY to alias)
    }

    override fun serialize(encoder: Encoder, value: ConfigurationSerializable) {
        val bukkitMap = serialize(value)
        when (encoder) {
            is JsonEncoder -> encoder.encodeJsonElement(mapToJson(bukkitMap))
            else -> {
                val node: YamlNode = mapToYaml(bukkitMap)
                encoder.encodeSerializableValue(YamlNode.serializer(), node)
            }
        }
    }

    fun deserializeObject(map: Map<String, Any?>): ConfigurationSerializable {
        val newMap = map.toMutableMap()
        for ((k, v) in map.entries) {
            if (v == null) continue
            if (v is Map<*, *> && v.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                val obj = deserializeObject(v.map {
                    it.key.toString() to it.value
                }.toMap())
                newMap[k] = obj
            }
        }
        return ConfigurationSerialization.deserializeObject(newMap)
            ?: error("Failed to deserialize ConfigurationSerializable (missing '==' alias?)")
    }

    override fun deserialize(decoder: Decoder): ConfigurationSerializable {
        val map: Map<String, Any?> = when (decoder) {
            is JsonDecoder -> {
                val elem = decoder.decodeJsonElement()
                (jsonToAny(elem) as Map<String, Any?>)
            }

            else -> {
                val node: YamlNode = decoder.decodeSerializableValue(YamlNode.serializer())
                (yamlToAny(node) as Map<String, Any?>)
            }
        }
        return deserializeObject(map)
    }

    // -------------------- JSON <-> Any --------------------
    private fun mapToJson(map: Map<String, *>): JsonObject = buildJsonObject {
        for ((k, v) in map) put(k, anyToJson(v))
    }

    private fun listToJson(list: List<*>): JsonArray = JsonArray(list.map { anyToJson(it) })

    private fun anyToJson(v: Any?): JsonElement = when (v) {
        null -> JsonNull
        is JsonElement -> v
        is String -> JsonPrimitive(v)
        is Boolean -> JsonPrimitive(v)
        is Number -> JsonPrimitive(v)
        is Map<*, *> -> mapToJson(v as Map<String, *>)
        is List<*> -> listToJson(v)
        is ConfigurationSerializable -> mapToJson(serialize(v))
        else -> JsonPrimitive(v.toString())
    }

    private fun jsonToAny(elem: JsonElement): Any? = when (elem) {
        is JsonNull -> null
        is JsonPrimitive -> when {
            elem.isString -> elem.content
            elem.booleanOrNull != null -> elem.boolean
            elem.longOrNull != null -> if (elem.content.contains('.')) elem.double else elem.long
            else -> elem.content
        }

        is JsonObject -> elem.mapValues { jsonToAny(it.value) }
        is JsonArray -> elem.map { jsonToAny(it) }
    }

    // -------------------- YAML(KAML 0.96) <-> Any --------------------
    private fun mapToYaml(map: Map<String, *>): YamlMap =
        YamlMap(map.map { (k, v) -> YamlScalar(k, ROOT) to anyToYaml(v) }.toMap(), ROOT)

    private fun listToYaml(list: List<*>): YamlList =
        YamlList(list.map { anyToYaml(it) }, ROOT)

    private fun anyToYaml(v: Any?): YamlNode = when (v) {
        null -> YamlNull(ROOT)
        is YamlNode -> v
        is String -> YamlScalar(v, ROOT)
        is Boolean -> YamlScalar(v.toString(), ROOT) // 스칼라는 문자열 컨텐트
        is Number -> YamlScalar(v.toString(), ROOT)
        is Map<*, *> -> mapToYaml(v as Map<String, *>)
        is List<*> -> listToYaml(v)
        is ConfigurationSerializable -> mapToYaml(serialize(v))
        else -> YamlScalar(v.toString(), ROOT)
    }

    private fun yamlToAny(node: YamlNode): Any? = when (node) {
        is YamlNull -> null
        is YamlScalar -> parseYamlScalar(node.content)
        is YamlList -> node.items.map { yamlToAny(it) }
        is YamlMap -> node.entries
            .mapKeys { (k, _) -> (yamlToAny(k) ?: "").toString() }
            .mapValues { (_, v) -> yamlToAny(v) }

        is YamlTaggedNode -> {
            yamlToAny(node.innerNode)
        }
    }

    private fun parseYamlScalar(s: String): Any {
        val lower = s.lowercase()
        if (lower == "true" || lower == "false") return lower == "true"
        val num = s.replace("_", "")
        num.toLongOrNull()?.let { return it }
        num.toDoubleOrNull()?.let { return it }
        return s
    }
}
