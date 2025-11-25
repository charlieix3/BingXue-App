package com.example.assignmentexample.data.History

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: OrderHistoryEntity)

    @Delete
    suspend fun deleteOrder(order: OrderHistoryEntity)

    @Query("SELECT * FROM order_history WHERE userOwnerId = :userId ORDER BY orderDate DESC")
    fun getAllOrders(userId: String): Flow<List<OrderHistoryEntity>>
}