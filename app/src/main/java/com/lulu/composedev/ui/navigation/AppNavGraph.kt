package com.bit.atbusiness.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lulu.composedev.ui.page.HomePage
import com.lulu.composedev.ui.page.LandscapeScreen


/**
 * 作者: shilu
 * 时间: 2025/5/8
 * 描述: App 导航图
 */
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModelProvider: ViewModelProvider,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.HOME) {
            HomePage(navController) {
                navController.navigate(Route.LANDSCAPE)
            }
        }

        composable(Route.IMGPreview) {
            LandscapeScreen()
        }


    }
}
