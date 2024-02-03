package com.stevesoltys.aosp_backup.manager.backup.restore

import android.content.pm.PackageInfo
import android.util.Log
import libcore.io.IoUtils.closeQuietly
import java.io.InputStream

data class RestoreManagerState(
  val packageList: List<PackageInfo>,
  val inputStream: InputStream?,
  var packageIndex: Int,
  var transportResponse: Int?
) {

  companion object {
    private val TAG = RestoreManagerState::class.java.simpleName
  }

  fun currentPackageName(): String {
    return packageList[packageIndex].packageName
  }

  fun hasMorePackages(): Boolean {
    return packageIndex < packageList.size
  }

  fun reset() {
    val packageNames = packageList.map { it.packageName }
    Log.i(TAG, "Resetting restore state, was previously on index: $packageIndex with packages: $packageNames.")

    closeQuietly(inputStream)
  }
}
