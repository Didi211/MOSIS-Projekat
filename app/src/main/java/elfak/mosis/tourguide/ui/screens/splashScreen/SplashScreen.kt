package elfak.mosis.tourguide.ui.screens.splashScreen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.components.images.LogoImage
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToWelcome: () -> Unit,
    viewModel: SplashScreenViewModel
) {
    val scaleLogo = remember { Animatable(0.0f) }

    //Animation
    LaunchedEffect(key1 = true) {
        scaleLogo.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(650, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )

        delay(500L)
        if (viewModel.isLoggedIn()) {
            navigateToHome()
        }
        else {
            navigateToWelcome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(bottom = 150.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            //Logo Component
            LogoImage(size = 180.dp, modifier = Modifier.scale(scaleLogo.value))
        }
    }
}