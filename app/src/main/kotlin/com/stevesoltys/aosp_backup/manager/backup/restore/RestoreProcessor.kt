package com.stevesoltys.aosp_backup.manager.backup.restore

import android.app.backup.IRestoreObserver
import android.app.backup.IRestoreSession
import android.app.backup.RestoreSet
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.PackageDataDescription
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlan
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom

class RestoreProcessor(
  private val systemBackupManager: SystemBackupManager,
  private val restoreManager: RestoreManager,
  private val packages: List<String>,
  private val restorePlan: RestorePlan
) : IRestoreObserver.Stub() {

  companion object {
    private val TAG = RestoreProcessor::class.java.simpleName
  }

  private lateinit var restoreSession: IRestoreSession

  private val restoredPackages = mutableSetOf<String>()

  private var currentPackage: PackageDataDescription? = null

  fun startRestore(): Result<Unit, Exception> = resultFrom {
    restorePlan.initializeRestore().onFailure { return it }
    restoreSession = systemBackupManager.beginRestoreSession().onFailure { return it }

    restoreNextPackage().onFailure { return it }
  }

  private fun restoreNextPackage(): Result<Unit, Exception> = resultFrom {

    if (restoredPackages.size >= packages.size) {
      finalizeRestore()
      return@resultFrom
    }

    currentPackage = restorePlan.nextRestorePackage()
      .onFailure { return it }
      ?: throw IllegalStateException("No current package, but we haven't finished restoring.")

    if (currentPackage!!.packageName !in packages) {
      Log.i(TAG, "Skipping package ${currentPackage!!.packageName} because it is not in the list of packages to restore.")
      return restoreNextPackage()
    }

    Log.i(TAG, "Restoring package ${currentPackage!!.packageName}")

    systemBackupManager.restorePackage(
      session = restoreSession,
      packageName = currentPackage!!.packageName,
      backupObserver = this
    ).mapFailure {
      Log.e(TAG, "Failed to restore package ${currentPackage!!.packageName}", it)
      restoreFinished(-1)
    }
  }

  override fun restoreSetsAvailable(p0: Array<out RestoreSet>?) {
  }

  override fun restoreStarting(p0: Int) {
  }

  override fun onUpdate(p0: Int, p1: String?) {
  }

  override fun restoreFinished(restoreResult: Int) {
    val currentPackageName = currentPackage?.packageName
      ?: run {
        finalizeRestore()
        throw IllegalStateException("No current package, but we finished a restore.")
      }

    Log.i(TAG, "Restore for package '$currentPackageName' finished with result: $restoreResult")
    restoredPackages.add(currentPackageName)

    restoreNextPackage().mapFailure {
      Log.e(TAG, "Failed to restore next package.", it)

      finalizeRestore()
    }
  }

  private fun finalizeRestore() {
    Log.i(TAG, "Finalizing restore")
    restoreSession.endRestoreSession()
    restoreManager.finalizeRestore()
  }
}
