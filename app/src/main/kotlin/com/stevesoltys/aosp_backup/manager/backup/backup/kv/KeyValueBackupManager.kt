package com.stevesoltys.aosp_backup.manager.backup.backup.kv

import android.app.backup.BackupTransport.FLAG_DATA_NOT_CHANGED
import android.app.backup.BackupTransport.FLAG_INCREMENTAL
import android.app.backup.BackupTransport.TRANSPORT_NON_INCREMENTAL_BACKUP_REQUIRED
import android.app.backup.BackupTransport.TRANSPORT_OK
import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManagerState
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanManager
import com.stevesoltys.aosp_backup.util.toSuccess
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueBackupManager @Inject constructor(
  private val backupPlanManager: BackupPlanManager
) {

  companion object {
    private val TAG = KeyValueBackupManager::class.java.simpleName
  }

  fun performBackup(
    currentState: BackupManagerState?,
    packageInfo: PackageInfo,
    socket: ParcelFileDescriptor,
    flags: Int
  ): Result<KeyValueBackupResult, Exception> {
    return resultFrom {
      // Reset the current state if necessary.
      currentState?.reset()

      // Check if we can or should perform a K/V backup for this package.
      checkFlags(socket, flags)?.let {
        return it.toSuccess()
      }

      // Perform the K/V backup.
      performBackup(packageInfo, socket)
    }
  }

  private fun performBackup(packageInfo: PackageInfo, socket: ParcelFileDescriptor): KeyValueBackupResult {
    val backupLocation = backupPlanManager.backupPlan()
      ?: throw IllegalStateException("Backup location is not initialized.")

    backupLocation.keyValueBackupOutputStream(packageInfo.packageName)
      .use { outputStream ->
        FileInputStream(socket.fileDescriptor).use { inputStream ->
          inputStream.copyTo(outputStream)
        }
      }

    return KeyValueBackupResult(result = TRANSPORT_OK)
  }

  private fun checkFlags(
    socket: ParcelFileDescriptor,
    flags: Int
  ): KeyValueBackupResult? {

    return if (isIncrementalBackup(flags)) {
      // We don't currently support incremental backups, so request a non-incremental backup.
      KeyValueBackupResult(result = TRANSPORT_NON_INCREMENTAL_BACKUP_REQUIRED)

    } else if (dataNotChanged(flags)) {
      // If the data hasn't changed, we don't need to do anything.
      socket.close() // TODO: Should we also close this for requesting incremental backups?
      KeyValueBackupResult(result = TRANSPORT_OK)

    } else {
      // Otherwise, we can perform a K/V backup.
      null
    }
  }

  private fun dataNotChanged(flags: Int): Boolean {
    return flags and FLAG_DATA_NOT_CHANGED != 0
  }

  private fun isIncrementalBackup(flags: Int): Boolean {
    return flags and FLAG_INCREMENTAL != 0
  }
}
