package com.example.assignmentexample.data.CreditCard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_methods")
data class CreditCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userOwnerId: String,
    val cardHolderName: String,
    val cardNumberLast4: String,
    val cardExpiryDate: String
)