package com.stevesoltys.aosp_backup.manager.backup.restore

import android.app.backup.RestoreSet
import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import com.stevesoltys.aosp_backup.manager.backup.restore.full.FullRestoreManager
import com.stevesoltys.aosp_backup.manager.backup.restore.kv.KeyValueRestoreManager
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.PackageDataDescription
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlan
import com.stevesoltys.aosp_backup.util.toSuccess
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestoreManager @Inject constructor(
  private val systemBackupManager: SystemBackupManager,
  private val fullRestoreManager: FullRestoreManager,
  private val keyValueRestoreManager: KeyValueRestoreManager
) {

  private var restorePlan: RestorePlan? = null

  private var state: RestoreManagerState? = null

  /**
   * Run a restore for the current user using a [RestoreProcessor].
   */
  fun runRestore(
      restorePlan: RestorePlan,
      packages: List<String>
  ): Result<Unit, Exception> = resultFrom {
    this.restorePlan = restorePlan

    val restoreProcessor = RestoreProcessor(
      systemBackupManager = systemBackupManager,
      restoreManager = this,
      restorePlan = restorePlan,
      packages = packages
    )

    restoreProcessor.startRestore()
      .onFailure {
        finalizeRestore()
        return it
      }
  }

  /**
   * Finalize any ongoing restore for the current user.
   */
  fun finalizeRestore(): Result<Unit, Exception> = resultFrom {
    state?.reset()
    state = null

    restorePlan?.finalizeRestore()
      ?.also { restorePlan = null }
      ?.onFailure { return it }
  }

  /**
   * Called by the transport when we have finished restoring a set of packages.
   *
   * Since we restore one package at a time, this might not be the end of the restore session.
   */
  fun finishRestore(): Result<Unit, Exception> = resultFrom {
  }

  /**
   * Skip to the next restore package.
   *
   * In our case, the [BackupProcessor] is restoring one package at a time, so this will always be the current package.
   */
  fun getNextRestorePackage(): Result<PackageDataDescription?, Exception> = resultFrom {

    if (state!!.packageIndex + 1 >= state!!.packageList.size) {
      return null.toSuccess()
    }

    state!!.packageIndex++

    val packageName = state!!.currentPackageName()

    return restorePlan!!.getPackageDescription(packageName)
  }

  /**
   * Get the available restore sets for the current backup location.
   *
   * We just mock a single restore set currently.
   */
  fun getAvailableRestoreSets(): Result<List<RestoreSet>, Exception> = resultFrom {
    return restorePlan!!.getRestoreSets()
  }

  /**
   * Get the current restore set identifier.
   */
  fun getCurrentRestoreSet(): Result<RestoreSet, Exception> = resultFrom {
    return restorePlan!!.getCurrentRestoreSet()
  }

  /**
   * Aborts the restore for the current package.
   */
  fun abortCurrentPackageRestore() = resultFrom {
  }

  /**
   * Get the next restore chunk for the given file descriptor.
   */
  fun getRestoreChunk(
    fileDescriptor: ParcelFileDescriptor,
    fullBackup: Boolean
  ): Result<Int, Exception> = resultFrom {

    val state = state ?: throw IllegalStateException("Restore state is not initialized.")
    val restoreLocation = restorePlan ?: throw IllegalStateException("Restore location is not initialized.")

    return if (fullBackup) {
      fullRestoreManager.getRestoreChunk(
        restoreManagerState = state,
        restorePlan = restoreLocation,
        fileDescriptor = fileDescriptor
      )

    } else {
      keyValueRestoreManager.getRestoreData(
        restoreManagerState = state,
        restorePlan = restoreLocation,
        fileDescriptor = fileDescriptor
      )

    }.map {
      this.state = it

      it.transportResponse!!
    }
  }

  fun startRestore(
    packages: Array<PackageInfo>
  ): Result<Unit, Exception> = resultFrom {

    state = RestoreManagerState(
      packageList = packages.toList(),
      packageIndex = -1,
      inputStream = null,
      transportResponse = null
    )
  }
}
