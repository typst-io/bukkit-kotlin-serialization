# bukkit-kotlin-serialization

A [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization#kotlin-multiplatform--multi-format-reflectionless-serialization) extension for Bukkit serializable objects

## Usage

No boilerplate, the static config parser will be generated automatically.

```kotlin
@Serializable
data class MyConfig(
    val name: String,
    @Serializable(ItemStackSerializer::class)
    val item: ItemStack?
    // or shortcut `val item: ItemStackSerializable?`
)

class MyPlugin : JavaPlugin() {
    var myConfig: MyConfig = MyConfig("", null)

    override fun onEnable(): Unit {
        Yaml.default.decodeFromString(File(dataFolder, "config.yml").readText())
        // or Json.decodeFromString
    }

    override fun onDisable(): Unit {
        File(dataFolder, "config.yml").writeText(Yaml.default.encodeToString(myConfig))
        // or Json.encodeToString
    }
} 
```
