package com.czy.miniproject

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.czy.miniproject.theme.MiniProjectTheme
import com.czy.miniproject.uiscreens.ChangePasswordScreen
import com.czy.miniproject.uiscreens.DashboardScreen
import com.czy.miniproject.uiscreens.KartuStudiScreen
import com.czy.miniproject.uiscreens.LoginScreen
import com.czy.miniproject.uiscreens.MataKuliahScreen
import com.czy.miniproject.uiscreens.RegisterScreen
import com.czy.miniproject.uiscreens.TranskripScreen
import com.czy.miniproject.viewmodels.AuthViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiniProjectTheme {
                MiniProjectApp()
            }
        }

//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}

@Composable
fun MiniProjectApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser

    // Auto logout when app is killed
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.logout()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = if (currentUser != null) "dashboard" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate("register") },
                    onLoginSuccess = { navController.navigate("dashboard") },
                    authViewModel = authViewModel
                )
            }
            composable("register") {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate("login") },
                    onRegisterSuccess = { navController.navigate("login") },
                    authViewModel = authViewModel
                )
            }
            composable("dashboard") {
                DashboardScreen(
                    user = currentUser,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable("mata_kuliah") {
                MataKuliahScreen(
                    user = currentUser,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("kartu_studi") {
                KartuStudiScreen(
                    user = currentUser,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("transkrip") {
                TranskripScreen(
                    user = currentUser,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("change_password") {
                ChangePasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}