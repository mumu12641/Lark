package io.github.mumu12641.lark.ui.theme.page.user

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.GlideAsyncImage
import io.github.mumu12641.lark.ui.theme.component.LarkAlertDialog
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel.Companion.INIT_USER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(
    navController: NavController,
    viewModel: UserViewModel
) {
    var actionMenu by remember { mutableStateOf(false) }

    var showLoginDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.user_message_text),
                    navIcon = Icons.Filled.ArrowBack,
                    actions = {
                        IconButton(onClick = { actionMenu = !actionMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = actionMenu,
                            onDismissRequest = { actionMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.refresh_user_text)) },
                                onClick = {
                                    viewModel.getNeteaseUserDetail()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.logout_text)) },
                                onClick = {
                                    viewModel.logout()
                                }
                            )
                        }
                    }
                ) {
                    navController.popBackStack()
                }
            },
            content = { paddingValues ->
                UserContent(modifier = Modifier.padding(paddingValues), viewModel)
            },
            floatingActionButton = {
                Column {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(60.dp),
                        onClick = {
                            if (kv.decodeLong("neteaseId") != 0L) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.already_login_text),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                showLoginDialog = true
                            }
                        }) {
                        Icon(
                            painterResource(id = R.drawable.ic_netease),
                            contentDescription = "Netease",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(60.dp),
                        onClick = {
                            viewModel.saveInformation()
                            Toast.makeText(
                                context,
                                context.getString(R.string.save_success_test),
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

            }
        )
    }
    if (showLoginDialog) {
        LoginDialog(showDialogFunc = { showLoginDialog = it }) { phone, password ->
            viewModel.loginUser(phone, password)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginDialog(
    showDialogFunc: (Boolean) -> Unit,
    login: (String, String) -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    LarkAlertDialog(
        onDismissRequest = { showDialogFunc(false) },
        title = stringResource(id = R.string.login_text),
        text = {
            Column {
                TextField(
                    modifier = Modifier.background(Color.Transparent),
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = {
                        Text(text = stringResource(id = R.string.enter_phone_text))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
                TextField(
                    modifier = Modifier.background(Color.Transparent),
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(text = stringResource(id = R.string.enter_password_text))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
            }

        },
        confirmOnClick = {
            if (phone != "" && password != "") {
                login(phone, password)
            }
            showDialogFunc(false)
        },
        confirmText = stringResource(id = R.string.confirm_text),
        dismissButton = {
            TextButton(
                onClick = {
                    showDialogFunc(false)
                }
            ) {
                Text(stringResource(id = R.string.cancel_text))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun UserContent(
    modifier: Modifier,
    viewModel: UserViewModel
) {
    val user by viewModel.userState.collectAsState(initial = INIT_USER)
    val loadState by viewModel.loadState.collectAsState(initial = LoadState.None())

    val launcherBackground =
        rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                viewModel.changeBackgroundValue(uri.toString())
            } ?: INIT_USER.backgroundImageUri?.let {
                viewModel.changeBackgroundValue(it)
            }
        }

    val launcherIcon = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.changeIconValue(uri.toString())
        } ?: INIT_USER.iconImageUri?.let {
            viewModel.changeIconValue(it)
        }
    }
    val imageModifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(50.dp))
        .clickable {
            launcherBackground.launch("image/*")
        }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            user.backgroundImageUri?.let {
                GlideAsyncImage(
                    modifier = imageModifier,
                    imageModel = user.backgroundImageUri,
                    failure = R.drawable.user_background
                )
            } ?: run {
                AsyncImage(
                    modifier = imageModifier,
                    imageModel = R.drawable.user_background,
                    failure = R.drawable.user_background
                )
            }

            Row(
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    imageModel = user.iconImageUri,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            launcherIcon.launch("image/*")
                        },
                    failure = {
                        Icon(
                            Icons.Filled.Face,
                            contentDescription = "User Icon",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    launcherIcon.launch("image/*")
                                }
                        )
                    }
                )
                AnimatedVisibility(visible = loadState is LoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(25.dp)
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f),
                    value = user.name,
                    onValueChange = { viewModel.changeNameValue(it) },
                    label = { Text(stringResource(id = R.string.my_name_text)) }
                )
            }
        }

    }
}


