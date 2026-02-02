package com.guerramath.safetyapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SafetyDao {

    @Query("SELECT * FROM safety_evaluations ORDER BY timestamp DESC")
    fun getAllEvaluations(): Flow<List<SafetyEntity>>

    @Query("SELECT * FROM safety_evaluations WHERE pilotName = :pilotName ORDER BY timestamp DESC")
    fun getEvaluationsByPilot(pilotName: String): Flow<List<SafetyEntity>>

    @Query("SELECT * FROM safety_evaluations WHERE id = :id")
    suspend fun getEvaluationById(id: Long): SafetyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evaluation: SafetyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(evaluations: List<SafetyEntity>): List<Long>

    @Delete
    suspend fun delete(evaluation: SafetyEntity): Int

    @Query("DELETE FROM safety_evaluations")
    suspend fun deleteAll(): Int
}