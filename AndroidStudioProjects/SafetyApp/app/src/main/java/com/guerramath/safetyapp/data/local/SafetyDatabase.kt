package com.guerramath.safetyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SafetyEntity::class, CustomChecklistEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SafetyDatabase : RoomDatabase() {

    abstract fun safetyDao(): SafetyDao
    abstract fun customChecklistDao(): CustomChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: SafetyDatabase? = null

        fun getDatabase(context: Context): SafetyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafetyDatabase::class.java,
                    "safety_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}