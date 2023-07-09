# bukkit-kotlin-serialization

A [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization#kotlin-multiplatform--multi-format-reflectionless-serialization) extension for Bukkit serializable objects

## Usage

No boilerplate, the static config parser will be generated automatically.

```kotlin
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

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
        this.myConfig = Yaml.default.decodeFromString(File(dataFolder, "config.yml").readText())
        // or Json.decodeFromString
    }

    override fun onDisable(): Unit {
        File(dataFolder, "config.yml").writeText(Yaml.default.encodeToString(this.myConfig))
        // or Json.encodeToString
    }
} 
```
