package com.guerramath.safetyapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomChecklistDao {

    @Query("SELECT * FROM custom_checklists ORDER BY updatedAt DESC")
    fun getAllChecklists(): Flow<List<CustomChecklistEntity>>

    @Query("SELECT * FROM custom_checklists WHERE id = :id")
    suspend fun getChecklistById(id: String): CustomChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checklist: CustomChecklistEntity)

    @Update
    suspend fun update(checklist: CustomChecklistEntity)

    @Delete
    suspend fun delete(checklist: CustomChecklistEntity)

    @Query("DELETE FROM custom_checklists WHERE id = :id")
    suspend fun deleteById(id: String)
}