# bukkit-kotlin-serialization

A [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization#kotlin-multiplatform--multi-format-reflectionless-serialization) extension for Bukkit serializable objects

## Usage

Gradle: 
```
repositories {
    mavenCentral()
}

dependencies {
    implementation('io.typst:bukkit-kotlin-serialization:1.0.0')
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

    override fun onEnable(): Unit {
        this.myConfig = Json.default.decodeFromString(File(dataFolder, "config.json").readText())
        // or Json.decodeFromString
    }

    override fun onDisable(): Unit {
        File(dataFolder, "config.json").writeText(Json.default.encodeToString(this.myConfig))
        // or Json.encodeToString
    }
} 
```
