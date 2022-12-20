package elfak.mosis.tourguide.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = DarkGreen,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = BgBlue,
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = DarkGreen,
    primaryVariant = Purple700,
    secondary = Orange,
    background = BgBlue,
    onPrimary = Color.White,
    onSecondary = Color.White

    /* Other default colors to override
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TourGuideTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = DarkGreen,
            darkIcons = false,
        )
        systemUiController.setNavigationBarColor(
            color = BgBlue
        )
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}