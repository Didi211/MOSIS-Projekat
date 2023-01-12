package elfak.mosis.tourguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.ButtonComponent
import elfak.mosis.tourguide.ui.components.LogoWithTextComponent
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginViewModel
import elfak.mosis.tourguide.ui.screens.registerScreen.RegisterViewModel

@Composable
fun RegisterScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: RegisterViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // logo
            LogoWithTextComponent(
                text = stringResource(id = R.string.register),
                navigateBack
            ) //ovo copy za register
            Spacer(modifier = Modifier.height(30.dp))

            // inputs
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, fill = false)
            ) {
                BasicInputComponent(
                    text = viewModel.uiState.fullname,
                    onTextChanged = {
                        viewModel.changeFullname(it)
                    },
                    label = stringResource(id = R.string.fullname) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = InputTypes.Text,
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicInputComponent(
                    text = viewModel.uiState.username,
                    onTextChanged = {
                        viewModel.changeUsername(it)
                    },
                    label = stringResource(id = R.string.username) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = InputTypes.Text,
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicInputComponent(
                    text = viewModel.uiState.email,
                    onTextChanged = {
                        viewModel.changeEmail(it)
                    },
                    label = stringResource(id = R.string.email) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    inputType = InputTypes.Text,
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicInputComponent(
                    text = viewModel.uiState.password,
                    onTextChanged = {
                        viewModel.changePassword(it)
                    },
                    label = stringResource(id = R.string.password) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    inputType = InputTypes.Password
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicInputComponent(
                    text = viewModel.uiState.confirm_password,
                    onTextChanged = {
                        viewModel.changeConfirmPassword(it)
                    },
                    label = stringResource(id = R.string.confirm_password) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    inputType = InputTypes.Password
                )


                Spacer(modifier = Modifier.heightIn(30.dp))

                // buttons
                ButtonComponent( //pravljeno
                    text = stringResource(id = R.string.register),
                    width = 230.dp,
                    onClick = {
                        viewModel.register(navigateToHome)
                    }
                )
//                TextButton( //postoji
//                    modifier = Modifier.padding(all = 10.dp),
//                    onClick = { /* TODO - navigate to forgot password screen */ }) {
//                    //telo textbutton-a
//                    Text(
//                        textDecoration = TextDecoration.Underline,
//                        text = stringResource(id = R.string.forgot_password),
//                        modifier = Modifier.background(color = MaterialTheme.colors.background)
//
//                    )
//                }
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewRegister(){
//    Text("Register page")
//}
