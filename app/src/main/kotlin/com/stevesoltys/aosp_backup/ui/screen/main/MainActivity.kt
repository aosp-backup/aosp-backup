package com.stevesoltys.aosp_backup.ui.screen.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.stevesoltys.aosp_backup.manager.location.BackupLocationManager
import com.stevesoltys.aosp_backup.ui.screen.initialize.InitializationActivity
import com.stevesoltys.aosp_backup.ui.screen.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var backupLocationManager: BackupLocationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    if (!backupLocationManager.isBackupLocationInitialized()) {
      startActivity(
        Intent(applicationContext, InitializationActivity::class.java)
      )
    } else {
      startActivity(
        Intent(applicationContext, SettingsActivity::class.java)
      )
    }
  }
}
