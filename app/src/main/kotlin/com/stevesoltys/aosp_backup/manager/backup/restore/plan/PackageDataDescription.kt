package com.stevesoltys.aosp_backup.manager.backup.restore.plan

import android.app.backup.RestoreDescription.TYPE_FULL_STREAM
import android.app.backup.RestoreDescription.TYPE_KEY_VALUE

data class PackageDataDescription(
  val type: PackageDataType,
  val packageName: String
)

enum class PackageDataType(
  val pathIdentifier: String,
  val identifier: Int?
) {
  FULL("full", TYPE_FULL_STREAM),
  KEY_VALUE("kv", TYPE_KEY_VALUE),
  APK("apk", null),
  UNKNOWN("unknown", null);

  companion object {
    fun fromPath(identifier: String): PackageDataType {
      return when (identifier) {
        FULL.pathIdentifier -> FULL
        KEY_VALUE.pathIdentifier -> KEY_VALUE
        APK.pathIdentifier -> APK
        else -> UNKNOWN
      }
    }
  }

  fun isAppData(): Boolean {
    return this == FULL || this == KEY_VALUE
  }
}
