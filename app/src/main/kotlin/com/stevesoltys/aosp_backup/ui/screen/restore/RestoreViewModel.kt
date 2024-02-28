package com.stevesoltys.aosp_backup.ui.screen.restore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevesoltys.aosp_backup.manager.backup.restore.RestoreManager
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlan
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlanManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
  private val restorePlanManager: RestorePlanManager,
  private val restoreManager: RestoreManager
) : ViewModel() {

  companion object {
    private val TAG = RestoreViewModel::class.java.simpleName
  }

  private var restorePlan: RestorePlan? = null

  fun getRestoreLocations(): List<RestorePlan> {
    return restorePlanManager.restorePlanList()
  }

  fun chooseRestoreLocation(restorePlan: RestorePlan) {
    this.restorePlan = restorePlan
  }

  fun runRestore(): Result<Unit, Exception> = resultFrom {
    val restorePlan = restorePlan ?: throw IllegalStateException("Restore location is not initialized.")

    viewModelScope.launch(Dispatchers.IO) {

      val backupMetadata = restorePlan.getBackupMetadata()
        .onFailure {
          Log.e(TAG, "Failed to get package list", it.reason)
          return@launch
        }

      val packages = backupMetadata.apps.map { it.packageName }

      restoreManager.runRestore(restorePlan, packages)
        .mapFailure {
          Log.e(TAG, "Failed to run restore", it)
        }
    }
  }
}
