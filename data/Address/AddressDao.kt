package com.example.assignmentexample.data.Address

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Query("SELECT * FROM addresses WHERE userOwnerId = :userId ORDER BY isDefault DESC")
    fun getAllAddresses(userId: String): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = 0 WHERE isDefault = 1 AND userOwnerId = :userId")
    suspend fun clearUserDefaults(userId: String)
}