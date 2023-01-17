package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissionsDialog(permissionsState: MultiplePermissionsState, permissionTextOnDenied: String, buttonText: String) {
    Box(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            if (!permissionsState.allPermissionsGranted) {
                Text(
                    text = permissionTextOnDenied,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    permissionsState.launchMultiplePermissionRequest()
                }) {
                    Text(text = buttonText)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDialog(permissionState: PermissionState, permissionTextOnDenied: String, buttonText: String) {
    Box(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            if (!permissionState.status.isGranted) {
                Text(
                    text = permissionTextOnDenied,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    permissionState.launchPermissionRequest()
                }) {
                    Text(text = buttonText)
                }
            }
        }
    }
}
