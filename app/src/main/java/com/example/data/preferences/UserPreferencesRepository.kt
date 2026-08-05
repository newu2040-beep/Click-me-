package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val GRID_TYPE = stringPreferencesKey("grid_type")
        val SHOW_HISTOGRAM = booleanPreferencesKey("show_histogram")
        val SHOW_LEVEL = booleanPreferencesKey("show_level")
        val SAVE_RAW = booleanPreferencesKey("save_raw")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val DEFAULT_CAMERA_MODE = stringPreferencesKey("default_camera_mode")
        val DEFAULT_ASPECT_RATIO = stringPreferencesKey("default_aspect_ratio")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACCENT_COLOR_HEX = stringPreferencesKey("accent_color_hex")
    }

    val appTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.APP_THEME] ?: "DARK"
    }

    val gridType: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.GRID_TYPE] ?: "RULE_OF_THIRDS"
    }

    val showHistogram: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_HISTOGRAM] ?: true
    }

    val showLevel: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_LEVEL] ?: true
    }

    val saveRaw: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SAVE_RAW] ?: false
    }

    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HAPTICS_ENABLED] ?: true
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    val accentColorHex: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCENT_COLOR_HEX] ?: "#FF6D00"
    }

    suspend fun setAppTheme(themeName: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_THEME] = themeName
        }
    }

    suspend fun setGridType(grid: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GRID_TYPE] = grid
        }
    }

    suspend fun setShowHistogram(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_HISTOGRAM] = show
        }
    }

    suspend fun setShowLevel(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_LEVEL] = show
        }
    }

    suspend fun setSaveRaw(save: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SAVE_RAW] = save
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAccentColor(hex: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCENT_COLOR_HEX] = hex
        }
    }
}
