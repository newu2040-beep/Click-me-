package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PhotoItem
import com.example.data.model.PresetItem

@Database(entities = [PhotoItem::class, PresetItem::class], version = 1, exportSchema = false)
abstract class ClickitDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun presetDao(): PresetDao

    companion object {
        @Volatile
        private var INSTANCE: ClickitDatabase? = null

        fun getDatabase(context: Context): ClickitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClickitDatabase::class.java,
                    "clickit_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
