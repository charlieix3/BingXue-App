package com.example.assignmentexample.data.Address

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userOwnerId: String,
    val addressLine1: String,
    val city: String,
    val postcode: String,
    val state: String,
    val isDefault: Boolean = false
)