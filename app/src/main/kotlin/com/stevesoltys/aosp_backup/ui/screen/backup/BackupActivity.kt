package com.stevesoltys.aosp_backup.ui.screen.backup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.BackupInitializationActivity
import com.stevesoltys.aosp_backup.ui.screen.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupActivity : ComponentActivity() {

  private val viewModel: BackupActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    if (!viewModel.isInitialized()) {
      startActivity(
        Intent(applicationContext, BackupInitializationActivity::class.java)
      )
    } else {
      startActivity(
        Intent(applicationContext, SettingsActivity::class.java)
      )
    }

    finish()
  }
}
