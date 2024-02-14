package com.stevesoltys.aosp_backup.ui.screen.backup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManager
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyManager
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyType
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.forkhandles.result4k.mapFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupActivityViewModel @Inject constructor(
  private val secretKeyManager: SecretKeyManager
) : ViewModel() {

  companion object {
    private val TAG = BackupActivityViewModel::class.java.simpleName
  }

  fun isInitialized(): Boolean {

    secretKeyManager.removeKey(SecretKeyType.BACKUP)

    return secretKeyManager.hasKey(SecretKeyType.BACKUP)
      .also {
        if (!it) {
          Log.i(TAG, "Backup key not initialized")
        }
      }
  }
}
