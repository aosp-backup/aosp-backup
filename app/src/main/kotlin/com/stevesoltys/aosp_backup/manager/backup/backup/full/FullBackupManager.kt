package com.stevesoltys.aosp_backup.manager.backup.backup.full

import android.app.backup.BackupTransport.TRANSPORT_OK
import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManagerState
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanManager
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FullBackupManager @Inject constructor(
  private val backupPlanManager: BackupPlanManager
) {

  fun initializePackageForBackup(
    currentState: BackupManagerState?,
    packageInfo: PackageInfo,
    socket: ParcelFileDescriptor,
    flags: Int
  ): Result<FullBackupResult, Exception> {
    return resultFrom {
      // Reset the current state if necessary.
      currentState?.reset()

      // Create the input stream from the socket.
      val inputStream = FileInputStream(socket.fileDescriptor)

      // Create the output stream from the backup location.
      val outputStream = backupPlanManager.backupLocationType()
        ?.fullBackupOutputStream(packageInfo.packageName)
        ?: throw IllegalStateException("Backup location is not initialized.")

      FullBackupResult(
        result = TRANSPORT_OK,

        state = BackupManagerState(
          currentPackage = packageInfo,
          inputStream = inputStream,
          inputSocket = socket,
          outputStream = outputStream,
          fullBackup = true
        )
      )
    }
  }

  fun cancelBackup(currentState: BackupManagerState?): Result<FullBackupResult, Exception> {
    return finishBackup(currentState)
  }

  fun finishBackup(currentState: BackupManagerState?): Result<FullBackupResult, Exception> {
    return resultFrom {
      currentState?.reset()

      FullBackupResult(result = TRANSPORT_OK)
    }
  }

  fun sendBackupData(
    currentState: BackupManagerState?,
    numBytes: Int
  ): Result<FullBackupResult, Exception> {

    return resultFrom {
      val state = currentState
        ?: throw IllegalStateException("No current backup state, but data was requested to be sent.")

      val buffer = ByteArray(numBytes)
      val bytesRead = state.inputStream.read(buffer)

      if (bytesRead == -1) {
        throw IOException(
          "No bytes read for package '${currentState.currentPackage.packageName}'," +
            " but we were requested to read ${numBytes}."
        )
      }

      currentState.outputStream.write(buffer, 0, bytesRead)

      FullBackupResult(
        result = TRANSPORT_OK,
        state = currentState
      )
    }
  }
}
