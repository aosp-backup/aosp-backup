package com.stevesoltys.aosp_backup.manager.backup.restore.plan.saf

import android.app.backup.BackupAgent
import android.app.backup.RestoreSet

object SAFRestoreSet {
  private const val NAME = "Backup"
  private const val DEVICE_NAME = "D2D"
  private const val TOKEN = 0L
  private const val TRANSPORT_FLAGS = BackupAgent.FLAG_DEVICE_TO_DEVICE_TRANSFER

  /**
   * We just use a static restore set for SAF, since we don't support multiple.
   */
  internal val SAF_RESTORE_SET = RestoreSet(NAME, DEVICE_NAME, TOKEN, TRANSPORT_FLAGS)
}
