package io.typst.bukkit.kotlin.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

//@Serializable
//data class MyData(
//    val name: String,
//    val item: ItemStackSerializable?,
//    val loc: LocationSerializable?
//)

class TestPlugin : JavaPlugin() {
//    override fun onEnable() {
//        val item = ItemStack(Material.STONE).apply {
//            itemMeta = itemMeta?.apply {
//                displayName = "display"
//                lore = listOf("a", "b")
//            }
//        }
//        val data = MyData("test", item, Location(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0))
//        // json
//        val text = Json.encodeToString(data)
//        println(text)
//        val newData = Json.decodeFromString<MyData>(text)
//        assert(data == newData)
//        // TODO: yaml
//    }
}
