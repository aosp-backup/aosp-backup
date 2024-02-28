package com.stevesoltys.aosp_backup.manager.backup.metadata

import android.content.pm.PackageInfo

/**
 * Metadata for a single APK.
 *
 * @param packageName The package name.
 * @param location The location of the APK(s) in the backup.
 */
data class AppApkMetadata(
  val packageName: String,
  val location: String
)

fun PackageInfo.toApkMetadata(): AppApkMetadata {
  return AppApkMetadata(
    packageName = packageName,
    location = "apk/$packageName"
  )
}
