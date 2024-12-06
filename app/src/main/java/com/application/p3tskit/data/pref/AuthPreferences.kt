package com.application.p3tskit.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>)  {

    suspend fun saveSession(user: AuthModel){
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
        }
    }

    fun getSession(): Flow<AuthModel> {
        return dataStore.data.map { preferences ->
            AuthModel(
                preferences[EMAIL_KEY]?:" ",
                preferences[TOKEN_KEY]?:" ",
            )
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}