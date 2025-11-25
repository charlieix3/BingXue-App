package com.example.assignmentexample.data.UserPoints

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPointsDao {
    @Upsert
    suspend fun upsertUser(user: UserPoints)


    @Query("SELECT * FROM user_points WHERE userId = :userId")
    fun getUserPoints(userId: String): Flow<UserPoints?>
}