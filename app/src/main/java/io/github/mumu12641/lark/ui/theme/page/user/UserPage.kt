package io.github.mumu12641.lark.ui.theme.page.user

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel.Companion.INIT_USER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(
    navController: NavController,
    viewModel: UserViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.user_message_text),
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = { paddingValues ->
                UserContent(modifier = Modifier.padding(paddingValues), viewModel)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    viewModel.saveInformation()
                    Toast.makeText(
                        context,
                        context.getString(R.string.save_success_test),
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun UserContent(
    modifier: Modifier,
    viewModel: UserViewModel
) {
    val user by viewModel.userState.collectAsState(initial = INIT_USER)
    val launcherBackground = rememberLauncherForActivityResult(
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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            GlideImage(
                imageModel = user.backgroundImageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        launcherBackground.launch("image/*")
                    },
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                failure = {
                    GlideImage(imageModel = R.drawable.userbackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .clickable {
                                launcherBackground.launch("image/*")
                            })
                }

            )

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
