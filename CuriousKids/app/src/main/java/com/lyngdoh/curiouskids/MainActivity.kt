package com.lyngdoh.curiouskids

import AdditionScreen
import ChattingPage
import DivisionScreen
import HomeScreen
import MathFunMenu
import MultiplicationScreen
import SplashScreen
import SubtractionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lyngdoh.curiouskids.ui.theme.CuriousKidsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request camera permission
        /*if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }*/

        setContent {
            CuriousKidsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController) }
                        composable("home") { HomeScreen(navController) }
                        composable("storyTelling") { KidsStoriesScreen(navController) }
                        composable("imageIdentification") { ImageIdentification(navController) }
                        composable("mathGames") { MathFunMenu(navController) }
                        composable("chatting") { ChattingPage(navController) }
                        composable("spellingBeeGame") { SpellingBeeGame(navController) }
                        composable("addition") { AdditionScreen(navController) }
                        composable("subtraction") { SubtractionScreen(navController) }
                        composable("multiplication") { MultiplicationScreen(navController) }
                        composable("division") { DivisionScreen(navController) }
                        composable(
                            "storyDetail/{storyTitle}",
                            arguments = listOf(navArgument("storyTitle") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val storyTitle = backStackEntry.arguments?.getString("storyTitle") ?: ""
                            KidsStoryDetail(navController, storyTitle)
                        }
                        composable("generateStory") {
                            // Implement Generate Story Screen
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app.
        } else {
            // Explain to the user that the feature is unavailable because
            // the features require a permission that the user has denied.
        }
    }
}
