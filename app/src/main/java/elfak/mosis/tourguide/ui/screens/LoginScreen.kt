package elfak.mosis.tourguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.ButtonComponent
import elfak.mosis.tourguide.ui.components.LogoWithTextComponent

@Composable
fun LoginScreen(navigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxWidth()) {
            // logo
            LogoWithTextComponent(text = stringResource(id = R.string.login), navigateBack)
            Spacer(modifier = Modifier.height(30.dp))

            // inputs
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicInputComponent(
                    label = stringResource(id = R.string.username) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = InputTypes.Text
                )
                Spacer(modifier = Modifier.height(15.dp))
                BasicInputComponent(
                    label = stringResource(id = R.string.password) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Default
                    ),
                    inputType = InputTypes.Password
                )
                Spacer(modifier = Modifier.heightIn(30.dp))

                // buttons
                ButtonComponent(text = stringResource(id = R.string.login), width = 230.dp, onClick = { /* implement later */})
                TextButton(
                    modifier = Modifier.padding(all = 10.dp),
                    onClick = { /*implement later */ }) {
                    Text(
                        textDecoration = TextDecoration.Underline,
                        text = stringResource(id = R.string.forgot_password)
                    )
                }
            }
        }

        //Image - travelers
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.travelers),
                contentDescription = stringResource(id = R.string.travelers_description),
            )
        }
    }
}