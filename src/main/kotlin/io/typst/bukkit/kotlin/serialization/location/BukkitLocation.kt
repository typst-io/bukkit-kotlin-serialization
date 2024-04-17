package io.typst.bukkit.kotlin.serialization.location

import kotlinx.serialization.Serializable

@Serializable
data class BukkitLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Double,
    val pitch: Double,
)
