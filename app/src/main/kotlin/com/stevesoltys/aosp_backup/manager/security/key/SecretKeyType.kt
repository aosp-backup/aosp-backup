package com.stevesoltys.aosp_backup.manager.security.key

import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import android.security.keystore.KeyProtection

enum class SecretKeyType(
  private val identifier: String
) {
  BACKUP("backup"),
  RESTORE("restore");

  companion object {
    private const val KEY_ALIAS_PREFIX = "com.stevesoltys.aosp_backup"
  }

  fun getAlias(): String {
    return "$KEY_ALIAS_PREFIX.$identifier"
  }

  fun getProtection(): KeyProtection {
    return KeyProtection.Builder(
      when (this) {
        BACKUP -> PURPOSE_ENCRYPT or PURPOSE_SIGN
        RESTORE -> PURPOSE_DECRYPT or PURPOSE_ENCRYPT or PURPOSE_SIGN or PURPOSE_VERIFY
      }
    ).build()
  }
}
