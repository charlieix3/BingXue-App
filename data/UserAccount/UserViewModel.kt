package com.example.assignmentexample.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.UserAccount.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {


    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.signUp(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Sign up failed. Please try again.")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.login(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Login failed. Please check your credentials.")
            }
        }
    }

    fun createUsernameProfile(username: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.createUserProfile(username)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create username.")
            }
        }
    }

    fun sendPasswordResetLink(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.sendPasswordResetEmail(email)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to send reset email.")
            }
        }
    }

    fun updateUserProfile(
        username: String,
        age: String,
        gender: String,
        phone: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentProfile = userProfile.value ?: throw Exception("User profile not loaded")
                val updatedProfile = currentProfile.copy(
                    username = username,
                    age = age,
                    gender = gender,
                    phone = phone
                    // The 'address' field is no longer part of the copy
                )
                repository.updateUserProfile(updatedProfile)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update profile.")
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}
