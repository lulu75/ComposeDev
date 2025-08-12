package com.lulu.composedev.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lulu.composedev.R

@Composable
fun TextBaseLineTextPage() {
    val commonTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = FontFamily.SansSerif, // 系统默认无外部字体
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp, // 固定行高，保证对齐
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        localeList = LocaleList("zh-CN"), // 所有字符用中文字体度量
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center, // 在行高内垂直居中
            trim = LineHeightStyle.Trim.None // 不裁切顶部/底部
        )
    )

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_loading),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "123",
                    style = commonTextStyle
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_loading),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "中文内容",
                    style = commonTextStyle
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_loading),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "😂ABC123中文",
                    style = commonTextStyle
                )
            }
        }
    }


}

@Preview(
    name = "BaseLineTest",
    showBackground = true,
//    widthDp = 200,
//    heightDp = 100
)
@Composable
fun BaseLineTest() {
//    BGTest()
//    LazyListPage()
    TextBaseLineTextPage()
}