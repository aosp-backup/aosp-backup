package com.stevesoltys.aosp_backup.ui.screen.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.ui.screen.backup.BackupActivity
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.location.InitializeLocationActivity
import com.stevesoltys.aosp_backup.ui.screen.restore.RestoreActivity
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    actionBar?.hide()
    setContent { Content() }
  }

  @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
        imageVector = ImageVector.vectorResource(R.drawable.ic_backup),
        contentDescription = null,
        modifier = Modifier
          .size(250.dp)
          .align(Alignment.CenterHorizontally)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Backup & Restore",
        fontSize = 36.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        text = "Manage your backup schedule, or restore from a previous backup.",
        fontSize = 16.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(horizontal = 24.dp)
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
            Intent(applicationContext, BackupActivity::class.java)
          )
          finish()
        },
      ) {
        Icon(
          imageVector = ImageVector.vectorResource(R.drawable.ic_event_note),
          contentDescription = "Manage Backups",
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Manage Backups",
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        modifier = Modifier
          .fillMaxWidth(0.75f)
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {
          startActivity(
            Intent(applicationContext, RestoreActivity::class.java)
          )
          finish()
        },
      ) {
        Icon(
          imageVector = ImageVector.vectorResource(R.drawable.ic_settings_restore_backup),
          contentDescription = "Restore Backup",
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Restore Backup",
          color = MaterialTheme.colorScheme.onSecondaryContainer
        )
      }
      Spacer(modifier = Modifier.height(64.dp))
    }
  }
}
