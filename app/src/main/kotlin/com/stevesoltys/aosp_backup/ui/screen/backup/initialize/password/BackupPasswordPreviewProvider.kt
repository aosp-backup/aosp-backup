package com.stevesoltys.aosp_backup.ui.screen.backup.initialize.password

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class BackupPasswordPreviewProvider : PreviewParameterProvider<List<String>> {
  override val values = sequenceOf(
    listOf(
      "potatoes", "oranges", "apples", "spoken", "password", "generated", "tomatoes", "cactus",
      "provider", "class", "dogs", "kotlin"
    )
  )
}
