package com.stevesoltys.aosp_backup.manager.configuration

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationManager @Inject constructor(
  @ApplicationContext
  private val context: Context
) {

  private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

  fun sharedPreferences(): SharedPreferences {
    return preferences
  }

  fun getPreference(key: String): String? {
    return preferences.getString(key, null)
  }

  fun setPreference(key: String, value: String) {
    preferences.edit().putString(key, value).apply()
  }
}
