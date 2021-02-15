package com.puzzle.dao

import android.content.Context
import androidx.room.Room
import com.puzzle.app

object MaterialDB {

    private var INSTANCE: MaterialDatabase? = null
    private const val dataBaseName = "material_database"

    @Synchronized
    fun getDatabase(context: Context = app): MaterialDatabase {
        return INSTANCE ?: Room.databaseBuilder(
                                    context.applicationContext,
                                    MaterialDatabase::class.java,
                                    dataBaseName
                                ).build()
                                 .apply {
                                     INSTANCE = this
                                 }
    }
}