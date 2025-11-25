package com.example.assignmentexample.data.CreditCard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import com.example.assignmentexample.data.SettingsManager
import com.example.assignmentexample.data.UserViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreditCardViewModel(
    application: Application,
    private val userViewModel: UserViewModel
) : AndroidViewModel(application) {


    private val creditCardDao = AppDatabase.getDatabase(application).creditCardDao()
    private val settingsManager = SettingsManager(application)

    val savedCreditCards: StateFlow<List<CreditCardEntity>> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                creditCardDao.getSavedCreditCards(user.uid)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addCreditCard(card: CreditCardEntity) {
        viewModelScope.launch {

            val userId = userViewModel.userProfile.value?.uid ?: return@launch


            creditCardDao.insert(card.copy(userOwnerId = userId))
        }
    }

    fun deleteCreditCard(card: CreditCardEntity) {
        viewModelScope.launch {
            creditCardDao.delete(card)
        }
    }

    fun saveDefaultPaymentChoice(
        paymentType: String,
        selectedCard: CreditCardEntity?,
        selectedBank: String
    ) {
        settingsManager.defaultPaymentType = paymentType
        when (paymentType) {
            "Credit Card" -> {
                settingsManager.defaultCardId = selectedCard?.id ?: -1
            }
            "Online Banking" -> {
                settingsManager.savedBank = selectedBank
            }
        }
    }
}



class CreditCardViewModelFactory(
    private val application: Application,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreditCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreditCardViewModel(application, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}