package com.example.assignmentexample.data

import android.content.Context

class SharedPrefHelper(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveCategory(category: String) {
        prefs.edit().putString("selected_category", category).apply()
    }

    fun getCategory(): String = prefs.getString("selected_category", "All") ?: "All"
}