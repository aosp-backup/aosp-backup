package com.stevesoltys.aosp_backup.manager.location.saf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_LOCAL_ONLY
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

class SAFBackupLocationContract(
  private val activity: ComponentActivity
) : ActivityResultContract<Unit, Uri?>() {

  @CallSuper
  override fun createIntent(context: Context, input: Unit): Intent {
    return Intent(Intent.ACTION_CREATE_DOCUMENT)
      .setType("application/zip")
      .apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(EXTRA_LOCAL_ONLY, true)

        addFlags(
          Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
      }
  }

  override fun getSynchronousResult(
    context: Context,
    input: Unit
  ): SynchronousResult<Uri?>? = null

  override fun parseResult(resultCode: Int, intent: Intent?): Uri? {

    // Make sure the result is valid
    val result = intent
      .takeIf { resultCode == Activity.RESULT_OK }?.data
      ?: return null

    // Make sure we have permanent read/write access to the URI
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
      Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    activity.contentResolver.takePersistableUriPermission(result, flags)
    return result
  }
}
