package io.typst.bukkit.kotlin.serialization

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

val JavaPlugin.configJsonFile: File get() = File(dataFolder, "config.json")
val JavaPlugin.configYamlFile: File get() = File(dataFolder, "config.yml")

val bukkitPluginJson: Json by lazy {
    Json {
        encodeDefaults = true
        prettyPrint = true
        ignoreUnknownKeys = true
    }
}
val bukkitPluginYaml: Yaml by lazy {
    Yaml(configuration = YamlConfiguration(strictMode = false))
}

inline fun <reified A> JavaPlugin.readConfigOrCreate(defaultValue: () -> A? = { null }, jsonOrYaml: Boolean = true): A {
    val configFile = if (jsonOrYaml) {
        configJsonFile
    } else configYamlFile
    val serialFormat = if (jsonOrYaml) {
        bukkitPluginJson
    } else bukkitPluginYaml
    if (configFile.isFile) {
        if (jsonOrYaml) {
            return serialFormat.decodeFromString<A>(configFile.readText())
        } else {
            return serialFormat.decodeFromString<A>(configFile.readText())
        }
    } else {
        configFile.parentFile.mkdirs()
        val defValue = defaultValue() ?: serialFormat.decodeFromString<A>("{}")
        configFile.writeText(serialFormat.encodeToString<A>(defValue))
        return defValue
    }
}
