package elfak.mosis.tourguide.ui.screens.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.buttons.ButtonComponent
import elfak.mosis.tourguide.ui.components.images.LogoWithTextComponent
import es.dmoral.toasty.Toasty


@Composable
fun LoginScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToResetPassword: () -> Unit,
    viewModel: LoginViewModel
) {
    val focusManager = LocalFocusManager.current
    var inProgress by remember { mutableStateOf(false) }

    if (viewModel.uiState.hasErrors) {
        Toasty.error(LocalContext.current, viewModel.uiState.errorMessage, Toast.LENGTH_LONG, true).show()
        inProgress = false
        viewModel.clearErrorMessage()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // logo
            LogoWithTextComponent(text = stringResource(id = R.string.login), navigateBack = navigateBack)
            Spacer(modifier = Modifier.height(30.dp))

            // inputs
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, fill = false)
            ) {
                // Email
                BasicInputComponent(
                    text = viewModel.uiState.email,
                    onTextChanged = {
                        viewModel.changeEmail(it.trim())
                    },
                    label = stringResource(id = R.string.email) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = InputTypes.Text,
                )
                Spacer(modifier = Modifier.height(15.dp))

                // Password
                BasicInputComponent(
                    text = viewModel.uiState.password,
                    onTextChanged = {
                        viewModel.changePassword(it.trim())
                    },
                    label = stringResource(id = R.string.password) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            inProgress = true
                            login(viewModel, focusManager) {
                                inProgress = false
                                navigateToHome()
                            }
                        }
                    ),
                    inputType = InputTypes.Password
                )
                Spacer(modifier = Modifier.heightIn(30.dp))

                // buttons
                if (inProgress) {
                    CircularProgressIndicator()
                }
                else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Login
                        ButtonComponent( //pravljeno
                            text = stringResource(id = R.string.login),
                            width = 230.dp,
                            onClick =  {
                                inProgress = true
                                login(viewModel, focusManager) {
                                    inProgress = false
                                    navigateToHome()
                                }
                            }

                        )
                        // Forgot password
                        TextButton( //postoji
                            modifier = Modifier.padding(all = 10.dp),
                            //telo textbutton-a
                            onClick = navigateToResetPassword
                        ) {
                            Text(
                                textDecoration = TextDecoration.Underline,
                                text = stringResource(id = R.string.forgot_password),
                                modifier = Modifier.background(color = MaterialTheme.colors.background)
                            )
                        }

                    }
                }
            }
        }
    }
}

private fun login(viewModel: LoginViewModel, focusManager: FocusManager, onSuccess: () -> Unit) {
    focusManager.clearFocus()
    viewModel.login {
        onSuccess()
    }
}
