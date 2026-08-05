package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PhotoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE isInTrash = 0 ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isFavorite = 1 AND isInTrash = 0 ORDER BY timestamp DESC")
    fun getFavoritePhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isInTrash = 1 ORDER BY timestamp DESC")
    fun getTrashPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): PhotoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem): Long

    @Update
    suspend fun updatePhoto(photo: PhotoItem)

    @Query("UPDATE photos SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE photos SET isInTrash = 1 WHERE id = :id")
    suspend fun moveToTrash(id: Long)

    @Query("UPDATE photos SET isInTrash = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM photos WHERE isInTrash = 1")
    suspend fun emptyTrash()
}
