package com.example.assignmentexample.data.Drink

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DrinkDetailViewModel(application: Application, drinkId: Int) : ViewModel() {
    private val drinkDao = AppDatabase.getDatabase(application).drinkDao()
    private val _drink = MutableStateFlow<DrinkEntity?>(null)
    val drink = _drink.asStateFlow()

    init {
        viewModelScope.launch {
            _drink.value = drinkDao.getDrinkById(drinkId)
        }
    }
}

class DrinkDetailViewModelFactory(
    private val application: Application,
    private val drinkId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrinkDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrinkDetailViewModel(application, drinkId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}