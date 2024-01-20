package com.stevesoltys.aosp_backup.manager.storage.location

import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.storage.location.impl.SharedStorageBackupLocation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BackupLocationModule {

  @Provides
  @Singleton
  fun backupLocations(
    configurationManager: ConfigurationManager
  ): List<BackupLocation> {
    return listOf(
      SharedStorageBackupLocation(configurationManager)
    )
  }
}
