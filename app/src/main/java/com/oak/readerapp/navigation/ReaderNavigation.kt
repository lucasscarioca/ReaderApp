package com.oak.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oak.readerapp.screens.SplashScreen
import com.oak.readerapp.screens.details.BookDetailsScreen
import com.oak.readerapp.screens.home.HomeScreen
import com.oak.readerapp.screens.home.HomeScreenViewModel
import com.oak.readerapp.screens.login.LoginScreen
import com.oak.readerapp.screens.search.BookSearchViewModel
import com.oak.readerapp.screens.search.SearchScreen
import com.oak.readerapp.screens.stats.StatsScreen
import com.oak.readerapp.screens.update.UpdateScreen


@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(ReaderScreens.SearchScreen.name) {
            val viewModel = hiltViewModel<BookSearchViewModel>()
            SearchScreen(navController = navController, viewModel)
        }

        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            StatsScreen(navController = navController)
        }

        composable(ReaderScreens.DetailScreen.name + "/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }

        composable(ReaderScreens.UpdateScreen.name + "/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                UpdateScreen(navController = navController, bookId = it.toString())
            }
        }
    }
}