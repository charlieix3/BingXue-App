package com.example.assignmentexample.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_REMEMBER_ME = "KEY_REMEMBER_ME"
        const val KEY_SAVED_EMAIL = "KEY_SAVED_EMAIL"
        const val KEY_DEFAULT_PAYMENT_TYPE = "KEY_DEFAULT_PAYMENT_TYPE"
        const val KEY_DEFAULT_CARD_ID = "KEY_DEFAULT_CARD_ID"
        const val KEY_LAST_ORDER_ID = "KEY_LAST_ORDER_ID"
        const val KEY_ORDER_HISTORY_SORT = "KEY_ORDER_HISTORY_SORT"
    }

    //property to save whether the checkbox is checked
    var rememberMeEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    //property to save the email address
    var savedEmail: String?
        get() = prefs.getString(KEY_SAVED_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_SAVED_EMAIL, value).apply()

    //property to save the bank option
    var savedBank: String
        get() = prefs.getString("KEY_SAVED_BANK", "Maybank") ?: "Maybank"
        set(value) = prefs.edit().putString("KEY_SAVED_BANK", value).apply()

    //property to save the payment type
    var defaultPaymentType: String
        get() = prefs.getString(KEY_DEFAULT_PAYMENT_TYPE, "Online Banking") ?: "Online Banking"
        set(value) = prefs.edit().putString(KEY_DEFAULT_PAYMENT_TYPE, value).apply()

    //property to save default card
    var defaultCardId: Int
        get() = prefs.getInt(KEY_DEFAULT_CARD_ID, -1)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_CARD_ID, value).apply()

    var lastOrderId: Int
        get() = prefs.getInt(KEY_LAST_ORDER_ID, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_ORDER_ID, value).apply()

    //save sort option
    var orderHistorySortOption: String
        get() = prefs.getString(KEY_ORDER_HISTORY_SORT, "Newest") ?: "Newest"
        set(value) = prefs.edit().putString(KEY_ORDER_HISTORY_SORT, value).apply()
}