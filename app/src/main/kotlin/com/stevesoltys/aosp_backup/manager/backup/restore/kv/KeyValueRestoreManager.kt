package com.stevesoltys.aosp_backup.manager.backup.restore.kv

import android.os.ParcelFileDescriptor
import com.stevesoltys.aosp_backup.manager.backup.restore.RestoreManagerState
import com.stevesoltys.aosp_backup.manager.backup.restore.plan.RestorePlan
import com.stevesoltys.aosp_backup.util.toSuccess
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueRestoreManager @Inject constructor(
) {

  companion object {
    private const val MAX_CHUNK_SIZE = Short.MAX_VALUE.toInt()
  }

  fun getRestoreData(
      restoreManagerState: RestoreManagerState,
      restorePlan: RestorePlan,
      fileDescriptor: ParcelFileDescriptor
  ): Result<RestoreManagerState, Exception> = resultFrom {

    val inputStream = restorePlan.getKeyValueInputStream(
      packageName = restoreManagerState.currentPackageName()
    ).onFailure { return it }

    ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor).use {
      val buffer = ByteArray(MAX_CHUNK_SIZE)

      while (true) {
        val bytesRead = inputStream.read(buffer)

        if (bytesRead == -1) {
          break
        }

        it.write(buffer, 0, bytesRead)
      }
    }

    return restoreManagerState.copy(
      transportResponse = 0
    ).toSuccess()
  }
}
