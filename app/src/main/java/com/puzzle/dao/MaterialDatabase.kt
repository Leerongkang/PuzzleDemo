package com.puzzle.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puzzle.material.Material

@Database(entities = [Material::class], version = 1)
abstract class MaterialDatabase : RoomDatabase() {

    abstract fun materialDao(): MaterialDao

}

