package com.stevesoltys.aosp_backup.manager.location

import android.content.Context
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.location.saf.SAFBackupLocation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BackupLocationModule {

  @Provides
  @Singleton
  fun backupLocations(
    @ApplicationContext
    context: Context,
    configurationManager: ConfigurationManager
  ): List<BackupLocation> {
    return listOf(
      SAFBackupLocation(context, configurationManager)
    )
  }
}
