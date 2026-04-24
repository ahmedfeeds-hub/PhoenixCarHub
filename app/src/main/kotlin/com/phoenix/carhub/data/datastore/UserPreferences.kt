package com.phoenix.carhub.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoenix.carhub.data.model.SOSContact
import com.phoenix.carhub.data.model.SavedLocation
import com.phoenix.carhub.data.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "phoenix_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    // ─── Keys ───────────────────────────────────────────────

    companion object {
        val KEY_THEME_MODE       = stringPreferencesKey("theme_mode")
        val KEY_HIGH_CONTRAST    = booleanPreferencesKey("high_contrast")
        val KEY_SOS_CONTACTS     = stringPreferencesKey("sos_contacts")
        val KEY_HOME_ADDRESS     = stringPreferencesKey("home_address")
        val KEY_WORK_ADDRESS     = stringPreferencesKey("work_address")
        val KEY_SCHOOL_ADDRESS   = stringPreferencesKey("school_address")
    }

    // ─── Theme ───────────────────────────────────────────────

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            when (prefs[KEY_THEME_MODE]) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK"  -> ThemeMode.DARK
                else    -> ThemeMode.AUTO
            }
        }

    val useHighContrast: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_HIGH_CONTRAST] ?: false }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HIGH_CONTRAST] = enabled }
    }

    // ─── SOS Contacts ────────────────────────────────────────

    val sosContacts: Flow<List<SOSContact>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val json = prefs[KEY_SOS_CONTACTS] ?: return@map emptyList()
            val type = object : TypeToken<List<SOSContact>>() {}.type
            runCatching { gson.fromJson<List<SOSContact>>(json, type) }.getOrDefault(emptyList())
        }

    suspend fun saveSosContacts(contacts: List<SOSContact>) {
        context.dataStore.edit { it[KEY_SOS_CONTACTS] = gson.toJson(contacts) }
    }

    // ─── Saved Locations ─────────────────────────────────────

    val homeAddress: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_HOME_ADDRESS] ?: "" }

    val workAddress: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_WORK_ADDRESS] ?: "" }

    val schoolAddress: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_SCHOOL_ADDRESS] ?: "" }

    suspend fun saveHomeAddress(address: String) {
        context.dataStore.edit { it[KEY_HOME_ADDRESS] = address }
    }

    suspend fun saveWorkAddress(address: String) {
        context.dataStore.edit { it[KEY_WORK_ADDRESS] = address }
    }

    suspend fun saveSchoolAddress(address: String) {
        context.dataStore.edit { it[KEY_SCHOOL_ADDRESS] = address }
    }
}
