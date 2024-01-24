package com.stevesoltys.aosp_backup.manager.location.saf

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.location.BackupLocation
import com.stevesoltys.aosp_backup.manager.location.BackupLocationType
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SAFBackupLocation(
  private val context: Context,
  private val configurationManager: ConfigurationManager
) : BackupLocation() {

  companion object {
    private val TAG = SAFBackupLocation::class.java.simpleName

    private const val PREF_BACKUP_LOCATION_URI = "backup_location_uri"
    private const val KEY_VALUE_TYPE = "kv"
    private const val FULL_TYPE = "full"
    private const val APK_TYPE = "apk"
  }

  private var backupOutputStream: ZipOutputStream? = null

  override fun locationSelectionActivity(
    activity: ComponentActivity,
    callback: () -> Unit
  ): ActivityResultLauncher<Unit> {
    return activity.registerForActivityResult(
      SAFBackupLocationContract(activity)
    ) { uri ->
      setBackupLocationUri(uri.toString())

      callback()
    }
  }

  override fun initializeLocation(): Result<Unit, Exception> = resultFrom { }

  override fun initializeBackup(): Result<Unit, Exception> = resultFrom {

    backupOutputStream?.let {
      Log.w(TAG, "Backup output stream is non-null, so previous backup was never finalized.")
    }

    val path = getBackupLocationUri()
      ?: throw IllegalStateException("Backup location is not initialized.")

    val fileUri = Uri.parse(path)
    val safOutputStream = context.contentResolver.openOutputStream(fileUri)
    backupOutputStream = ZipOutputStream(safOutputStream)
  }

  override fun keyValueBackupOutputStream(identifier: String): OutputStream {
    return outputStream(KEY_VALUE_TYPE, identifier)
  }

  override fun fullBackupOutputStream(identifier: String): OutputStream {
    return outputStream(FULL_TYPE, identifier)
  }

  override fun apkBackupOutputStream(identifier: String): OutputStream {
    return outputStream(APK_TYPE, identifier)
  }

  override fun finalizeBackup(): Result<Unit, Exception> = resultFrom {
    backupOutputStream?.flush()
    backupOutputStream?.close()
    backupOutputStream = null
  }

  override fun type(): BackupLocationType {
    return BackupLocationType.SHARED_STORAGE
  }

  override fun name(): String {
    return "Shared storage"
  }

  override fun icon(): Int {
    return R.drawable.ic_storage_location
  }

  override fun excludedPackages(): List<String> {
    val uri = getBackupLocationUri() ?: return emptyList()
    val authority = Uri.parse(uri).authority ?: return emptyList()

    val contentProviderPackages = context.packageManager.resolveContentProvider(authority, 0)
      ?.let {
        // Find all content providers with the given process name.
        context.packageManager.queryContentProviders(it.processName, 0, 0)
      }?.map { it.packageName }

    return contentProviderPackages.orEmpty()
  }

  private fun outputStream(type: String, identifier: String): OutputStream {
    val outputStream = backupOutputStream
      ?: throw IllegalStateException("Backup output stream is not initialized.")

    Log.i("SharedStorageBackupLocation", "Writing $type/$identifier")
    outputStream.putNextEntry(ZipEntry("$type/$identifier"))

    return object : OutputStream() {
      override fun write(b: Int) {
        outputStream.write(b)
      }

      override fun write(b: ByteArray) {
        outputStream.write(b)
      }

      override fun write(b: ByteArray, off: Int, len: Int) {
        outputStream.write(b, off, len)
      }

      override fun flush() {
        outputStream.flush()
      }

      override fun close() {
        outputStream.flush()
        outputStream.closeEntry()
      }
    }
  }

  private fun getBackupLocationUri(): String? {
    return configurationManager.getPreference(PREF_BACKUP_LOCATION_URI)
  }

  private fun setBackupLocationUri(uri: String) {
    configurationManager.setPreference(
      key = PREF_BACKUP_LOCATION_URI,
      value = uri
    )
  }
}
