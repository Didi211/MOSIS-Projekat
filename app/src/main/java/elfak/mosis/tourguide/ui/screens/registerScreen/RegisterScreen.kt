package elfak.mosis.tourguide.ui.screens.registerScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.buttons.ButtonComponent
import elfak.mosis.tourguide.ui.components.images.LogoWithTextComponent
import es.dmoral.toasty.Toasty
import org.intellij.lang.annotations.JdkConstants.VerticalScrollBarPolicy

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
                    titleSize = 15.sp,
                    logoSize = 60.dp,
                    customTextSize = 25.sp,
                    navigateBack = navigateBack
                ) //ovo copy za register
                Spacer(modifier = Modifier.height(10.dp))



                // inputs
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState(1000), true, null, true)
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(bottom = 10.dp)
                ) {
                    // profile image
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(130.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .border(5.dp, MaterialTheme.colors.primary, CircleShape)
                            .clickable {
                                // open camera
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        if (!viewModel.uiState.hasPhoto) {
                            Icon(
                                imageVector = Icons.Outlined.AddAPhoto,
                                contentDescription = stringResource(id = R.string.add_profile_photo),
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(scaleX = -1f, scaleY = 1f)

                            )
                        }
                        //                    else {
                        //                        Image(
                        //
                        //                        )
                        //                    }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // fullname
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

                    // username
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

                    // phone number
                    BasicInputComponent(
                        text = viewModel.uiState.phoneNumber,
                        onTextChanged = {
                            viewModel.changePhoneNumber(it.trim())
                        },
                        label = stringResource(id = R.string.phone_number) + ":",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = false,
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        inputType = InputTypes.Text,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // email
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

                    // password
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

                    // confirm password
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
                            onDone = { register(viewModel, focusManager, navigateToHome) }
                        ),
                        inputType = InputTypes.Password
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

                // button
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ButtonComponent(
                        text = stringResource(id = R.string.register),
                        width = 230.dp,
                        onClick = {
                            viewModel.register(navigateToHome)
                        }
                    )
                    Spacer(modifier = Modifier.heightIn(15.dp))
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

