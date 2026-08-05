package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val title: String,
    val isFavorite: Boolean = false,
    val isInTrash: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val filterApplied: String = "Natural",
    val iso: String = "ISO 100",
    val shutterSpeed: String = "1/250s",
    val focalLength: String = "26mm",
    val aperture: String = "f/1.8",
    val width: Int = 4000,
    val height: Int = 3000,
    val fileSizeMb: Double = 3.5,
    val location: String = "Captured with Clickit",
    val isRaw: Boolean = false,
    // Filter / Edit parameter JSON or String representation
    val editParamsJson: String = ""
)
