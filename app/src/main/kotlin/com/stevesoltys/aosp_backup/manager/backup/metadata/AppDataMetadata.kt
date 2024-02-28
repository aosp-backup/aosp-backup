package com.stevesoltys.aosp_backup.manager.backup.metadata

import android.content.pm.PackageInfo

/**
 * Metadata for a single app's data.
 *
 * @param packageName The package name.
 * @param location The location of the app's data in the backup.
 */
data class AppDataMetadata(
  val packageName: String,
  val location: String
)

fun PackageInfo.toAppDataMetadata(): AppDataMetadata {
  return AppDataMetadata(
    packageName = packageName,
    location = "data/$packageName"
  )
}
