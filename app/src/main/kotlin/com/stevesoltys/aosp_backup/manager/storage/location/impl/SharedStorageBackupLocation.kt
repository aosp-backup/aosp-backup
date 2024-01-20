package com.stevesoltys.aosp_backup.manager.storage.location.impl

import android.content.Intent
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.storage.location.BackupLocation
import com.stevesoltys.aosp_backup.manager.storage.location.BackupLocationType

class SharedStorageBackupLocation(
  private val configurationManager: ConfigurationManager
) : BackupLocation() {

  companion object {
    private const val PREF_BACKUP_LOCATION_URI = "backup_location_uri"
  }

  override fun selectionIntent(): Intent {
    TODO("Not yet implemented")
  }

  override fun name(): String {
    return "Shared storage"
  }

  override fun icon(): Int {
    return R.drawable.ic_storage_location
  }

  override fun type(): BackupLocationType {
    return BackupLocationType.SHARED_STORAGE
  }
}
