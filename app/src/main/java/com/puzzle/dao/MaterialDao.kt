package com.puzzle.dao

import androidx.room.*
import com.puzzle.material.Material

@Dao
interface MaterialDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMaterials(vararg materials: Material)

    @Update
    suspend fun updateMaterials(vararg materials: Material)

    @Delete
    suspend fun deleteMaterial(vararg materials: Material)

    @Query("SELECT * FROM material WHERE categoryType = :type ")
    suspend fun queryMaterial(type: Int): List<Material>
}