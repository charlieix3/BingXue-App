package com.example.assignmentexample.data.Address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import com.example.assignmentexample.data.UserViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddressViewModel(application: Application, private val userViewModel: UserViewModel) : AndroidViewModel(application) {
    private val addressDao = AppDatabase.getDatabase(application).addressDao()

    val allAddresses: StateFlow<List<AddressEntity>> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else addressDao.getAllAddresses(user.uid)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            val userId = userViewModel.userProfile.value?.uid ?: return@launch
            if (address.isDefault) {
                addressDao.clearUserDefaults(userId)
            }
            addressDao.insertAddress(address.copy(userOwnerId = userId))
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            addressDao.deleteAddress(address)
        }
    }

    fun setDefaultAddress(address: AddressEntity) {
        viewModelScope.launch {
            val userId = userViewModel.userProfile.value?.uid ?: return@launch
            addressDao.clearUserDefaults(userId)
            addressDao.updateAddress(address.copy(isDefault = true))
        }
    }
}

class AddressViewModelFactory(private val application: Application, private val userViewModel: UserViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddressViewModel(application, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}