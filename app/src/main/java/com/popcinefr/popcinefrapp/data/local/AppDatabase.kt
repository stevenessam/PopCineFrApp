package com.popcinefr.popcinefrapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.popcinefr.popcinefrapp.data.local.dao.FavoritesDao
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity

// @Database tells Room what tables exist and what version the schema is
@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Room will generate the implementation of this automatically
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        // @Volatile means this value is always read from main memory
        // never from a thread's local cache — keeps it thread safe
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Only create the database once — reuse the same instance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "popcine_database"  // the name of the .db file on device
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}