package elfak.mosis.tourguide.ui.screens.registerScreen

import android.widget.Toast
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.ButtonComponent
import elfak.mosis.tourguide.ui.components.LogoWithTextComponent
import es.dmoral.toasty.Toasty

@Composable
fun RegisterScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: RegisterViewModel
) {
    val focusManager = LocalFocusManager.current

    if (viewModel.uiState.hasErrors) {
        Toasty.error(LocalContext.current, viewModel.uiState.errorMessage, Toast.LENGTH_LONG, true).show()
        viewModel.clearErrorMessage()
    }
//    LazyColumn {
        Box(modifier = Modifier.fillMaxSize()) {
//        items(1, null) {
            Column(modifier = Modifier
                .fillMaxWidth()
            ) {
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
                        .verticalScroll(rememberScrollState(),true, null,true)
                        .fillMaxWidth()
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
                            viewModel.changeUsername(it.trim())
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
                            viewModel.changeEmail(it.trim())
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
                            viewModel.changePassword(it.trim())
                        },
                        label = stringResource(id = R.string.password) + ":",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = false,
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                        ),
                        inputType = InputTypes.Password
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BasicInputComponent(
                        text = viewModel.uiState.confirm_password,
                        onTextChanged = {
                            viewModel.changeConfirmPassword(it.trim())
                        },
                        label = stringResource(id = R.string.confirm_password) + ":",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = false,
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions = KeyboardActions(
                        onDone = { register(viewModel, focusManager,navigateToHome) }
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
//        }
//    }
}

@Composable
fun ScrollableColumn(
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
//    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
//    horizontalGravity: Alignment.Horizontal = Alignment.Start,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,

//    contentPadding: ,
    children: @Composable ColumnScope.() -> Unit
) {
}


private fun register(viewModel: RegisterViewModel, focusManager: FocusManager, navigateToHome: () -> Unit){
    viewModel.register{
        focusManager.clearFocus()
        navigateToHome()
    }
}

