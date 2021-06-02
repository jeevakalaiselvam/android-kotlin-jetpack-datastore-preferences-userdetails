package com.gamerguide.android.starterapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(dataStore: DataStore<Preferences>) {

    private val mDataStore: DataStore<Preferences> = dataStore

    //Create a companion object to reference to store and get unique details
    companion object{
        val USER_NAME_KEY = stringPreferencesKey("USER_NAME")
        val USER_AGE_KEY = intPreferencesKey("USER_AGE")
    }

    /**
     * Store the user information by using reference to the data store
     * and using the edit method on it
     */
    suspend fun storeUserData(age: Int,name: String){
        mDataStore.edit { preference ->
            preference[USER_NAME_KEY] = name
            preference[USER_AGE_KEY] = age
        }
    }

    //Create a Coroutine flow to retrieve User Age
    val userAgeFlow: Flow<Int> = mDataStore.data.map {
        it[USER_AGE_KEY] ?: 0 //Set default value to zero if data not present in Datastore
    }

    //Create a Coroutine flow to retrieve User Name
    val userNameFlow: Flow<String> = mDataStore.data.map {
        it[USER_NAME_KEY] ?: ""
    }







}