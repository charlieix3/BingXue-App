package com.example.assignmentexample.data.History

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignmentexample.data.UserViewModel

//update the factory
class OrderHistoryViewModelFactory(
    private val application: Application,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderHistoryViewModel(application, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}