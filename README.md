# bukkit-kotlin-serialization

A [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization#kotlin-multiplatform--multi-format-reflectionless-serialization) extension for Bukkit serializable objects

## Usage

Gradle: 
```
repositories {
    mavenCentral()
}

dependencies {
    implementation('io.typst:bukkit-kotlin-serialization:1.3.0')
}
```

No boilerplate, the static config parser will be generated automatically.

```kotlin
import io.typst.bukkit.kotlin.serialization.ItemStackSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MyConfig(
    val name: String,
    // typealias ItemStackSerializable = @Serializable(ItemStackSerializer::class) ItemStack 
    val item: ItemStackSerializable?
)

class MyPlugin : JavaPlugin() {
    var myConfig: MyConfig = MyConfig("", null)
    val configJson: Json = Json { prettyPrint = true; encodeDefaults = true }

    override fun onEnable(): Unit {
        this.myConfig = configJson.decodeFromString(File(dataFolder, "config.json").readText())
        // or Json.decodeFromString
    }

    override fun onDisable(): Unit {
        File(dataFolder, "config.json").writeText(configJson.encodeToString(this.myConfig))
        // or Json.encodeToString
    }
} 
```
