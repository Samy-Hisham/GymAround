package com.android.gymaround

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.gymaround.ui.theme.GymAroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymAroundTheme {
                GymAroundApp()
            }
        }
    }
}

@Composable
private fun GymAroundApp() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gyms") {
        composable(route = "gyms") {
            GymsScreen { id ->
                navController.navigate("gym/$id")
            }
        }

        composable(route = "gym/{gym_id}", arguments = listOf(

            navArgument("gym_id") {
                type = NavType.IntType
            }
        )
        ) {
//            val gymId = it.arguments?.getInt("gym_id")
            GymDetailScreen()
        }
    }
}