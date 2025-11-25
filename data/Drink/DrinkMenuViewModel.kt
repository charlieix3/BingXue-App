package com.example.assignmentexample.data.Drink

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignmentexample.data.AppDatabase
import kotlinx.coroutines.flow.Flow

class DrinkMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val drinkDao = AppDatabase.getDatabase(application).drinkDao()
    val drinks: Flow<List<DrinkEntity>> = drinkDao.getAllDrinks()
}

class DrinkMenuViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrinkMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrinkMenuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}