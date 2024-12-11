package com.application.p3tskit.ui.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfilePreferences private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {

    private object PreferencesKeys {
        val THEME_KEY = booleanPreferencesKey("is_dark_mode")
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_KEY] = isDarkModeActive
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.THEME_KEY] ?: false
        }
    }

    companion object {
        private var INSTANCE: ProfilePreferences? = null

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): ProfilePreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfilePreferences(dataStore).also { INSTANCE = it }
            }
        }
    }
}
