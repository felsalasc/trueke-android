package com.example.trueke.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trueke.ui.screens.ForgotPasswordScreen
import com.example.trueke.ui.screens.LoginScreen
import com.example.trueke.ui.screens.RegisterScreen
import com.example.trueke.ui.screens.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val auth = remember { FirebaseAuth.getInstance() }
    val startDestination = remember {
        if (auth.currentUser != null) "home" else "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {

            LoginScreen(

                onLoginClick = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id)
                        launchSingleTop = true
                    }
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

            if (auth.currentUser != null) {
                HomeScreen(
                    onLogout = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.id)
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                // Protege una ruta restaurada si Firebase ya no conserva la sesión.
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id)
                        launchSingleTop = true
                    }
                }
            }

        }
    }
}
