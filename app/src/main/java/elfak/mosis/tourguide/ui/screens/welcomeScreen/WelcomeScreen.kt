package elfak.mosis.tourguide.ui.screens.welcomeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.buttons.ButtonComponent
import elfak.mosis.tourguide.ui.components.images.LogoComponent
import elfak.mosis.tourguide.ui.components.images.TravelersImage

// NOTE: should be placed somewhere to be widely accessible - like constants or so
//region ui sizes
private val buttonWidth = 230.dp
private val logoSize  = 150.dp
private val paddingTop = 40.dp
private val paddingBottom = 100.dp
private val btnPaddingTop = 100.dp
//endregion

@Composable
fun WelcomeScreen(
    navigateToLogin: () -> Unit = {},
    navigateToRegister: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingTop, bottom = paddingBottom)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                //Logo, Title and Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    //Logo Component
                    LogoComponent(logoSize)
                    //Subtitle
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        stringResource(id = R.string.welcome_screen_subtitle),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary
                    )
                }
                //Buttons
                Column(modifier = Modifier.padding(top = btnPaddingTop)) {
                    //Login
                    ButtonComponent(
                        text = stringResource(id = R.string.login),
                        width = buttonWidth,
                        onClick = navigateToLogin
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    //Register
                    ButtonComponent(
                        text = stringResource(id = R.string.register),
                        width = buttonWidth,
                        onClick = navigateToRegister
                    )
                }
            }

        }
        TravelersImage(modifier = Modifier.align(Alignment.BottomEnd))
    }
}

