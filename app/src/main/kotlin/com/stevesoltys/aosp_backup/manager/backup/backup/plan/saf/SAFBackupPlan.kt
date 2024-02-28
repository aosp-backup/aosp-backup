package com.stevesoltys.aosp_backup.manager.backup.backup.plan.saf

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.manager.configuration.ConfigurationManager
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlan
import com.stevesoltys.aosp_backup.manager.backup.backup.plan.BackupPlanType
import com.stevesoltys.aosp_backup.manager.backup.metadata.BackupFormat
import com.stevesoltys.aosp_backup.manager.backup.metadata.BackupMetadata
import com.stevesoltys.aosp_backup.manager.backup.metadata.toApkMetadata
import com.stevesoltys.aosp_backup.manager.backup.metadata.toAppDataMetadata
import com.stevesoltys.aosp_backup.manager.package_.PackageManager
import com.stevesoltys.aosp_backup.manager.security.stream.EncryptedStreamManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SAFBackupPlan @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val configurationManager: ConfigurationManager,
  private val encryptedStreamManager: EncryptedStreamManager,
  private val packageManager: PackageManager
) : BackupPlan() {

  companion object {
    private val TAG = SAFBackupPlan::class.java.simpleName

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
      SAFBackupPlanContract(activity)
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
      ?: throw IOException("Could not open output stream for URI: $fileUri")

    val backupOutputStream = encryptedStreamManager.getBackupOutputStream(safOutputStream)

    this.backupOutputStream = ZipOutputStream(backupOutputStream)
    writeMetadata()
  }

  private fun writeMetadata() {
    val appMetadata = packageManager.getAppDataEligiblePackages(this).map { it.toAppDataMetadata() }
    val apkMetadata = packageManager.getApkEligiblePackages().map { it.toApkMetadata() }

    val metadata = BackupMetadata(
      backupFormat = BackupFormat.ZIP_V1,
      apps = appMetadata,
      apks = apkMetadata
    )

    val mapper = jacksonObjectMapper()
    val metadataEntry = ZipEntry(BackupMetadata.FILENAME)
    backupOutputStream?.putNextEntry(metadataEntry)
    backupOutputStream?.write(mapper.writeValueAsBytes(metadata))
    backupOutputStream?.closeEntry()
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

  override fun type(): BackupPlanType {
    return BackupPlanType.SHARED_STORAGE
  }

  override fun name(): String {
    return "Shared Storage"
  }

  override fun icon(): Int {
    return R.drawable.ic_storage_location
  }

  override fun excludedPackages(): List<String> {
    val uri = getBackupLocationUri() ?: return emptyList()
    val authority = Uri.parse(uri).authority ?: return emptyList()
    val contentProviderPackage = context.packageManager.resolveContentProvider(authority, 0)

    return listOfNotNull(contentProviderPackage?.packageName)
  }

  private fun outputStream(type: String, identifier: String): OutputStream {
    val outputStream = backupOutputStream
      ?: throw IllegalStateException("Backup output stream is not initialized.")

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
