package com.stevesoltys.aosp_backup.manager.backup

import android.app.backup.BackupObserver
import android.app.backup.IBackupManager
import android.app.backup.IBackupManagerMonitor
import android.app.backup.IBackupObserver
import android.content.Context
import android.content.Context.BACKUP_SERVICE
import android.os.Bundle
import android.os.ServiceManager
import android.os.UserHandle
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManager
import com.stevesoltys.aosp_backup.manager.backup.backup.BackupProcessor
import com.stevesoltys.aosp_backup.transport.AppBackupTransport
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class used for interacting with the system backup manager.
 */
@Singleton
class SystemBackupManager @Inject constructor(
  @ApplicationContext
  private val context: Context
) {

  private val systemBackupManager = IBackupManager.Stub.asInterface(
    ServiceManager.getService(BACKUP_SERVICE)
  )

  /**
   * Tells the system to request a backup for the current user.
   */
  fun requestBackup(
    backupObserver: IBackupObserver,
    packages: List<String>
  ): Result<Unit, Exception> = resultFrom {
    val userId = UserHandle.myUserId()

    val backupMonitor = object : IBackupManagerMonitor.Stub() {
      override fun onEvent(bundle: Bundle) {}
    }

    systemBackupManager.requestBackupForUser(
      userId, packages.toTypedArray(), backupObserver, backupMonitor, 0
    )
  }

  /**
   * Tells the system to initialize the transport for the current user.
   */
  fun initializeBackupLocation(): Result<Unit, Exception> = resultFrom {
    val userId = UserHandle.myUserId()
    val transportIds = arrayOf(
      AppBackupTransport.getTransportName(context)
    )

    systemBackupManager.initializeTransportsForUser(userId, transportIds, null)
  }
}
