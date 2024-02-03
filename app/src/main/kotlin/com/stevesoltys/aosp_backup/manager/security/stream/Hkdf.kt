package com.stevesoltys.aosp_backup.manager.security.stream

import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.SecretKey

/**
 * Modified version of [com.google.crypto.tink.subtle.Hkdf] which takes in a predefined [SecretKey].
 *
 * We need this because we store the secret key in the Android Keystore, and thus can't use the Tink implementation as is.
 */
object Hkdf {

  /**
   * Computes an HKDF.
   *
   * @param macAlgorithm the MAC algorithm used for computing the Hkdf. I.e., "HMACSHA1" or
   * "HMACSHA256".
   * @param secretKey the secret key to use.
   * @param info optional context and application specific information.
   * @param size The length of the generated pseudorandom string in bytes. The maximal size is
   * 255.DigestSize, where DigestSize is the size of the underlying HMAC.
   * @return size pseudorandom bytes.
   * @throws GeneralSecurityException if the `macAlgorithm` is not supported or if `size` is too large or if `salt` is not a valid key for macAlgorithm (which should not
   * happen since HMAC allows key sizes up to 2^64).
   */
  @Throws(GeneralSecurityException::class)
  fun computeHkdf(
    macAlgorithm: String?, secretKey: SecretKey, info: ByteArray?, size: Int
  ): ByteArray {
    val mac = Mac.getInstance(macAlgorithm)
    if (size > 255 * mac.macLength) {
      throw GeneralSecurityException("size too large")
    }
    val result = ByteArray(size)
    var ctr = 1
    var pos = 0
    mac.init(secretKey)
    var digest = ByteArray(0)
    while (true) {
      mac.update(digest)
      mac.update(info)
      mac.update(ctr.toByte())
      digest = mac.doFinal()
      if (pos + digest.size < size) {
        System.arraycopy(digest, 0, result, pos, digest.size)
        pos += digest.size
        ctr++
      } else {
        System.arraycopy(digest, 0, result, pos, size - pos)
        break
      }
    }
    return result
  }
}
