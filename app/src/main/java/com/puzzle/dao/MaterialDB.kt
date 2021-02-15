package com.puzzle.dao

import android.content.Context
import androidx.room.Room
import com.puzzle.app

object MaterialDB {

    private const val dataBaseName = "material_database"

    val db by lazy {
        Room.databaseBuilder(
            app.applicationContext,
            MaterialDatabase::class.java,
            dataBaseName
        ).build()
    }
}