package com.stevesoltys.aosp_backup.transport

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AppBackupTransportService : Service() {

  private var transport: AppBackupTransport? = null

  override fun onCreate() {
    super.onCreate()
    transport = AppBackupTransport(applicationContext)
  }

  override fun onBind(intent: Intent?): IBinder {
    return transport?.binder ?: throw IllegalStateException("Transport not initialized")
  }

  override fun onDestroy() {
    super.onDestroy()
    transport = null
  }
}
