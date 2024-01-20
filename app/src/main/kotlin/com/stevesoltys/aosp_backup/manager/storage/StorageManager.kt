package com.stevesoltys.aosp_backup.manager.storage

import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.storage.location.BackupLocation
import com.stevesoltys.aosp_backup.manager.storage.location.BackupLocationType
import javax.inject.Inject
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
class StorageManager @Inject constructor(
  private val configurationManager: ConfigurationManager,
  private val backupLocations: List<BackupLocation>,
) {

  companion object {
    private const val PREF_BACKUP_LOCATION_TYPE = "backup_location_type"
  }

  fun backupLocations(): List<BackupLocation> {
    return backupLocations
  }

  fun backupLocation(): BackupLocation? {
    val locationType = configurationManager.getPreference(PREF_BACKUP_LOCATION_TYPE) ?: return null

    return backupLocations.firstOrNull {
      it.type() == BackupLocationType.valueOf(locationType)
    }
  }

  fun isBackupLocationInitialized(): Boolean {
    return backupLocation() != null
  }
}
