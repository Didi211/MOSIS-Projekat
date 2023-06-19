@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class
)

package elfak.mosis.tourguide.ui.screens.registerScreen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.helper.CameraFileProvider
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.buttons.ButtonComponent
import elfak.mosis.tourguide.ui.components.images.LogoWithTextComponent
import elfak.mosis.tourguide.ui.components.images.ProfileImage
import es.dmoral.toasty.Toasty

@Composable
fun RegisterScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: RegisterViewModel
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (!success) {
                viewModel.setPhotoUri(viewModel.uiState.previousPhoto.uri)
            }
            viewModel.setHasPhoto(true)
        }
    )
    var permissionAlreadyRequested by rememberSaveable {
        mutableStateOf(false)
    }
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = {
            val allowed = viewModel.checkPermissions(context)
            if (allowed) {
                val uri = CameraFileProvider.getImageUri(context)
                viewModel.setPhotoUri(uri)
                cameraLauncher.launch(uri)
            }
            permissionAlreadyRequested = true
        }
    )
    var inProgress by remember { mutableStateOf(false) }

    if (viewModel.uiState.hasErrors) {
        Toasty.error(LocalContext.current, viewModel.uiState.errorMessage, Toast.LENGTH_LONG, true).show()
        inProgress = false
        viewModel.clearErrorMessage()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                ProfileImage(
                    photo = viewModel.uiState.photo,
                    onClick = {
                        openCamera(
                            viewModel,
                            permissionAlreadyRequested,
                            permissionState,
                            cameraLauncher,
                            context
                        )
                    },
                    onLongClick = {
                        if (viewModel.uiState.photo.hasPhoto) {
                            // remove current photo
                            viewModel.setHasPhoto(false)
                            viewModel.setPhotoUri(null)
                            Toasty.info(context, context.getString(R.string.image_removed)).show()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                // fullname
                BasicInputComponent(
                    text = viewModel.uiState.fullname,
                    onTextChanged = {
                        viewModel.setFullname(it)
                    },
                    label = stringResource(id = R.string.fullname) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.Words,
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
                        viewModel.setUsername(it.trim())
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
                        viewModel.setPhoneNumber(it.trim())
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
                        viewModel.setEmail(it.trim())
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
                        viewModel.setPassword(it.trim())
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
                    text = viewModel.uiState.confirmPassword,
                    onTextChanged = {
                        viewModel.setConfirmPassword(it.trim())
                    },
                    label = stringResource(id = R.string.confirm_password) + ":",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            inProgress = true
                            register(viewModel, focusManager) {
                                inProgress = false
                                navigateToHome()
                            }
                        }
                    ),
                    inputType = InputTypes.Password
                )
            }

            // button
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (inProgress) {
                    CircularProgressIndicator()
                }
                else {
                    ButtonComponent(
                        text = stringResource(id = R.string.register),
                        width = 230.dp,
                        onClick = {
                            inProgress = true
                            register(viewModel, focusManager) {
                                inProgress = false
                                navigateToHome()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.heightIn(15.dp))
            }
        }
    }
}


private fun register(viewModel: RegisterViewModel, focusManager: FocusManager, onSuccess: () -> Unit) {
    focusManager.clearFocus()
    viewModel.register {
        onSuccess()
    }
}

private fun openCamera(
    viewModel: RegisterViewModel,
    permissionAlreadyRequested: Boolean,
    permissionState: PermissionState,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    context: Context
) {
    if (!viewModel.checkPermissions(context)) {
        if (!permissionAlreadyRequested || permissionState.status.shouldShowRationale) {
            permissionState.launchPermissionRequest()
            return
        }
        Toasty.error(context, context.getString(R.string.permission_denied_twice), Toast.LENGTH_LONG).show()
        return
    }

    // open camera
    try {
        viewModel.setPreviousPhoto()
        viewModel.setHasPhoto(false)
        val uri = CameraFileProvider.getImageUri(context)
        viewModel.setPhotoUri(uri) // changing file in fs in which will camera write and compose read
        cameraLauncher.launch(uri)
    }
    catch (ex: Exception) {
        ex.message?.let { Toasty.error(context, it).show() }
    }
}

