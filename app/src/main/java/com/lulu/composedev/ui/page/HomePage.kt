package com.lulu.composedev.ui.page

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import coil3.request.ImageRequest
import com.bit.atbusiness.navigation.Route
import github.leavesczy.matisse.CoilImageEngine
import github.leavesczy.matisse.DefaultMediaFilter
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseCaptureContract
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaType


/**
 * 作者: shilu
 * 时间: 2025/6/19
 * 描述:
 */
@Composable
fun HomePage(navController: NavHostController,onNavigateToLandscape: () -> Unit) {
    Scaffold() { paddingValues: PaddingValues ->
        val context = LocalContext.current
        val packageName = context.packageName

        Column(Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            Text("竖屏页面")

            Button(onClick = {
                onNavigateToLandscape()
            }) {
                Text("打开横屏页面")
            }

            val mediaPickerLauncher =
                rememberLauncherForActivityResult(contract = MatisseContract()) {

                }

            val matisse =  Matisse(
                gridColumns = 4,
                maxSelectable = 5,
                fastSelect = false,
                mediaType = MediaType.ImageAndVideo,
                mediaFilter = DefaultMediaFilter(
                    ignoredMimeType = emptySet(),
                    ignoredResourceUri = emptySet(),
                    selectedResourceUri = emptySet()
                ),
                imageEngine =  CoilImageEngine(),
                singleMediaType = false,
//                captureStrategy = FileProviderCaptureStrategy(authority = "${packageName}.FileProvider")
            )


            val takePictureLauncher =
                rememberLauncherForActivityResult(contract = MatisseCaptureContract()) { result ->
                    if (result != null) {
                        val uri = result.uri
                        val path = result.path
                        val name = result.name
                        val mimeType = result.mimeType
                    }
                }

//            takePictureLauncher.launch(MatisseCapture(captureStrategy = MediaStoreCaptureStrategy()))



            Button(onClick = {
                mediaPickerLauncher.launch(matisse)
            }) {
                Text("打开图片选择器")
            }

            Button(onClick = {
                navController.navigate(Route.IMGPreview)
            }) {
                Text("图片缩放查看+多图滑动组件")
            }

            Button(onClick = {
                navController.navigate(Route.IMGPreview)
            }) {
                Text("列表+分页")
            }

        }
    }
}