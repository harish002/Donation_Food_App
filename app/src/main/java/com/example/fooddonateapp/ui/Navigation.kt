package com.example.fooddonateapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodapp.ui.Screens.LoginScreen
import com.example.foodapp.ui.Screens.RegisterScreen
import com.example.foodapp.ui.Screens.User
import com.example.foodapp.ui.Screens.donor
import com.example.fooddonateapp.ui.Screens.Screens.Donor.DonateFood
import com.example.fooddonateapp.ui.Screens.Screens.Donor.DonationList
import com.google.firebase.auth.FirebaseAuth


@Composable
fun NavGraph(navController: NavHostController) {
    val votesList = mutableMapOf<String, Boolean>()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(votesList, navController)
            }
            composable("register") {
                RegisterScreen(navController)
            }
            composable("user") {
                User(FirebaseAuth.getInstance(),navController)
//                DonationList(FirebaseAuth.getInstance()) // Ensure 'voting' is @Composable
            }
            composable("donor") {
                donor(navController) // Ensure 'admin' is @Composable
            }
            composable("add_FoodDonation") {
                DonateFood(navController) // Ensure 'admin' is @Composable
            }

            composable("FoodDonationlist") {
                DonationList(mAuth = FirebaseAuth.getInstance()) // Ensure 'admin' is @Composable
            }

            composable("Donor_orders") {
                Text("Food Order list") // Ensure 'admin' is @Composable
            }

            composable("voted_successfully") { // Changed to use underscores for consistency
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Voted Successfully")
                }
            }
        }
    }
}
