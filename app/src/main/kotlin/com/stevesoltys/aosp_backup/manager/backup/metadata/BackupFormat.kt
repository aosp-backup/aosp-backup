package com.stevesoltys.aosp_backup.manager.backup.metadata

enum class BackupFormat(
  val identifier: String
) {
  ZIP_V1("zip-v1"),
  UNKNOWN("unknown")
}
