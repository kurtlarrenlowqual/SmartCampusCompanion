package com.example.smartcampuscompanion.data.local


import android.content.Context
import androidx.room.Room


object DbProvider {
    @Volatile private var INSTANCE: AppDatabase? = null


    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "smart_campus_db"
            ).build()
            INSTANCE = db
            db
        }
    }
}
