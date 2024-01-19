package com.stevesoltys.aosp_backup.ui.activity

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val hmm = ComponentName(applicationContext, javaClass).flattenToShortString()

    setContent {
      Box(contentAlignment = Center, modifier = Modifier.fillMaxSize()) {
        Text("Hello World! $hmm", color = Color.White)
      }
    }
  }
}
