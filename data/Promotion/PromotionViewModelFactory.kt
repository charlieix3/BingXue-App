package com.example.assignmentexample.data.Promotion

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignmentexample.data.UserViewModel

class PromotionViewModelFactory(private val application: Application,private val userViewModel: UserViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromotionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromotionViewModel(application,userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}