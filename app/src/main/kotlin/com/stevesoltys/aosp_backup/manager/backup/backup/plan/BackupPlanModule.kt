package com.stevesoltys.aosp_backup.manager.backup.backup.plan

import android.content.Context
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.saf.SAFBackupPlan
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BackupPlanModule {

  @Provides
  @Singleton
  fun backupLocations(
    safBackupPlan: SAFBackupPlan
  ): List<BackupPlan> {

    return listOf(
      safBackupPlan
    )
  }
}
