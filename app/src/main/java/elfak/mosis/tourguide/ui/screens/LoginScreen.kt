package elfak.mosis.tourguide.ui.screens

import androidx.compose.runtime.Composable
import elfak.mosis.tourguide.ui.components.CustomWelcomeScreenLogoComponent

@Composable
fun LoginScreen(navigateBack: () -> Unit) {
    CustomWelcomeScreenLogoComponent(text = "Login", navigateBack)
}