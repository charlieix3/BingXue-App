package com.example.assignmentexample.data.Cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)


    @Query("SELECT * FROM cart_items WHERE userOwnerId = :userId")
    fun getAllItemsFlow(userId: String): Flow<List<CartItemEntity>>


    @Query("DELETE FROM cart_items WHERE userOwnerId = :userId")
    suspend fun clearCart(userId: String)

}
