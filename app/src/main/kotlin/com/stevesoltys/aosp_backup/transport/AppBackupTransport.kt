package com.stevesoltys.aosp_backup.transport

import android.app.backup.BackupAgent.FLAG_DEVICE_TO_DEVICE_TRANSFER
import android.app.backup.BackupTransport
import android.content.ComponentName
import android.content.Context

class AppBackupTransport(
  private val context: Context
) : BackupTransport() {

  companion object {
    private const val TRANSPORT_DIRECTORY_NAME = "com.stevesoltys.aosp_backup.transport.AppBackupTransport"
  }

  /**
   * The transport flags.
   *
   * We default to D2D transfers to provide the most coverage on application data.
   */
  override fun getTransportFlags(): Int {
    return FLAG_DEVICE_TO_DEVICE_TRANSFER
  }

  /**
   * The transport name.
   */
  override fun name(): String {
    return ComponentName(context, javaClass).flattenToShortString()
  }

  /**
   * The transport directory name.
   */
  override fun transportDirName(): String {
    return TRANSPORT_DIRECTORY_NAME
  }
}
