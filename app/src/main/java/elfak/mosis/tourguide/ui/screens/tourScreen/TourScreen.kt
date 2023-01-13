package elfak.mosis.tourguide.ui.screens.tourScreen

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun TourScreen(navigateToWelcome: () -> Unit) {
    Text(text = "Tour Screen")
    Button(onClick = { navigateToWelcome() }) {
        Text(text = "To welcome")

    }
}