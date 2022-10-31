package elfak.mosis.tourguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.ButtonComponent
import elfak.mosis.tourguide.ui.components.LogoComponent

//region ui sizes
val buttonWidth = 230.dp
val logoSize  = 150.dp
val paddingTop = 40.dp
val paddingBottom = 100.dp
val btnPaddingTop = 100.dp
//endregion

@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = paddingTop, bottom = paddingBottom),
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
                    stringResource(id = R.string.home_screen_subtitle),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
            }
            //Buttons
            Column(modifier = Modifier.padding(top = btnPaddingTop)) {
                //Login
                ButtonComponent(
                    text = "Login",
                    width = buttonWidth,
                    onClick = {
                        //Login Action
                    }
                )
                Spacer(modifier = Modifier.height(15.dp))
                //Register
                ButtonComponent(
                    text = "Register",
                    width = buttonWidth,
                    onClick = {
                        //Register Action
                    }
                )
            }
        }
    }
    //Image - travelers
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            painter = painterResource(id = R.drawable.travelers),
            contentDescription = stringResource(id = R.string.travelers_description),
        )
    }
}

