package com.stevesoltys.aosp_backup.manager.backup.restore.plan.saf

import android.app.backup.RestoreSet
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.manager.backup.metadata.BackupMetadata
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.PackageDataDescription
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.PackageDataType
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.PackageDataType.KEY_VALUE
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlan
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlanType
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.saf.SAFRestoreSet.SAF_RESTORE_SET
import com.stevesoltys.aosp_backup.manager.security.stream.EncryptedStreamManager
import com.stevesoltys.aosp_backup.util.AOSP.PACKAGE_MANAGER_SENTINEL
import com.stevesoltys.aosp_backup.util.toSuccess
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SAFRestorePlan @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val encryptedStreamManager: EncryptedStreamManager
) : RestorePlan() {

  companion object {
    private val TAG = SAFRestorePlan::class.java.simpleName
  }

  private lateinit var restoreInputStream: ZipInputStream

  private lateinit var restoreUri: Uri

  private lateinit var backupMetadata: BackupMetadata

  private lateinit var magicPackageManagerData: ByteArray

  private var currentEntry: ZipEntry? = null

  override fun locationSelectionActivity(
    activity: ComponentActivity,
    callback: (Boolean) -> Unit
  ): ActivityResultLauncher<Unit> {
    return activity.registerForActivityResult(
      SAFRestoreContract(activity)
    ) { resultUri ->
      resultUri?.let { restoreUri = it }

      callback(resultUri != null)
    }
  }

  private fun getMagicPackageManagerData(): ByteArray {
    return magicPackageManagerData
  }

  override fun getBackupMetadata(): Result<BackupMetadata, Exception> = resultFrom {

    if (::backupMetadata.isInitialized) {
      return backupMetadata.toSuccess()
    }

    Log.i(TAG, "Reading package list from backup.")
    initializeInputStream()

    do {
      currentEntry = restoreInputStream.nextEntry

      if (currentEntry?.name == BackupMetadata.FILENAME) {
        val metadata = restoreInputStream.readAllBytes()
        val objectMapper = jacksonObjectMapper()

        backupMetadata = objectMapper.readValue(metadata, BackupMetadata::class.java)
        break
      }

    } while (currentEntry != null)

    restoreInputStream.close()

    Log.i(TAG, "Found ${backupMetadata.apps.size} packages in backup.")
    return backupMetadata.toSuccess()
  }

  override fun getRestoreSets(): Result<List<RestoreSet>, Exception> {
    return listOf(
      SAF_RESTORE_SET
    ).toSuccess()
  }

  override fun getCurrentRestoreSet(): Result<RestoreSet, Exception> {
    return SAF_RESTORE_SET
      .toSuccess()
  }

  override fun getKeyValueInputStream(packageName: String): Result<InputStream, Exception> {

    if (packageName == PACKAGE_MANAGER_SENTINEL) {
      return getMagicPackageManagerData()
        .inputStream()
        .toSuccess()
    }

    return getInputStream().toSuccess()
  }

  override fun getFullInputStream(packageName: String): Result<InputStream, Exception> {
    return getInputStream().toSuccess()
  }

  private fun getInputStream(): InputStream {
    return object : InputStream() {
      override fun read(): Int {
        return restoreInputStream.read()
      }

      override fun read(b: ByteArray): Int {
        return restoreInputStream.read(b)
      }
    }
  }

  override fun initializeRestore(): Result<Unit, Exception> = resultFrom {
    initializeInputStream()
  }

  private fun initializeInputStream() {

    if (::restoreInputStream.isInitialized) {
      restoreInputStream.close()
    }

    val safInputStream = context.contentResolver.openInputStream(restoreUri)
      ?: throw IllegalStateException("Failed to open input stream for $restoreUri")

    val cipherInputStream = encryptedStreamManager.getRestoreInputStream(safInputStream)

    Log.i(TAG, "Initialized cipher input stream.")
    restoreInputStream = ZipInputStream(cipherInputStream)
  }

  override fun getPackageDescription(packageName: String): Result<PackageDataDescription?, Exception> = resultFrom {

    // If the requested package is the package manager sentinel, return a package data description for it, since we have it cached.
    if (packageName == PACKAGE_MANAGER_SENTINEL) {
      return PackageDataDescription(
        type = KEY_VALUE,
        packageName = PACKAGE_MANAGER_SENTINEL
      ).toSuccess()
    }

    val entryName = currentEntry?.name ?: return@resultFrom null

    // Get the type and identifier from the path.
    val type = PackageDataType.fromPath(entryName.substringBefore('/'))

    // Only restore if the entry is app data.
    if (type.isAppData()) {
      val identifier = entryName.substringAfter('/')

      return PackageDataDescription(
        packageName = identifier,
        type = type
      ).toSuccess()
    }

    null
  }

  override fun nextRestorePackage(): Result<PackageDataDescription?, Exception> = resultFrom {
    Log.i(TAG, "Reading next restore package.")
    currentEntry = restoreInputStream.nextEntry

    while (currentEntry != null) {
      Log.i(TAG, "Found entry ${currentEntry!!.name}")

      // Return the current package data description if it is an app data entry.
      // We can provide an empty string as the package name since we'll use the entry name to determine the package name.
      getPackageDescription("")
        .onFailure { return it }
        ?.let { return@resultFrom it }

      currentEntry = try {
        restoreInputStream.nextEntry

      } catch (e: Exception) {
        Log.e(TAG, "Error reading next entry.", e)
        null
      }
    }

    return@resultFrom null
  }

  override fun finalizeRestore(): Result<Unit, Exception> = resultFrom {
    restoreInputStream.close()
  }

  override fun excludedPackages(): List<String> {
    return emptyList()
  }

  override fun type(): RestorePlanType {
    return RestorePlanType.STORAGE_ACCESS_FRAMEWORK
  }

  override fun name(): String {
    return "Shared Storage"
  }

  override fun icon(): Int {
    return R.drawable.ic_storage_location
  }
}
