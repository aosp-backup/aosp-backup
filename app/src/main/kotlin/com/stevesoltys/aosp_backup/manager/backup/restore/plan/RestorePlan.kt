package com.stevesoltys.aosp_backup.manager.backup.restore.plan

import android.app.backup.RestoreSet
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.stevesoltys.aosp_backup.manager.backup.metadata.BackupMetadata
import dev.forkhandles.result4k.Result
import java.io.InputStream

abstract class RestorePlan {

  /**
   * Prompt the user to select a backup to restore.
   */
  abstract fun locationSelectionActivity(
    activity: ComponentActivity,
    callback: (Boolean) -> Unit
  ): ActivityResultLauncher<Unit>

  abstract fun getBackupMetadata(): Result<BackupMetadata, Exception>

  abstract fun getRestoreSets(): Result<List<RestoreSet>, Exception>

  abstract fun getCurrentRestoreSet(): Result<RestoreSet, Exception>

  abstract fun getKeyValueInputStream(packageName: String): Result<InputStream, Exception>

  abstract fun getFullInputStream(packageName: String): Result<InputStream, Exception>

  abstract fun getPackageDescription(packageName: String): Result<PackageDataDescription?, Exception>

  abstract fun initializeRestore(): Result<Unit, Exception>

  abstract fun nextRestorePackage(): Result<PackageDataDescription?, Exception>

  abstract fun finalizeRestore(): Result<Unit, Exception>

  abstract fun excludedPackages(): List<String>

  abstract fun type(): RestorePlanType

  abstract fun name(): String

  abstract fun icon(): Int
}
