@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.profileScreen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.ModeEditOutline
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.helper.CameraFileProvider
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import elfak.mosis.tourguide.ui.components.dialogs.ChangePasswordDialog
import elfak.mosis.tourguide.ui.components.images.ProfileImage
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import es.dmoral.toasty.Toasty

@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navController: NavController,
){
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = viewModel::handleCameraLauncherResult
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

    ToastHandler(
        toastData = viewModel.uiState.toastData,
        clearErrorMessage = viewModel::clearErrorMessage,
        clearSuccessMessage = viewModel::clearSuccessMessage
    )

    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.profile),
                scaffoldState = scaffoldState,
                coroutineScope = coroutineScope
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel
            )

        }
    ) {
        /** MAIN CONTENT */

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(rememberScrollState(1000), true, null, true)
        ) {
            val icon = if (!viewModel.isEditMode) Icons.Rounded.ModeEditOutline else Icons.Rounded.Cancel
            val tint = if (!viewModel.isEditMode) MaterialTheme.colors.primary else MaterialTheme.colors.error
            if (viewModel.isEditEnabled) {
                var showResetPasswordDialog by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    if (showResetPasswordDialog) {
                        ChangePasswordDialog(
                            onAcceptClick = { password, confirmPassword ->
                                viewModel.changePassword(password, confirmPassword, onSuccess = { showResetPasswordDialog = false })
                            },
                            onDismiss = { showResetPasswordDialog = false }
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(10.dp)
                            .size(32.dp)
                            .clickable {
                                viewModel.isEditMode = !viewModel.isEditMode
                            },
                        imageVector = icon,
                        contentDescription = stringResource(id = R.string.edit_profile),
                        tint = tint
                    )
                    AnimatedVisibility(viewModel.isEditMode) {
                        Icon(modifier = Modifier
                            .clip(CircleShape)
                            .padding(10.dp)
                            .size(32.dp)
                            .clickable {
                                showResetPasswordDialog = true
                            },
                            imageVector = Icons.Filled.Lock,
                            contentDescription = stringResource(id = R.string.change_password),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
            Column {
                Column(
                    modifier = Modifier

                        .fillMaxWidth()
                        //                    .weight(1f, fill = false)
                        .padding(top = 10.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    // Profile image
                    ProfileImage(
                       viewModel.uiState.photo,
                       onClick = {
                           if (!viewModel.isEditMode) return@ProfileImage
                           openCamera(
                               viewModel,
                               permissionAlreadyRequested,
                               permissionState,
                               cameraLauncher,
                               context
                           )
                       },
                       onLongClick = {
                           if (!viewModel.isEditMode) return@ProfileImage
                           if (viewModel.uiState.photo.hasPhoto) {
                               // remove current photo
                               viewModel.removeCurrentPhoto()
                           }
                       }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // fullname
                    BasicInputComponent(
                        enabled = viewModel.isEditMode,
                        text = viewModel.uiState.fullname,
                        onTextChanged = { fullname ->
                            viewModel.setDirty(viewModel.uiState.fullname != fullname)
                            viewModel.setFullname(fullname)
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

                    // phone number
                    BasicInputComponent(
                        enabled = viewModel.isEditMode,
                        text = viewModel.uiState.phoneNumber,
                        onTextChanged = { phoneNumber ->
                            viewModel.setDirty(viewModel.uiState.phoneNumber != phoneNumber)
                            viewModel.setPhoneNumber(phoneNumber.trim())
                        },
                        label = stringResource(id = R.string.phone_number) + ":",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = false,
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        inputType = InputTypes.Text,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // username
                    BasicInputComponent(
                        enabled = false,
                        text = viewModel.uiState.username,
                        onTextChanged = { username ->
//                            viewModel.setDirty(viewModel.uiState.username != username)
//                            viewModel.setUsername(username.trim())
                        },
                        label = stringResource(id = R.string.username) + ":",
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            autoCorrect = false,
//                            capitalization = KeyboardCapitalization.None,
//                            keyboardType = KeyboardType.Text,
//                            imeAction = ImeAction.Next
//                        ),
                        inputType = InputTypes.Text,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // email
                    BasicInputComponent(
                        enabled = false,
                        text = viewModel.uiState.email,
                        onTextChanged = { email ->
//                            viewModel.setDirty(viewModel.uiState.email != email)
//                            viewModel.setEmail(email.trim())
                        },
                        label = stringResource(id = R.string.email) + ":",
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            autoCorrect = false,
//                            capitalization = KeyboardCapitalization.None,
//                            keyboardType = KeyboardType.Email,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = {
//                                focusManager.clearFocus()
//                            }
//                        ),
                        inputType = InputTypes.Text,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                AnimatedVisibility(viewModel.isEditMode) {
                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp, bottom = 15.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        if (viewModel.uiState.inProgress) {
                            CircularProgressIndicator()
                        } else {
                            SaveButton {
                                viewModel.setInProgress(true)
                                viewModel.saveData()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun openCamera(
    viewModel: ProfileScreenViewModel,
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