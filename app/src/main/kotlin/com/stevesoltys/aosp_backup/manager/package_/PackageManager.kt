package com.stevesoltys.aosp_backup.manager.package_

import android.content.Context
import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_INSTRUMENTATION
import android.util.Log
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageManager @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val backupPlanManager: BackupPlanManager
) {

  private val systemPackageManager: PackageManager = context.packageManager

  companion object {
    private val TAG = PackageManager::class.java.simpleName
  }

  fun getAppDataEligiblePackages(): List<PackageInfo> {
    val backupLocation = backupPlanManager.backupLocationType()

    // Exclude our package, as well as any packages excluded by the backup location.
    val excludedPackages = listOf(
      context.packageName
    ) + backupLocation?.excludedPackages().orEmpty()

    Log.i(TAG, "Excluding $excludedPackages package(s) from backup.")

    return systemPackageManager.getInstalledPackages(0)
      .filter { pkgs ->
        !excludedPackages.contains(pkgs.packageName) && !isSystemApp(pkgs)
      }
  }

  fun getApkEligiblePackages(): List<PackageInfo> {
    val excludedPackages = listOf(context.packageName)

    return systemPackageManager.getInstalledPackages(GET_INSTRUMENTATION)
      .filter { pkg ->
        !excludedPackages.contains(pkg.packageName) && !shouldIgnoreSystemApp(pkg)
      }
  }

  private fun shouldIgnoreSystemApp(pkg: PackageInfo): Boolean {
    return isSystemApp(pkg) && !isUpdatedSystemApp(pkg)
  }

  private fun isSystemApp(pkg: PackageInfo): Boolean {
    return pkg.applicationInfo.flags and FLAG_SYSTEM != 0
  }

  private fun isUpdatedSystemApp(pkg: PackageInfo): Boolean {
    return pkg.applicationInfo.flags and FLAG_UPDATED_SYSTEM_APP != 0
  }
}
