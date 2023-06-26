package elfak.mosis.tourguide.ui.screens.resetPasswordScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.buttons.ButtonComponent
import elfak.mosis.tourguide.ui.components.images.LogoWithTextComponent
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginViewModel
import es.dmoral.toasty.Toasty

@Composable
fun ResetPasswordScreen(
    navigateBack: () -> Unit,
    viewModel: ResetPasswordViewModel
)  {
    val focusManager = LocalFocusManager.current
    var inProgress by remember { mutableStateOf(false) }


    if (viewModel.uiState.hasErrors) {
        Toasty.error(LocalContext.current, viewModel.uiState.errorMessage, Toast.LENGTH_LONG, true).show()
        inProgress = false
        viewModel.clearErrorMessage()
    }

    ToastHandler(
        toastData = viewModel.uiState.toastData,
        clearErrorMessage = viewModel::clearErrorMessage,
        clearSuccessMessage = viewModel::clearSuccessMessage
    )

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxWidth()) {
            // logo
            LogoWithTextComponent(
                text = stringResource(id = R.string.reset_password),
                navigateBack = navigateBack
            )
            Spacer(modifier = Modifier.height(30.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                Spacer(modifier = Modifier.heightIn(25.dp))
                Text(
                     text = "We will send a reset link to your email",
                     style = MaterialTheme.typography.body1,
                     color = MaterialTheme.colors.primary

                )
                Spacer(modifier = Modifier.heightIn(30.dp))

                ButtonComponent(
                    text = stringResource(id = R.string.send_code),
                    width = 230.dp,
                    onClick =  {
                        viewModel.sendResetEmail()
                    }

                )
            }
        }
    }
//    Text(text = "Reset Password Screen")

}