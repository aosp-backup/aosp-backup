package com.stevesoltys.aosp_backup.ui.screen.restore

data class RestoreLocationEntry(
  val name: String,
  val icon: Int,
  val onClick: () -> Unit
)
