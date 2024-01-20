package com.stevesoltys.aosp_backup.transport

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.stevesoltys.aosp_backup.manager.backup.BackupManager
import com.stevesoltys.aosp_backup.manager.storage.StorageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppBackupTransportService @Inject constructor(
  private val backupManager: BackupManager,
  private val storageManager: StorageManager
) : Service() {

  private var transport: AppBackupTransport? = null

  override fun onCreate() {
    super.onCreate()

    transport = AppBackupTransport(
      context = applicationContext,
      backupManager = backupManager,
      storageManager = storageManager
    )
  }

  override fun onBind(intent: Intent?): IBinder {
    return transport?.binder ?: throw IllegalStateException("Transport not initialized")
  }

  override fun onDestroy() {
    super.onDestroy()
    transport = null
  }
}
