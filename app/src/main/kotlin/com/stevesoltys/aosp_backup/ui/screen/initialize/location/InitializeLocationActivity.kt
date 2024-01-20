package com.stevesoltys.aosp_backup.ui.screen.initialize.location

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.stevesoltys.aosp_backup.manager.storage.StorageManager
import com.stevesoltys.aosp_backup.manager.storage.location.BackupLocation
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InitializeLocationActivity : ComponentActivity() {

  @Inject
  lateinit var storageManager: StorageManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    setContent {
      Content(
        backupLocations = storageManager.backupLocations()
      )
    }
  }

  @Preview(uiMode = UI_MODE_NIGHT_YES)
  @Composable
  private fun Content(
    backupLocations: List<BackupLocation> = emptyList()
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
            modifier = Modifier.fillMaxSize()
          ) {
            BackupLocationHeader()
            BackupLocationList(backupLocations)
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
        text = "Select Backup Location",
        fontSize = 36.sp,
        textAlign = TextAlign.Center,
        lineHeight = 36.sp,
      )
    }
  }

  @Composable
  fun BackupLocationList(backupLocations: List<BackupLocation>) {
    Spacer(modifier = Modifier.height(48.dp))
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      items(backupLocations) { location ->
        BackupLocationRow(location)
      }
    }
  }

  @Composable
  fun BackupLocationRow(location: BackupLocation) {
    Button(
      modifier = Modifier
        .fillMaxWidth(0.75f)
        .height(64.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
      ),
      onClick = {
        startActivity(location.selectionIntent())
      },
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = location.icon()),
        contentDescription = "Storage Location Icon",
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.size(36.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = location.name(),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        fontSize = 18.sp,
      )
    }
  }
}
