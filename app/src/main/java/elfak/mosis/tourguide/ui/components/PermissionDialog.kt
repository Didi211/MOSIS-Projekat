package elfak.mosis.tourguide.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import es.dmoral.toasty.Toasty

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissionsDialog(permissionsState: MultiplePermissionsState, permissionTextOnDenied: String) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            if (!permissionsState.allPermissionsGranted) {
                Toasty.info(
                    context,
                    permissionTextOnDenied,
                    Toast.LENGTH_LONG,
                    true
                ).show()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDialog(permissionState: PermissionState, permissionTextOnDenied: String) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            if (!permissionState.status.isGranted) {
                Toasty.info(
                    context,
                    permissionTextOnDenied,
                    Toast.LENGTH_LONG,
                    true
                ).show()
            }
        }
    }
}
