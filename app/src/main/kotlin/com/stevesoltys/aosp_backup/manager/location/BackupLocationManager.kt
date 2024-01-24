package com.stevesoltys.aosp_backup.manager.location

import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import javax.inject.Inject
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
class BackupLocationManager @Inject constructor(
  private val configurationManager: ConfigurationManager,
  private val backupLocations: List<BackupLocation>,
  private val systemBackupManager: SystemBackupManager
) {

  companion object {
    const val PREF_BACKUP_LOCATION_TYPE = "backup_location_type"
  }

  fun backupLocationTypes(): List<BackupLocation> {
    return backupLocations
  }

  /**
   * Set the current backup location type, which should have already been initialized by now.
   *
   * This will also call the system backup manager to initialize the backup location.
   */
  fun setBackupLocationType(backupLocation: BackupLocation) {
    configurationManager.setPreference(
      PREF_BACKUP_LOCATION_TYPE,
      backupLocation.type().name
    )

    systemBackupManager.initializeBackupLocation()
  }

  fun backupLocationType(): BackupLocation? {
    val locationType = configurationManager.getPreference(PREF_BACKUP_LOCATION_TYPE) ?: return null

    return backupLocations.firstOrNull {
      it.type() == BackupLocationType.valueOf(locationType)
    }
  }

  fun isBackupLocationInitialized(): Boolean {
    return backupLocationType() != null
  }
}
