package com.stevesoltys.aosp_backup.util

import android.app.backup.BackupManager
import android.content.Context
import android.os.UserHandle

/**
 * Constants that are available via hidden APIs, and won't have syntax highlighting in an IDE.
 *
 * @author Steve Soltys
 */
object AOSP {

  /**
   * Use with {@link #getSystemService(String)} to retrieve an
   * {@link android.app.backup.IBackupManager IBackupManager} for communicating
   * with the backup mechanism.
   * @hide
   *
   * @see #getSystemService(String)
   */
  const val BACKUP_SERVICE: String = Context.BACKUP_SERVICE

  /**
   * Use with requestBackup to force backup of package meta data. Typically you do not need to explicitly
   * request this be backed up as it is handled internally by the BackupManager. If you are requesting backups with
   * FLAG_NON_INCREMENTAL, this package won't automatically be backed up and you have to explicitly request for its backup.
   */
  const val PACKAGE_MANAGER_SENTINEL: String = BackupManager.PACKAGE_MANAGER_SENTINEL

  /**
   * Returns the user id of the current process
   *
   * @return user id of the current process
   */
  fun myUserId(): Int = UserHandle.myUserId()
}
