package com.stevesoltys.aosp_backup.manager.backup

import android.app.backup.IBackupManager
import android.app.backup.IBackupManagerMonitor
import android.app.backup.IBackupObserver
import android.app.backup.IRestoreObserver
import android.app.backup.IRestoreSession
import android.content.Context
import android.os.Bundle
import android.os.ServiceManager
import com.stevesoltys.aosp_backup.transport.AppBackupTransport
import com.stevesoltys.aosp_backup.util.AOSP
import com.stevesoltys.aosp_backup.util.AOSP.BACKUP_SERVICE
import com.stevesoltys.aosp_backup.util.toSuccess
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class used for interacting with the system [IBackupManager].
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
    val userId = AOSP.myUserId()

    val backupMonitor = object : IBackupManagerMonitor.Stub() {
      override fun onEvent(bundle: Bundle) {}
    }

    val result = systemBackupManager.requestBackupForUser(
      userId, packages.toTypedArray(), backupObserver, backupMonitor, 0
    )

    if (result != 0) {
      throw RuntimeException("Request backup failed for packages: '${packages}', result is: $result.")
    }
  }

  fun beginRestoreSession(): Result<IRestoreSession, Exception> = resultFrom {
    val userId = AOSP.myUserId()
    val transportName = AppBackupTransport.getTransportName(context)

    return systemBackupManager
      .beginRestoreSessionForUser(userId, null, transportName)
      ?.toSuccess() ?: throw IllegalStateException("Restore session is null.")
  }

  fun restorePackage(
    session: IRestoreSession,
    packageName: String,
    backupObserver: IRestoreObserver
  ): Result<Unit, Exception> = resultFrom {

    val result = session.restorePackage(packageName, backupObserver, null)

    if (result != 0) {
      throw RuntimeException("Restore package for '$packageName' failed, result is: $result.")
    }
  }

  /**
   * Tells the system to initialize the transport for the current user.
   */
  fun initializeBackupLocation(): Result<Unit, Exception> = resultFrom {
    val userId = AOSP.myUserId()
    val transportIds = arrayOf(
      AppBackupTransport.getTransportName(context)
    )

    systemBackupManager.initializeTransportsForUser(userId, transportIds, null)
  }
}
