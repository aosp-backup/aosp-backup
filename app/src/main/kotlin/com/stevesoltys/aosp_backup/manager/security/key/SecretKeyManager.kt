package com.stevesoltys.aosp_backup.manager.security.key

import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecretKeyManager @Inject constructor(

) {

  companion object {
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    const val MASTER_KEY_SIZE = 256
    const val MASTER_KEY_SIZE_BYTES = MASTER_KEY_SIZE / 8
    const val MASTER_KEY_ALGORITHM = "HmacSHA256"
  }

  private val keyStore by lazy {
    KeyStore.getInstance(ANDROID_KEY_STORE).apply {
      load(null)
    }
  }

  fun removeKey(keyType: SecretKeyType) {
    keyStore.deleteEntry(keyType.getAlias())
  }

  fun getKey(keyType: SecretKeyType): SecretKey? {
    val secretKeyEntry = keyStore.getEntry(
      keyType.getAlias(),
      null
    ) as? SecretKeyEntry

    return secretKeyEntry?.secretKey
  }

  fun storeKey(keyType: SecretKeyType, key: ByteArray) {
    removeKey(keyType)

    val secretKeySpec = SecretKeySpec(
      key,
      0,
      MASTER_KEY_SIZE_BYTES,
      MASTER_KEY_ALGORITHM
    )

    keyStore.setEntry(
      keyType.getAlias(),
      SecretKeyEntry(secretKeySpec),
      keyType.getProtection()
    )
  }

  fun hasKey(keyType: SecretKeyType): Boolean {
    return keyStore.containsAlias(keyType.getAlias())
  }
}
