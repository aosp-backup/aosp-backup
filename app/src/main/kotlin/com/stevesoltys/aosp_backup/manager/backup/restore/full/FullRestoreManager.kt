package com.stevesoltys.aosp_backup.manager.backup.restore.full

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
class FullRestoreManager @Inject constructor(
) {

  companion object {
    private const val MAX_CHUNK_SIZE = Short.MAX_VALUE.toInt()
  }

  fun getRestoreChunk(
    restoreManagerState: RestoreManagerState,
    restorePlan: RestorePlan,
    fileDescriptor: ParcelFileDescriptor
  ): Result<RestoreManagerState, Exception> = resultFrom {

    val inputStream = restoreManagerState.inputStream
      ?: restorePlan.getFullInputStream(
        packageName = restoreManagerState.currentPackageName()
      ).onFailure { return it }

    val bytesRead = ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor)
      .use {
        val buffer = ByteArray(MAX_CHUNK_SIZE)
        val bytesRead = inputStream.read(buffer)

        if (bytesRead != -1) {
          it.write(buffer, 0, bytesRead)
        } else {
          restoreManagerState.reset()
        }

        bytesRead
      }

    return restoreManagerState.copy(
      inputStream = null,
      transportResponse = bytesRead
    ).toSuccess()
  }
}
