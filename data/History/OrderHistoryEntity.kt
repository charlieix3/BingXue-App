package com.example.assignmentexample.data.History

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.assignmentexample.data.Drink.DrinkEntity
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "order_history",
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
data class OrderHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userOwnerId: String,
    val orderId: String,
    val drinkId: Int,
    val details: String,
    val quantity: Int,
    val price: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val paymentMethod: String
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mma", Locale.getDefault())
        return sdf.format(Date(orderDate))
    }
}