package com.stevesoltys.aosp_backup.manager.backup

import android.content.Context.BACKUP_SERVICE
import android.app.backup.IBackupManager
import android.os.ServiceManager
import javax.inject.Singleton

@Singleton
class BackupManager {

  private val systemBackupManager = IBackupManager.Stub.asInterface(
    ServiceManager.getService(BACKUP_SERVICE)
  )


}
