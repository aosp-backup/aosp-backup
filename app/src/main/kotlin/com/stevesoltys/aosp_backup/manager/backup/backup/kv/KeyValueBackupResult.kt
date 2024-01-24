package com.stevesoltys.aosp_backup.manager.backup.backup.kv

import com.stevesoltys.aosp_backup.manager.backup.backup.BackupManagerState

data class KeyValueBackupResult(
  val result: Int,
  val state: BackupManagerState? = null
)
