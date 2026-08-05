package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class PresetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isFavorite: Boolean = false,
    val filterType: String = "Classic Film",
    val intensity: Float = 1.0f,
    val grain: Float = 0.2f,
    val fade: Float = 0.1f,
    val temperature: Float = 0.0f,
    val tint: Float = 0.0f,
    val contrast: Float = 1.0f,
    val highlights: Float = 0.0f,
    val shadows: Float = 0.0f,
    val saturation: Float = 1.0f,
    val vignette: Float = 0.1f,
    val bloom: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis()
)
