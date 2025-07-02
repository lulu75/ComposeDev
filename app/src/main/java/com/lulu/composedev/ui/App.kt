package com.lulu.composedev.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.bit.atbusiness.navigation.AppNavGraph
import com.bit.atbusiness.navigation.Route
import com.lulu.composedev.ui.theme.ComposeDevTheme


/**
 * 作者: shilu
 * 时间: 2025/5/8
 * 描述: 主入口
 * 	•	包裹全局主题 MaterialTheme
 * 	•	提供全局 ProvideTextStyle、CompositionLocal 等
 * 	•	内部调用 AppNavGraph() 作为根导航结构
 * 	•	未来可以更灵活地支持多端（如 WearOS、TV）或登录前后分离的导航结构
 */
@Composable
fun App(viewModelProvider: ViewModelProvider) {
    ComposeDevTheme {
        val navController = rememberNavController()
        val startRout by remember { mutableStateOf(Route.HOME) }
        LaunchedEffect(Unit) {

        }

        if (startRout.isNotEmpty())
            AppNavGraph(navController = navController, viewModelProvider, startRout)
    }

}





