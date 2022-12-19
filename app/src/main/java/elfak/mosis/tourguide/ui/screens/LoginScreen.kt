package elfak.mosis.tourguide.ui.screens

import androidx.compose.runtime.Composable
import elfak.mosis.tourguide.ui.components.LogoWithTextComponent

@Composable
fun LoginScreen(navigateBack: () -> Unit) {
    LogoWithTextComponent(text = "Login", navigateBack)
}