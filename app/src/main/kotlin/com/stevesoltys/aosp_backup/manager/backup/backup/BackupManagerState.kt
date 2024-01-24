package com.stevesoltys.aosp_backup.manager.backup.backup

import android.content.pm.PackageInfo
import android.os.ParcelFileDescriptor
import android.util.Log
import libcore.io.IoUtils.closeQuietly
import java.io.InputStream
import java.io.OutputStream

data class BackupManagerState(
  val currentPackage: PackageInfo,
  val inputStream: InputStream,
  val inputSocket: ParcelFileDescriptor,
  val outputStream: OutputStream,
  val fullBackup: Boolean
) {

  companion object {
    private val TAG = BackupManagerState::class.java.simpleName
  }

  fun reset() {
    Log.i(TAG, "Resetting backup state, was previously: ${currentPackage.packageName}")

    outputStream.flush()
    closeQuietly(outputStream)

    closeQuietly(inputStream)
    closeQuietly(inputSocket)
  }
}
