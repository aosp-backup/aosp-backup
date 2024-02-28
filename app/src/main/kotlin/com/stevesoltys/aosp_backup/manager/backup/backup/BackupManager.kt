package com.stevesoltys.aosp_backup.manager.backup.backup

import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import com.stevesoltys.aosp_backup.manager.backup.backup.apk.ApkBackupManager
import com.stevesoltys.aosp_backup.manager.backup.backup.full.FullBackupManager
import com.stevesoltys.aosp_backup.manager.backup.backup.kv.KeyValueBackupManager
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanManager
import com.stevesoltys.aosp_backup.manager.package_.PackageManager
import com.stevesoltys.aosp_backup.util.AOSP.PACKAGE_MANAGER_SENTINEL
import com.stevesoltys.aosp_backup.util.toSuccess
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class used for initializing and performing backups.
 */
@Singleton
class BackupManager @Inject constructor(
  private val systemBackupManager: SystemBackupManager,
  private val packageManager: PackageManager,
  private val backupPlanManager: BackupPlanManager,
  private val fullBackupManager: FullBackupManager,
  private val keyValueBackupManager: KeyValueBackupManager,
  private val apkBackupManager: ApkBackupManager
) {

  private var currentState: BackupManagerState? = null

  companion object {
    private val TAG = BackupManager::class.java.simpleName
  }

  /**
   * Initialize the backup location for the current user.
   */
  fun initializeBackupLocation(): Result<Unit, Exception> = resultFrom {
    val backupLocation = backupPlanManager.backupPlan()
      ?: throw IllegalStateException("Backup location is not initialized.")

    backupLocation.initializeLocation()
      .onFailure { return it }
  }

  /**
   * Run a backup for the current user using a [BackupProcessor].
   *
   * We always have to include the package manager sentinel, since the metadata is used during restores.
   */
  fun runBackup(): Result<Unit, Exception> = resultFrom {
    val packages = packageManager.getAppDataEligiblePackages(backupPlanManager.backupPlan())
      .map { it.packageName }
      .plus(PACKAGE_MANAGER_SENTINEL)

    val backupProcessor = BackupProcessor(
      systemBackupManager = systemBackupManager,
      backupManager = this,
      packages = packages
    )

    backupProcessor.startBackup()
      .onFailure { return it }
  }

  /**
   * Initializes a backup that we initiated via [runBackup].
   *
   * This will be called by a [BackupProcessor] when it is ready to start a backup.
   */
  fun initializeBackup(): Result<Unit, Exception> = resultFrom {
    val backupLocation = backupPlanManager.backupPlan()
      ?: throw IllegalStateException("Backup location is not initialized.")

    Log.i(TAG, "Initializing backup.")

    backupLocation.initializeBackup()
      .onFailure { return it }
  }

  /**
   * Finalizes a backup that we initiated via [runBackup].
   *
   * This will be called by a [BackupProcessor] when it is ready to finalize a backup.
   */
  fun finalizeBackup(): Result<Unit, Exception> = resultFrom {
    val backupLocation = backupPlanManager.backupPlan()
      ?: throw IllegalStateException("Backup location is not initialized.")

    apkBackupManager.backupApks().mapFailure {
      // Even if we fail while backing up our APKs, we can still try to finalize the backup.
      Log.e(TAG, "Failed to backup APKs.", it)
    }

    Log.i(TAG, "Finalizing backup.")

    backupLocation.finalizeBackup()
      .onFailure { return it }
  }

  /**
   * Perform backup on a package.
   *
   * For a full backup, this is more of an initialization which will be followed by a series of calls to transfer the data.
   * For a K/V backup, this will be a single call to send the data.
   */
  fun performBackup(
    packageInfo: PackageInfo,
    socket: ParcelFileDescriptor,
    flags: Int,
    fullBackup: Boolean
  ): Result<Int, Exception> {

    return if (fullBackup) {
      fullBackupManager.initializePackageForBackup(
        currentState = currentState,
        packageInfo = packageInfo,
        socket = socket,
        flags = flags
      ).map {
        currentState = it.state
        it.result
      }

    } else {
      keyValueBackupManager.performBackup(
        currentState = currentState,
        packageInfo = packageInfo,
        socket = socket,
        flags = flags
      ).map {
        currentState = it.state
        it.result
      }
    }
  }

  /**
   * Backup the provided number of bytes for the current full backup package.
   */
  fun sendFullBackupData(
    numBytes: Int
  ): Result<Int, Exception> {
    return fullBackupManager.sendBackupData(
      currentState = currentState,
      numBytes = numBytes
    ).map { it.result }
  }

  /**
   * Cancel a full backup.
   */
  fun cancelFullBackup(): Result<Unit, Exception> {
    return fullBackupManager.cancelBackup(currentState)
      .map { it.result }
  }

  /**
   * Mark a backup as completed.
   */
  fun finishBackup(): Result<Int, Exception> {
    return fullBackupManager.finishBackup(currentState)
      .map { it.result }
  }

  /**
   * Check if a given package is eligible to be backed up.
   */
  fun checkBackupEligibility(
    packageInfo: PackageInfo,
    isFullBackup: Boolean
  ): Result<Boolean, Exception> {
    return true.toSuccess()
  }
}
