package com.stevesoltys.aosp_backup.manager.backup.metadata

/**
 * Metadata for a backup.
 *
 * Encoded as JSON and stored in the backup file.
 *
 * @param backupFormat The backup format.
 * @param apps List of apps included in the backup.
 * @param apks List of APKs included in the backup.
 */
data class BackupMetadata(
  val backupFormat: BackupFormat,
  val apps: List<AppDataMetadata>,
  val apks: List<AppApkMetadata>
) {
  companion object {
    const val FILENAME = "metadata.json"
  }
}
