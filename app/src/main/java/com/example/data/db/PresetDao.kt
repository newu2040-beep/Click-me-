package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PresetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY timestamp DESC")
    fun getAllPresets(): Flow<List<PresetItem>>

    @Query("SELECT * FROM presets WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoritePresets(): Flow<List<PresetItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetItem): Long

    @Update
    suspend fun updatePreset(preset: PresetItem)

    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deletePreset(id: Long)
}
