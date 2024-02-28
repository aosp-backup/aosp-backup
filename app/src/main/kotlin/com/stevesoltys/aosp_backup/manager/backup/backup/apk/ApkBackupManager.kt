package com.stevesoltys.aosp_backup.manager.backup.backup.apk

import android.content.pm.PackageInfo
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanManager
import com.stevesoltys.aosp_backup.manager.package_.PackageManager
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkBackupManager @Inject constructor(
  private val packageManager: PackageManager,
  private val backupPlanManager: BackupPlanManager
) {

  companion object {
    private val TAG = ApkBackupManager::class.java.simpleName
  }

  fun backupApks(): Result<Unit, Exception> = resultFrom {
    val apkEligiblePackages = packageManager.getApkEligiblePackages()
    val backupLocation = backupPlanManager.backupPlan()
      ?: throw IllegalStateException("Backup location is not initialized.")

    Log.i(TAG, "Backing up ${apkEligiblePackages.size} APK(s).")

    apkEligiblePackages.forEach { packageInfo ->
      val apkFiles = getApkFiles(packageInfo)

      apkFiles.forEach { (name, apkInputStream) ->
        backupLocation.apkBackupOutputStream("${packageInfo.packageName}/${name}")
          .use { apkInputStream.copyTo(it) }
      }
    }
  }

  private fun getApkFiles(packageInfo: PackageInfo): MutableMap<String, InputStream> {
    val result = mutableMapOf<String, InputStream>()

    val baseApkFile = File(packageInfo.applicationInfo.sourceDir)
    result[baseApkFile.name] = baseApkFile.inputStream()

    packageInfo.applicationInfo.splitSourceDirs?.forEach {
      val file = File(it)

      result[file.name] = file.inputStream()
    }

    return result
  }
}
