package com.lulu.composedev.ui.page

import android.R
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems

/**
 * 作者: shilu
 * 时间: 2025/7/4
 * 描述: 列表+分页
 */
@Composable
fun ItemListPage(viewModel: ItemViewModel = viewModel()) {
    val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            item?.let {
                Text(
                    text = it.name, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        // 加载更多状态展示
        lazyPagingItems.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
//                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item {
                        Text(
                            text = "加载失败: ${e.error.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

data class Item(val id: Int, val name: String)

class ItemPagingSource : PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {

        val page = params.key ?: 1
        val pageSize = params.loadSize
        Log.e("ItemPagingSource", "触发加载， 加载第 $page 页")
        return try {
            // 模拟数据
            val data = (1..pageSize).map {
                val id = (page - 1) * pageSize + it
                Item(id, "Item $id")
            }
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}

class ItemViewModel : ViewModel() {
    val pager = Pager(PagingConfig(pageSize = 20)) {
        ItemPagingSource()
    }.flow.cachedIn(viewModelScope)
}


@Composable
fun Material3BottomDialogSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    containerColor: Color = Color.White,
    scrimColor: Color = Color.Black.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    if (show)
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false // ✅ 关键：让键盘不要推动 Dialog
            ), onDismissRequest = onDismissRequest
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onDismissRequest()
                    }) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(WindowInsets.navigationBars.asPaddingValues()) // ✅ 保留 navigationBar
                        .then(modifier),
                    shape = shape,
                    color = containerColor,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(), content = content
                    )
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BGTest() {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var bottomShow by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // 背景用于测试
        contentAlignment = Alignment.BottomCenter
    ) {

        Button(onClick = {
            bottomShow = true
        }) { Text("打开评论") }

//        if (bottomShow)
//            ModalBottomSheet(
//                modifier = Modifier.wrapContentHeight(),
//                containerColor = Color.White,
//                sheetState = sheetState,
//                onDismissRequest = {
////                    showBottomSheet.value = false
////                commentViewModel.resetState()
////                onClose()
//                    bottomShow = false
//                }) {
//                // 灰色背景
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
//                        .background(Color.DarkGray)
//                ) {
//                    // 内部白色内容，使用 padding 向下留空露出灰色背景
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 1.dp) // 这块是让灰色背景露出的关键！
//                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
//                            .background(Color.White)
//                            .padding(10.dp)
//                    ) {
//                        Text("内容1")
//                        Text("内容2")
//                        Spacer(modifier = Modifier.height(50.dp))
//                        TextField(
//                            value = "",
//                            onValueChange = {
////                                text = it
//                            },
//                            label = { Text("请输入内容") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }

        if (bottomShow)
            Material3BottomDialogSheet(bottomShow, onDismissRequest = {
                bottomShow = false
            }) {
                // 灰色背景
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color.DarkGray)
                ) {
                    // 内部白色内容，使用 padding 向下留空露出灰色背景
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.dp) // 这块是让灰色背景露出的关键！
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(Color.White)
                            .padding(10.dp)
                    ) {
                        Text("内容1")
                        Text("内容2")
                        Spacer(modifier = Modifier.height(50.dp))
                        TextField(
                            value = "",
                            onValueChange = {
//                                text = it
                            },
                            label = { Text("请输入内容") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

    }

}

@Composable
fun LazyListPage() {

    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        item { Text(text = "我是顶部视图", modifier = Modifier.height(150.dp)) }
        LazyListContent()
    }
}


fun LazyListScope.LazyListContent() {
    val data =
        listOf<String>("我是内容1", "我是内容2", "我是内容3", "我是内容1", "我是内容2", "我是内容3")
    itemsIndexed(data) { index, content ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), text = content
        )
    }
}

@Composable
fun RowTest() {
    Row {
        Column(
            modifier = Modifier

                .weight(1f)
                .background(Color.Cyan)
        ) {
            Column {
                Button(onClick = {}) {
                    Text("1111")
                }
            }

        }
        Column(modifier = Modifier
            .background(Color.DarkGray)) {
            Text("发送")
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
fun ListPreview() {
    ItemListPage()
}

@Preview(
    name = "TestPreview",
    showBackground = true,
//    widthDp = 200,
//    heightDp = 100
)
@Composable
fun TestPreview() {
//    BGTest()
//    LazyListPage()
    RowTest()
}




