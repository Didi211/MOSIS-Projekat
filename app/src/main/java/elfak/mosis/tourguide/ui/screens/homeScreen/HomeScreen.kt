package elfak.mosis.tourguide.ui.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navigateToWelcome: () -> Unit,
    navigateToTour: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HomeScreen")
        Button(onClick = {
            navigateToTour()
        }) {
            Text(text = "Create Tour")
        }
        Button(onClick = {
            viewModel.logout()
            navigateToWelcome()
        } ) {
            Text(text = "Logout")
        }
    }
}