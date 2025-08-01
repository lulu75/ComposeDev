package com.lulu.composedev.ui.page

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

/**
 * 作者: shilu
 * 时间: 2025/8/1
 * 描述: 统计图页面
 */
@Composable
fun ChartsPage() {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                ColumnChart(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    data = remember {
                        listOf(
                            Bars(
                                label = "周一",
                                values = listOf(
                                    Bars.Data(value = 3425.4, color = SolidColor(Color(0xff4DFDEB)))
                                ),
                            ),
                            Bars(
                                label = "周二",
                                values = listOf(

                                    Bars.Data(value = 2011.4, color = SolidColor(Color(0xff4DFDEB)))
                                ),
                            ),

                            Bars(
                                label = "周三",
                                values = listOf(
                                    Bars.Data(value = 1423.6, color = SolidColor(Color(0xff4DFDEB)))
                                ),
                            ),
                            Bars(
                                label = "周四",
                                values = listOf(

                                    Bars.Data(value = 4000.8, color = SolidColor(Color(0xff4DFDEB)))
                                ),
                            ),
                            Bars(
                                label = "周五",
                                values = listOf(

                                    Bars.Data(value = 7921.6, color = SolidColor(Color(0xff4DFDEB)))
                                ),
                            ),
                        )
                    },
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(
                            topRight = 8.dp,
                            topLeft = 8.dp,
                            bottomLeft = 8.dp,
                            bottomRight = 8.dp
                        ),
                        spacing = 3.dp,
                        thickness = 40.dp
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),

                    labelProperties = LabelProperties(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        enabled = true
                    ),
                    // 网格线设置
                    gridProperties = GridProperties(enabled = false),
                    dividerProperties = DividerProperties(enabled = false),
                    //垂直坐标轴
                    indicatorProperties = HorizontalIndicatorProperties(
                        enabled = false
                    )
                )
            }
        }
    }
}

@Preview(
    name = "ChartsPreview",
    showBackground = true,
//    widthDp = 200,
//    heightDp = 100
)
@Composable
fun ChartsPreview() {
//    BGTest()
//    LazyListPage()
    ChartsPage()
}
