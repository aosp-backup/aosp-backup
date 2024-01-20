package com.stevesoltys.aosp_backup.ui.screen.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.stevesoltys.aosp_backup.manager.storage.StorageManager
import com.stevesoltys.aosp_backup.ui.screen.initialize.InitializationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var storageManager: StorageManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    if (!storageManager.isBackupLocationInitialized()) {
      startActivity(
        Intent(applicationContext, InitializationActivity::class.java)
      )
    }
  }
}
