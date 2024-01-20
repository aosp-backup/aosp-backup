package com.stevesoltys.aosp_backup.manager.storage.location

import android.content.Intent

abstract class BackupLocation {

  abstract fun selectionIntent(): Intent

  abstract fun name(): String

  abstract fun type(): BackupLocationType

  abstract fun icon(): Int
}
