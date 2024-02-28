package com.stevesoltys.aosp_backup.manager.backup.backup.plan

import com.stevesoltys.aosp_backup.manager.backup.SystemBackupManager
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import javax.inject.Inject
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
class BackupPlanManager @Inject constructor(
  private val configurationManager: ConfigurationManager,
  private val backupPlans: List<BackupPlan>,
  private val systemBackupManager: SystemBackupManager
) {

  companion object {
    const val BACKUP_PLAN_TYPE = "backup_location_type"
  }

  fun backupLocationTypes(): List<BackupPlan> {
    return backupPlans
  }

  /**
   * Set the current backup location type, which should have already been initialized by now.
   *
   * This will also call the system backup manager to initialize the backup location.
   */
  fun setBackupLocationType(backupPlan: BackupPlan) {
    configurationManager.setPreference(
      BACKUP_PLAN_TYPE,
      backupPlan.type().name
    )

    systemBackupManager.initializeBackupLocation()
  }

  fun backupPlan(): BackupPlan? {
    val locationType = configurationManager.getPreference(BACKUP_PLAN_TYPE) ?: return null

    return backupPlans.firstOrNull {
      it.type() == BackupPlanType.valueOf(locationType)
    }
  }

  fun isBackupLocationInitialized(): Boolean {
    return backupPlan() != null
  }
}
