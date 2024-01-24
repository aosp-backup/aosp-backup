package com.stevesoltys.aosp_backup.manager.location

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import dev.forkhandles.result4k.Result
import java.io.OutputStream

abstract class BackupLocation {

  /**
   * Prompt the user to select a backup location.
   */
  abstract fun locationSelectionActivity(
    activity: ComponentActivity,
    callback: () -> Unit
  ): ActivityResultLauncher<Unit>

  abstract fun initializeLocation(): Result<Unit, Exception>

  abstract fun initializeBackup(): Result<Unit, Exception>

  abstract fun keyValueBackupOutputStream(identifier: String): OutputStream

  abstract fun fullBackupOutputStream(identifier: String): OutputStream

  abstract fun apkBackupOutputStream(identifier: String): OutputStream

  abstract fun finalizeBackup(): Result<Unit, Exception>

  abstract fun excludedPackages(): List<String>

  abstract fun type(): BackupLocationType

  abstract fun name(): String

  abstract fun icon(): Int
}
