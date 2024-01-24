package com.stevesoltys.aosp_backup.manager.backup.backup.full

import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManagerState

data class FullBackupResult(
  val result: Int,
  val state: BackupManagerState? = null
)
