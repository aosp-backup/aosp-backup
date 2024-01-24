package com.stevesoltys.aosp_backup.manager.backup.backup

import android.app.backup.BackupProgress
import android.app.backup.IBackupObserver
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom

class BackupProcessor(
  private val systemBackupManager: SystemBackupManager,
  private val backupManager: BackupManager,
  private val packages: List<String>
) : IBackupObserver.Stub() {

  companion object {
    private val TAG = BackupProcessor::class.java.simpleName

    private const val PACKAGE_CHUNK_SIZE = 25
  }

  private val packageChunks = packages.chunked(PACKAGE_CHUNK_SIZE)

  private var packageIndex = 0

  fun startBackup(): Result<Unit, Exception> = resultFrom {
    backupManager.initializeBackup().onFailure { return it }
    backupNextChunk().onFailure { return it }
  }

  private fun backupNextChunk(): Result<Unit, Exception> = resultFrom {

    if (packageIndex >= packages.size) {
      backupManager.finalizeBackup()
      return@resultFrom
    }

    val currentChunkIndex = packageIndex / PACKAGE_CHUNK_SIZE
    val currentChunk = packageChunks[currentChunkIndex]

    Log.i(TAG, "Starting backup for chunk $currentChunkIndex of ${packageChunks.size - 1} with packages: ${currentChunk.joinToString(", ")}.")
    systemBackupManager.requestBackup(this, currentChunk)
  }

  override fun onUpdate(currentPackage: String?, backupProgress: BackupProgress) {
  }

  override fun onResult(currentPackage: String?, backupResult: Int) {
  }

  override fun backupFinished(backupResult: Int) {
    Log.i(TAG, "Backup for chunk ${packageIndex / PACKAGE_CHUNK_SIZE} finished with result: $backupResult")

    packageIndex += PACKAGE_CHUNK_SIZE
    backupNextChunk()
  }
}
