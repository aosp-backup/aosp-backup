package com.stevesoltys.aosp_backup.ui.screen.backup.initialize.password

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.stevesoltys.aosp_backup.R
import com.stevesoltys.aosp_backup.ui.screen.backup.initialize.location.InitializeLocationActivity
import com.stevesoltys.aosp_backup.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupPasswordGeneratedActivity : ComponentActivity() {

  private val viewModel: BackupPasswordGeneratedViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    actionBar?.hide()
    setContent { Content(viewModel.generateBackupPassword()) }
  }

  @Preview(uiMode = UI_MODE_NIGHT_YES)
  @Composable
  private fun Content(
    @PreviewParameter(BackupPasswordPreviewProvider::class)
    backupPassword: List<String>
  ) {
    AppTheme {
      Surface {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Header()
            Words(backupPassword)
            Button()
          }
        }
      }
    }
  }

  @Composable
  private fun Header() {
    Column(
      horizontalAlignment = Alignment.Start,
      modifier = Modifier.fillMaxWidth()
    ) {
      Spacer(modifier = Modifier.height(64.dp))
      Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_key),
        contentDescription = null,
        modifier = Modifier
          .size(175.dp)
          .align(Alignment.CenterHorizontally)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Recovery Code",
        fontSize = 36.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        lineHeight = 40.sp
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Write this down on paper!",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
      Spacer(modifier = Modifier.height(48.dp))
    }
  }

  @Composable
  private fun Words(backupPassword: List<String>) {
    Row(
      horizontalArrangement = Arrangement.SpaceAround,
      modifier = Modifier
        .fillMaxWidth()
    ) {
      (1..3).forEach { columnIndex ->
        Column(
          verticalArrangement = Arrangement.SpaceEvenly
        ) {
          (1..4).forEach { rowIndex ->
            // should read left to right
            val index = (rowIndex - 1) * 3 + columnIndex - 1

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.requiredWidth(120.dp)
            ) {
              Text(
                text = "${index + 1}. ",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = backupPassword[index],
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
            Spacer(modifier = Modifier.height(48.dp))
          }
        }
      }
    }
  }

  @Composable
  private fun Button() {
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
            Intent(applicationContext, InitializeLocationActivity::class.java)
          )
        },
      ) {
        Text(
          text = "Confirm code",
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
