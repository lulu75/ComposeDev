package com.lulu.composedev.ui.page.base

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.lulu.composedev.R
import com.lulu.composedev.ui.bean.IMAGE_PREVIEW_LOCAL
import com.lulu.composedev.ui.bean.IMAGE_PREVIEW_NETWORK
import com.lulu.composedev.ui.bean.PreviewItem
import github.leavesczy.matisse.CoilImageEngine
import github.leavesczy.matisse.DefaultMediaFilter
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaType

/**
 * 作者: shilu
 * 时间: 2025/7/1
 * 描述: 图片缩放预览+翻页 组件
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableImagePager(
    items: List<PreviewItem>, startIndex: Int = 0
) {
    val pagerState = rememberPagerState(initialPage = startIndex) { items.size }
    HorizontalPager(
        state = pagerState, modifier = Modifier.fillMaxSize()
    ) { page ->
        ZoomableImage(items[page])
    }
}

@Composable
fun ZoomableImage(data: PreviewItem) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val minScale = 1f
    val maxScale = 5f

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                Log.e("手势", "onPreScroll")
                return if (scale > 1f) Offset(available.x, 0f) else Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset, available: Offset, source: NestedScrollSource
            ): Offset {
                Log.e("手势", "onPostScroll")

                if (scale > 1f) {
                    val scaledWidth = imageSize.width * scale
                    val scaledHeight = imageSize.height * scale

                    val maxX = ((scaledWidth - screenWidthPx) / 2f).coerceAtLeast(0f)
                    val maxY = ((scaledHeight - screenHeightPx) / 2f).coerceAtLeast(0f)

                    val newOffset = offset + available
                    offset = Offset(
                        x = newOffset.x.coerceIn(-maxX, maxX), y = newOffset.y.coerceIn(-maxY, maxY)
                    )
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (scale > 1f) available else Velocity.Zero
            }
        }
    }
    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .nestedScroll(nestedScrollConnection)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        var zoom = 1f
                        var pan = Offset.Zero

                        // 先等所有手指按下
                        do {
                            val event = awaitPointerEvent()
                            Log.e("手势", "awaitPointerEvent ${event.changes.size}")
                            // 需要两根以上手指才计算缩放
                            if (event.changes.size >= 2) {
                                zoom = event.calculateZoom()
                                pan = event.calculatePan()
                                val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                                val scaledWidth = imageSize.width * newScale
                                val scaledHeight = imageSize.height * newScale

                                val maxX = ((scaledWidth - screenWidthPx) / 2f).coerceAtLeast(0f)
                                val maxY = ((scaledHeight - screenHeightPx) / 2f).coerceAtLeast(0f)

                                val newOffset = offset + pan

                                scale = newScale
                                offset = if (newScale > 1f) {
                                    Offset(
                                        x = newOffset.x.coerceIn(-maxX, maxX),
                                        y = newOffset.y.coerceIn(-maxY, maxY)
                                    )
                                } else Offset.Zero
                            }

                            // 缩放状态下，单指拖动计算滑动
                            if (scale > 1f) {
                                val drag = event.changes.first()
                                val delta = drag.positionChange()
                                if (drag.pressed) {
                                    // 计算缩放后的图片宽高（实际布局尺寸 × 缩放因子）
                                    val scaledWidth = imageSize.width * scale
                                    val scaledHeight = imageSize.height * scale

                                    // 计算水平和垂直方向最大可拖动距离（图片比屏幕大多少的一半）
                                    val maxX =
                                        ((scaledWidth - screenWidthPx) / 2f).coerceAtLeast(0f)
                                    val maxY =
                                        ((scaledHeight - screenHeightPx) / 2f).coerceAtLeast(0f)

                                    // 计算新的偏移值（拖动后的位置）
                                    val newOffset = offset + delta

                                    // 将偏移值限制在 [-maxX, maxX] 和 [-maxY, maxY] 范围内，防止图片被拖出边界
                                    val clampedOffset = Offset(
                                        x = newOffset.x.coerceIn(-maxX, maxX),
                                        y = newOffset.y.coerceIn(-maxY, maxY)
                                    )
                                    offset = clampedOffset

                                    // ✅ 消费掉这个事件，防止它继续传给 NestedScroll、Pager、其他 PointerInput
                                    if (drag.positionChange() != Offset.Zero) drag.consume()
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2f
                            }
                        })
                }, contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model =  when (data.type) {
                    IMAGE_PREVIEW_LOCAL -> data.uri
                    IMAGE_PREVIEW_NETWORK -> data.imageUrl
                    else -> {}
                },
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .onGloballyPositioned {
                        imageSize = it.size
                    },
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_loading), // 替换为你的 mipmap 图片名
                            contentDescription = "Mipmap Image",
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                error = {

                })
        }
        if (scale > 1f)
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        scale = 1f
                        offset = Offset.Zero
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "还 原",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(Color(0xFF666666).copy(alpha = 0.5f))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

    }
}

@Preview(
    name = "ZoomableImagePreview",
    showBackground = true,
    backgroundColor = 0xFFEFEFEF,
//    widthDp = 200,
//    heightDp = 100
)
@Composable
fun ZoomableImagePreview() {
    val mockData = remember {
        mutableStateListOf(
            PreviewItem(
                IMAGE_PREVIEW_NETWORK,
                Uri.EMPTY,
                "https://images.pexels.com/photos/13743196/pexels-photo-13743196.jpeg"
            ),
            PreviewItem(
                IMAGE_PREVIEW_NETWORK,
                Uri.EMPTY,
                "https://images.pexels.com/photos/32435046/pexels-photo-32435046.jpeg"
            ),
            PreviewItem(
                IMAGE_PREVIEW_NETWORK,
                Uri.EMPTY,
                "https://images.pexels.com/photos/27968071/pexels-photo-27968071.jpeg"
            ),
            PreviewItem(
                IMAGE_PREVIEW_NETWORK,
                Uri.EMPTY,
                "https://images.pexels.com/photos/7007275/pexels-photo-7007275.jpeg"
            )
        )
    }
    val mediaPickerLauncher =
        rememberLauncherForActivityResult(contract = MatisseContract()) { selectUriList ->
            selectUriList?.forEach { item ->
                mockData.add( PreviewItem(
                    IMAGE_PREVIEW_LOCAL,
                    item.uri,
                    ""
                ))
            }
        }
    val matisse = Matisse(
        gridColumns = 4,
        maxSelectable = 5,
        fastSelect = false,
        mediaType = MediaType.ImageOnly,
        mediaFilter = DefaultMediaFilter(
            ignoredMimeType = emptySet(),
            ignoredResourceUri = emptySet(),
            selectedResourceUri = emptySet()
        ),
        imageEngine = CoilImageEngine(),
        singleMediaType = false,
    )

    Column {
        Button(onClick = {
            mediaPickerLauncher.launch(matisse)
        }) {
            Text("选择图片")
        }
            ZoomableImagePager(mockData, 2)
    }
}

