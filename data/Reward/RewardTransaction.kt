package com.example.assignmentexample.data.Reward

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "reward_transactions")
data class RewardTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userOwnerId: String,
    val rewardName: String,
    val pointsChange: Int,
    val transactionDate: Long = System.currentTimeMillis()
) {
    //formatted string for date
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mma", Locale.getDefault())
        return sdf.format(Date(transactionDate))
    }
}