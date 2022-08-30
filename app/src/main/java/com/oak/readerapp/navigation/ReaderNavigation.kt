package com.oak.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oak.readerapp.screens.SplashScreen
import com.oak.readerapp.screens.home.HomeScreen
import com.oak.readerapp.screens.login.LoginScreen
import com.oak.readerapp.screens.search.SearchScreen


@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            HomeScreen(navController = navController)
        }

        composable(ReaderScreens.SearchScreen.name) {
            SearchScreen(navController = navController)
        }

        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
    }
}