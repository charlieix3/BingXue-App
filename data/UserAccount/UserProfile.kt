package com.example.assignmentexample.data

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val age: String? = null,
    val gender: String? = null,
    val phone: String? = null,
)