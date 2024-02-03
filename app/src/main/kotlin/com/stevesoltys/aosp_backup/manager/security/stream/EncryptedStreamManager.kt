package com.stevesoltys.aosp_backup.manager.security.stream

import com.google.crypto.tink.subtle.AesGcmHkdfStreaming
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyManager
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyManager.Companion.MASTER_KEY_SIZE_BYTES
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyManager.Companion.MASTER_KEY_ALGORITHM
import com.stevesoltys.aosp_backup.manager.security.key.SecretKeyType
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EncryptedStreamManager @Inject constructor(
  private val secretKeyManager: SecretKeyManager
) {

  companion object {
    /**
     * The size of each chunk in the backup file.
     *
     * Currently set to 5MB.
     */
    private const val CHUNK_SIZE = 1024 * 1024 * 5

    /**
     * The offset for the first chunk.
     */
    private const val FIRST_CHUNK_OFFSET = 0
  }

  fun getBackupOutputStream(outputStream: OutputStream): OutputStream {
    secretKeyManager.storeKey(SecretKeyType.BACKUP, ByteArray(MASTER_KEY_SIZE_BYTES))

    val key = secretKeyManager.getKey(SecretKeyType.BACKUP)
      ?: throw IllegalStateException("App key is null.")

    val subKey = generateSubKey(key)

    val result = AesGcmHkdfStreaming(
      subKey,
      MASTER_KEY_ALGORITHM,
      MASTER_KEY_SIZE_BYTES,
      CHUNK_SIZE,
      FIRST_CHUNK_OFFSET
    )

    return result.newEncryptingStream(outputStream, ByteArray(0))
  }

  fun getRestoreInputStream(inputStream: InputStream): InputStream {
    secretKeyManager.storeKey(SecretKeyType.RESTORE, ByteArray(MASTER_KEY_SIZE_BYTES))

    val key = secretKeyManager.getKey(SecretKeyType.RESTORE)
      ?: throw IllegalStateException("App key is null.")

    val subKey = generateSubKey(key)

    val result = AesGcmHkdfStreaming(
      subKey,
      MASTER_KEY_ALGORITHM,
      MASTER_KEY_SIZE_BYTES,
      CHUNK_SIZE,
      FIRST_CHUNK_OFFSET
    )

    return result.newDecryptingStream(inputStream, ByteArray(0))
  }

  private fun generateSubKey(secretKey: SecretKey): ByteArray {
    return Hkdf.computeHkdf(
      macAlgorithm = MASTER_KEY_ALGORITHM,
      secretKey = secretKey,
      info = ByteArray(0),
      size = MASTER_KEY_SIZE_BYTES
    )
  }
}
