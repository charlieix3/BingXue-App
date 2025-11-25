package com.example.assignmentexample.data.Cart

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.assignmentexample.data.Drink.DrinkEntity

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = DrinkEntity::class,
            parentColumns = ["drinkId"],
            childColumns = ["drinkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["drinkId"])]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userOwnerId: String,
    val drinkId: Int,
    val quantity: Int,
    val sweetness: String,
    val size: String,
    val ice: String,
    val totalPrice: Double
)