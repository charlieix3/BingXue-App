package com.example.assignmentexample.data.Reward

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardTransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: RewardTransaction)

    @Query("SELECT * FROM reward_transactions WHERE userOwnerId = :userId ORDER BY transactionDate DESC")
    fun getAllTransactions(userId: String): Flow<List<RewardTransaction>>
}