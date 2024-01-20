package com.stevesoltys.aosp_backup.ui.screen.initialize

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevesoltys.aosp_backup.ui.screen.initialize.location.InitializeLocationActivity
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitializationActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    actionBar?.hide()
    setContent { Content() }
  }

  @Preview(uiMode = UI_MODE_NIGHT_YES)
  @Composable
  private fun Content() {
    AppTheme {
      Surface {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            HeaderRow()
            ContinueButton()
          }
        }
      }
    }
  }

  @Composable
  private fun HeaderRow() {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Spacer(modifier = Modifier.height(64.dp))
      Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
          .size(125.dp)
          .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(16.dp)
          )
      )
      Spacer(modifier = Modifier.height(48.dp))
      Text(
        text = "Backups",
        fontSize = 48.sp,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(48.dp))
      Text(
        text = "Welcome to the backup initialization process!\n\n" +
          "We will begin by selecting where you'd like to store your backups. " +
          "This could be in your local storage, or on a USB device like a flash drive.",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
      )
    }
  }

  @Composable
  private fun ContinueButton() {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Button(
        modifier = Modifier
          .fillMaxWidth(0.75f)
          .height(48.dp),
        onClick = {
          startActivity(
            Intent(applicationContext, InitializeLocationActivity::class.java)
          )
        },
      ) {
        Text(
          text = "Get Started"
        )
      }
      Spacer(modifier = Modifier.height(64.dp))
    }
  }
}
