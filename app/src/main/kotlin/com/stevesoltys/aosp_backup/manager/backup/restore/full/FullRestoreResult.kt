package com.stevesoltys.aosp_backup.manager.backup.restore.full

import com.stevesoltys.aosp_backup.manager.backup.restore.RestoreManagerState

data class FullRestoreResult(
  val result: Int,
  val state: RestoreManagerState? = null
)
