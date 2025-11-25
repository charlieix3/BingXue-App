package com.example.assignmentexample.data.CreditCard

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Query("SELECT * FROM payment_methods WHERE userOwnerId = :userId")
    fun getSavedCreditCards(userId: String): Flow<List<CreditCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentMethod: CreditCardEntity)

    @Query("SELECT * FROM payment_methods WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CreditCardEntity?

    @Delete
    suspend fun delete(paymentMethod: CreditCardEntity)


    @Query("DELETE FROM payment_methods WHERE userOwnerId = :userId")
    suspend fun clearCards(userId: String)
}