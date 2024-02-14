package com.stevesoltys.aosp_backup.ui.screen.backup.initialize

import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.location.InitializeLocationActivity
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.password.BackupPasswordGeneratedActivity
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupInitializationActivity : ComponentActivity() {

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
            Buttons()
          }
        }
      }
    }
  }

  @Composable
  private fun HeaderRow() {
    Column(
      horizontalAlignment = Alignment.Start,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Spacer(modifier = Modifier.height(64.dp))
      Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_quick_reference),
        contentDescription = null,
        modifier = Modifier
          .size(150.dp)
          .align(Alignment.CenterHorizontally)
      )
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        text = "Getting started",
        fontSize = 36.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        lineHeight = 40.sp
      )
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        text = "To begin setting up your scheduled backups, we'll provide you with a list of recovery words.\n\n" +
          "You should write these down on paper and keep them safe. " +
          "They will be used to recover your backup in the future, so don't lose them!",
        fontSize = 16.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }
  }

  @Composable
  private fun Buttons() {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Button(
        modifier = Modifier
          .fillMaxWidth(0.75f)
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = {
          startActivity(
            Intent(applicationContext, BackupPasswordGeneratedActivity::class.java)
          )
        },
      ) {
        Text(
          text = "Continue",
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }
      Spacer(modifier = Modifier.height(64.dp))
    }
  }
}
