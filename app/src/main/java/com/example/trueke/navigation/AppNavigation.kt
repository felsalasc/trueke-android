package com.example.trueke.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trueke.ui.screens.ForgotPasswordScreen
import com.example.trueke.ui.screens.LoginScreen
import com.example.trueke.ui.screens.RegisterScreen
import com.example.trueke.ui.screens.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {

            LoginScreen(

                onLoginClick = {
                    navController.navigate("home")
                },

                onRegisterClick = {
                    navController.navigate("register")
                },

                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )
        }

        composable("register") {

            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("forgot_password") {

            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {

            HomeScreen()

        }
    }
}