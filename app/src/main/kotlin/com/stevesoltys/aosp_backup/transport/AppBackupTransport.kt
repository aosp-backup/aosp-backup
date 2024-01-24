package com.stevesoltys.aosp_backup.transport

import android.app.backup.BackupAgent.FLAG_DEVICE_TO_DEVICE_TRANSFER
import android.app.backup.BackupTransport
import android.app.backup.RestoreDescription
import android.app.backup.RestoreSet
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManager
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import java.util.concurrent.TimeUnit

class AppBackupTransport(
  private val context: Context,
  private val backupManager: BackupManager
) : BackupTransport() {

  companion object {
    private val TAG = AppBackupTransport::class.java.simpleName
    private const val TRANSPORT_DIRECTORY_NAME = "com.stevesoltys.aosp_backup.transport.AppBackupTransport"

    fun getTransportName(context: Context): String {
      return ComponentName(
        context, AppBackupTransport::class.java
      ).flattenToShortString()
    }
  }

  /**
   * The transport flags.
   *
   * We default to D2D transfers to provide the most coverage on application data.
   */
  override fun getTransportFlags(): Int {
    return FLAG_DEVICE_TO_DEVICE_TRANSFER
  }

  /**
   * The transport name.
   */
  override fun name(): String {
    return getTransportName(context)
  }

  /**
   * The transport directory name.
   */
  override fun transportDirName(): String {
    return TRANSPORT_DIRECTORY_NAME
  }

  /**
   * Initialize the device for backups.
   *
   * This will be called after the user has selected a backup location.
   */
  override fun initializeDevice(): Int {
    return backupManager.initializeBackupLocation()
      .map {
        Log.i(TAG, "Initialized backup location")
        TRANSPORT_OK
      }
      .mapFailure {
        Log.e(TAG, "Error initializing backup location", it)
        TRANSPORT_ERROR
      }
      .get()
  }

  /**
   * Checks if a package is eligible for backup.
   */
  override fun isAppEligibleForBackup(targetPackage: PackageInfo, isFullBackup: Boolean): Boolean {
    Log.i(TAG, "Checking if app is eligible for backup: ${targetPackage.packageName}")

    return backupManager.checkBackupEligibility(targetPackage, isFullBackup)
      .mapFailure {
        Log.e(TAG, "Error checking backup eligibility", it)
        false
      }
      .get()
  }

  /**
   * Perform a K/V backup.
   */
  override fun performBackup(
    targetPackage: PackageInfo,
    fileDescriptor: ParcelFileDescriptor,
    flags: Int
  ): Int {
    Log.i(TAG, "Performing K/V backup for ${targetPackage.packageName}")
    return backupManager.performBackup(targetPackage, fileDescriptor, flags, false)
      .map { TRANSPORT_OK }
      .mapFailure {
        Log.e(TAG, "Error initializing K/V backup for ${targetPackage.packageName}", it)
        TRANSPORT_ERROR
      }
      .get()
  }

  /**
   * Perform a K/V backup without flags.
   *
   * This typically won't get called.
   */
  override fun performBackup(
    packageInfo: PackageInfo,
    inFd: ParcelFileDescriptor
  ): Int {
    Log.i(TAG, "Performing legacy K/V backup for ${packageInfo.packageName}")
    return performBackup(packageInfo, inFd, 0)
  }

  /**
   * Perform a full backup.
   */
  override fun performFullBackup(
    targetPackage: PackageInfo,
    socket: ParcelFileDescriptor,
    flags: Int
  ): Int {
    Log.i(TAG, "Performing full backup for ${targetPackage.packageName}")

    return backupManager.performBackup(targetPackage, socket, flags, true)
      .map { TRANSPORT_OK }
      .mapFailure {
        Log.e(TAG, "Error initializing full backup for ${targetPackage.packageName}", it)
        TRANSPORT_ERROR
      }
      .get()
  }

  /**
   * Perform a full backup without flags.
   *
   * This typically won't get called.
   */
  override fun performFullBackup(
    targetPackage: PackageInfo,
    socket: ParcelFileDescriptor
  ): Int {
    Log.i(TAG, "Performing legacy full backup for ${targetPackage.packageName}")
    return performFullBackup(targetPackage, socket, 0)
  }

  override fun requestFullBackupTime(): Long {
    Log.i(TAG, "Requesting full backup time")
    return TimeUnit.DAYS.toMillis(1)
  }

  override fun checkFullBackupSize(size: Long): Int {
    Log.i(TAG, "Checking full backup size: $size bytes")
    return TRANSPORT_OK
  }

  override fun sendBackupData(numBytes: Int): Int {

    return backupManager.sendFullBackupData(numBytes)
      .map { TRANSPORT_OK }
      .mapFailure {
        Log.e(TAG, "Error sending backup data", it)
        TRANSPORT_ERROR
      }
      .get()
  }

  override fun cancelFullBackup() {
    Log.i(TAG, "Cancelling full backup")

    backupManager.cancelFullBackup()
      .mapFailure {
        Log.e(TAG, "Error cancelling full backup", it)
      }
  }

  override fun requestBackupTime(): Long {
    Log.i(TAG, "Requesting backup time")
    return TimeUnit.DAYS.toMillis(1)
  }

  override fun currentDestinationString(): String {
    Log.i(TAG, "Getting current destination string")
    return "Destination"
  }

  override fun getBackupQuota(packageName: String, isFullBackup: Boolean): Long {
    Log.i(TAG, "Getting backup quota for $packageName")
    return Long.MAX_VALUE
  }

  override fun clearBackupData(packageInfo: PackageInfo): Int {
    Log.i(TAG, "Clearing backup data for ${packageInfo.packageName}")
    return TRANSPORT_OK
  }

  override fun finishBackup(): Int {
    Log.i(TAG, "Finished backup")

    return backupManager.finishBackup()
      .map { TRANSPORT_OK }
      .mapFailure {
        Log.e(TAG, "Error finishing backup", it)
        TRANSPORT_ERROR
      }
      .get()
  }

  // Restore

  override fun getAvailableRestoreSets(): Array<RestoreSet> {
    Log.i(TAG, "Getting available restore sets")
    return emptyArray()
  }

  override fun getCurrentRestoreSet(): Long {
    Log.i(TAG, "Getting current restore set")
    return 0
  }

  override fun startRestore(token: Long, packages: Array<PackageInfo>): Int {
    Log.i(TAG, "Starting restore for token $token")
    return TRANSPORT_OK
  }

  override fun getNextFullRestoreDataChunk(socket: ParcelFileDescriptor): Int {
    Log.i(TAG, "Getting next full restore data chunk")
    return TRANSPORT_OK
  }

  override fun nextRestorePackage(): RestoreDescription? {
    Log.i(TAG, "Getting next restore package")
    return null
  }

  override fun getRestoreData(outputFileDescriptor: ParcelFileDescriptor): Int {
    Log.i(TAG, "Getting restore data")
    return TRANSPORT_OK
  }

  override fun abortFullRestore(): Int {
    Log.i(TAG, "Aborting full restore")
    return TRANSPORT_OK
  }

  override fun finishRestore() {
    Log.i(TAG, "Finished restore")
  }
}
