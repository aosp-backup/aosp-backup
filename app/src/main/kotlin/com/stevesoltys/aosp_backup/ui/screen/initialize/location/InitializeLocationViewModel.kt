package com.stevesoltys.aosp_backup.ui.screen.initialize.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.forkhandles.result4k.mapFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitializeLocationViewModel @Inject constructor(
  private val backupManager: BackupManager
) : ViewModel() {

  companion object {
    private val TAG = InitializeLocationViewModel::class.java.simpleName
  }

  fun runBackup() {
    viewModelScope.launch(Dispatchers.IO) {
      backupManager.runBackup()
        .mapFailure {
          Log.e(TAG, "Failed to run backup", it)
        }
    }
  }
}
