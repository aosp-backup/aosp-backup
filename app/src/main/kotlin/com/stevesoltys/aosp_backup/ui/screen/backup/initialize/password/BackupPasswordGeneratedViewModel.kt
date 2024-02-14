package com.stevesoltys.aosp_backup.ui.screen.backup.initialize.password

import androidx.lifecycle.ViewModel
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupPasswordGeneratedViewModel @Inject constructor(
  private val secretKeyManager: SecretKeyManager
) : ViewModel() {

  companion object {
    private val TAG = BackupPasswordGeneratedViewModel::class.java.simpleName
  }

  fun generateBackupPassword(): List<String> {
    return listOf(
      "potatoes", "oranges", "apples", "spoken", "password", "generated", "tomatoes", "cactus",
      "provider", "class", "dogs", "kotlin"
    )
  }
}
