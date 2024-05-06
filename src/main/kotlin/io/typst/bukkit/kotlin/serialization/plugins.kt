package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

val JavaPlugin.configJsonFile: File get() = File(dataFolder, "config.json")

val bukkitPluginJson: Json by lazy {
    Json {
        encodeDefaults = true
        prettyPrint = true
    }
}

inline fun <reified A> JavaPlugin.readConfigOrCreate(defaultValue: () -> A? = { null }): A {
    val configFile = configJsonFile
    if (configFile.isFile) {
        return bukkitPluginJson.decodeFromString<A>(configJsonFile.readText())
    } else {
        configFile.parentFile.mkdirs()
        val defValue = defaultValue() ?: bukkitPluginJson.decodeFromString<A>("{}")
        configFile.writeText(bukkitPluginJson.encodeToString<A>(defValue))
        return defValue
    }
}
