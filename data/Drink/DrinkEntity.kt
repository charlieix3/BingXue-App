package com.example.assignmentexample.data.Drink

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drinks")
data class DrinkEntity(
    @PrimaryKey(autoGenerate = true)
    val drinkId: Int = 0,
    val name: String,
    val chinese: String,
    val price: Double,
    val imageRes: Int,
    val category: String,
    val pointsValue: Int
)