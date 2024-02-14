package com.stevesoltys.aosp_backup.ui.screen.restore

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.location.InitializeLocationEntry
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.forkhandles.result4k.mapFailure

@AndroidEntryPoint
class RestoreActivity : ComponentActivity() {

  private val restoreViewModel: RestoreViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    val backupLocations = restoreViewModel.getRestoreLocations().map {

      val launcher = it.locationSelectionActivity(this) { didChooseLocation ->
        if (didChooseLocation) {
          restoreViewModel.chooseRestoreLocation(it)

          Toast.makeText(this, "Restore location selected!", Toast.LENGTH_SHORT).show()
        }
      }

      InitializeLocationEntry(
        name = it.name(),
        icon = it.icon(),
        onClick = { launcher.launch(Unit) }
      )
    }

    setContent {
      Content(backupLocations = backupLocations)
    }
  }

  @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
  @Composable
  private fun Content(
    backupLocations: List<InitializeLocationEntry> = emptyList()
  ) {
    AppTheme {
      Surface {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
          ) {
            Column(modifier = Modifier.fillMaxSize()) {
              BackupLocationHeader()
              BackupLocationList(backupLocations)
              Button(onClick = {
                restoreViewModel.runRestore()
                  .mapFailure {
                    Toast.makeText(
                      this@RestoreActivity, "Failed to run restore!", Toast.LENGTH_SHORT
                    ).show()
                  }
              }) {
                Text(text = "Run Restore")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun BackupLocationHeader() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {
    Spacer(modifier = Modifier.height(64.dp))
    Icon(
      imageVector = ImageVector.vectorResource(id = R.drawable.ic_storage_location),
      contentDescription = "Storage Location Icon",
      tint = MaterialTheme.colorScheme.onSecondaryContainer,
      modifier = Modifier.size(150.dp)
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
      text = "Restore Backup",
      fontSize = 36.sp,
      textAlign = TextAlign.Center,
      lineHeight = 36.sp,
    )
  }
}

@Composable
fun BackupLocationList(backupLocations: List<InitializeLocationEntry>) {
  Spacer(modifier = Modifier.height(48.dp))
  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    items(backupLocations) { location ->
      BackupLocationRow(location)
    }
  }
}

@Composable
fun BackupLocationRow(location: InitializeLocationEntry) {
  Button(
    modifier = Modifier
      .fillMaxWidth(0.75f)
      .height(64.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ),
    onClick = { location.onClick() },
  ) {
    Icon(
      imageVector = ImageVector.vectorResource(id = location.icon),
      contentDescription = "Storage Location Icon",
      tint = MaterialTheme.colorScheme.onSecondaryContainer,
      modifier = Modifier.size(36.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = location.name,
      color = MaterialTheme.colorScheme.onSecondaryContainer,
      fontSize = 18.sp,
    )
  }
}
