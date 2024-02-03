package com.stevesoltys.aosp_backup.manager.backup.restore.plan.saf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

class SAFRestoreContract(
  private val activity: ComponentActivity
) : ActivityResultContract<Unit, Uri?>() {

  @CallSuper
  override fun createIntent(context: Context, input: Unit): Intent {
    return Intent(Intent.ACTION_OPEN_DOCUMENT)
      .setType("*/*")
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

    // Make sure we have permanent read access to the URI
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

    activity.contentResolver.takePersistableUriPermission(result, flags)
    return result
  }
}
