package io.github.mumu12641.lark.ui.theme.page.user

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.compose.rememberNavController
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(
    navController: NavController,
    viewModel: UserViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.user_message_text),
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = {
                paddingValues ->
                    UserContent(modifier = Modifier.padding(paddingValues),viewModel)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    viewModel.saveInformation()
                }) {
                    Icon(painterResource(id = R.drawable.file_icon), contentDescription = "Save")
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
//    var name by remember {
//        mutableStateOf(MMKV.defaultMMKV().decodeString("UserName")!!)
//    }
    val name by viewModel.userNameState.collectAsState(
        initial = MMKV.defaultMMKV().decodeString("UserName")!!
    )
    Box(
        modifier = modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.userbackground),
                contentDescription = "User Background",
                modifier = Modifier.clip(RoundedCornerShape(50.dp))
            )
            Row (
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
               Icon(
                   Icons.Filled.Face, contentDescription = "User Icon",
                   modifier = Modifier.size(50.dp)
               )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f),
                    value = name,
                    onValueChange = { viewModel.changeNameValue(it) },
                    label = { Text(stringResource(id = R.string.my_name_text)) }
                )
            }
//            OutlinedTextField(
//                modifier = Modifier
//                    .padding(top = 20.dp)
//                    .fillMaxWidth()
//                    .height(200.dp),
//                value = description,
//                onValueChange = {description = it},
//                label = { Text(stringResource(id = R.string.description_text)) }
//            )
        }

    }
}