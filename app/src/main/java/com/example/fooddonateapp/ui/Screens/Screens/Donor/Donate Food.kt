package com.example.fooddonateapp.ui.Screens.Screens.Donor

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foodapp.ui.Component.custTextInput
import com.example.fooddonateapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

//@Composable
//fun DonateFood(navController: NavHostController) {
//    var food by remember { mutableStateOf("") }
//    var quantity by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var location by remember { mutableStateOf("") } // Default role
//    val context = LocalContext.current
//    val mAuth = FirebaseAuth.getInstance()
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//            .padding(20.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        item {
//            Image(
//                modifier = Modifier.size(100.dp),
//                painter = painterResource(R.drawable.foodlogo_removebg_preview),
//                contentDescription = "app logo",
//            )
//
//            Text(
//                "Add Food Description",
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            custTextInput(label = "Food Item", value = food, onValueChange = { food = it })
//            Spacer(modifier = Modifier.padding(4.dp))
//
//            custTextInput(label = "Quantity", value = quantity, onValueChange = { quantity = it })
//            Spacer(modifier = Modifier.padding(4.dp))
//
//            custTextInput(label = "Email", value = email, onValueChange = { email = it })
//            Spacer(modifier = Modifier.padding(4.dp))
//
//            custTextInput(label = "Location", value = location, onValueChange = { location = it })
//            Spacer(modifier = Modifier.padding(4.dp))
//
//            custTextInput(label = "Phone", value = phone, onValueChange = { phone = it })
//            Spacer(modifier = Modifier.padding(4.dp))
//
//
//            Spacer(modifier = Modifier.padding(20.dp))
//
//            Button(
//                enabled = (food.isNotBlank() && quantity.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && location.isNotBlank()),
//                onClick = {
//
//                }) {
//                Text("Donate")
//            }
//
//        }
//    }
//}

@Composable
fun DonateFood(navController: NavHostController,mAuth: FirebaseAuth) {
    var food by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(mAuth.currentUser?.email) }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val context = LocalContext.current
    val databaseRef = FirebaseDatabase.getInstance().getReference("donations")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(R.drawable.foodlogo_removebg_preview),
                contentDescription = "app logo",
            )

            Spacer(modifier = Modifier.padding(12.dp))

            Text(
                "Add Food Description",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.padding(12.dp))

            custTextInput(label = "Food Item", value = food, onValueChange = { food = it })
            Spacer(modifier = Modifier.padding(8.dp))

            custTextInput(label = "Quantity", value = quantity, onValueChange = { quantity = it })
            Spacer(modifier = Modifier.padding(8.dp))

            email?.let { custTextInput(label = "Email", value = it,
                readonly = true,
                onValueChange = { email = it }) }
            Spacer(modifier = Modifier.padding(8.dp))

            custTextInput(label = "Location", value = location,

                onValueChange = { location = it })
            Spacer(modifier = Modifier.padding(8.dp))

            custTextInput(label = "Phone", value = phone, onValueChange = { phone = it })
            Spacer(modifier = Modifier.padding(8.dp))

            Spacer(modifier = Modifier.padding(12.dp))

            Button(
                enabled = (food.isNotBlank() && quantity.isNotBlank() && email?.isNotBlank() == true && phone.isNotBlank() && location.isNotBlank()),
                onClick = {
                    val donationId = databaseRef.push().key // Generate a unique donation ID
                    val donationData = mapOf(
                        "food" to food,
                        "quantity" to quantity,
                        "location" to location,
                        "phone" to phone,
                        "donatedBy" to email
                    )

                    if (donationId != null) {
                        val sanitizedEmail = email?.replace(".", "_")
                        if (sanitizedEmail != null) {
                            databaseRef.child(sanitizedEmail).child(donationId).setValue(donationData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Donation added successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to add donation: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("Donate")
            }
        }
    }
}


//fun fetchUserDonations(email: String,
//                       onResult: (List<Map<String, String>>) -> Unit)
//{
//    val user = FirebaseAuth.getInstance().currentUser
//    val sanitizedEmail = user?.email?.replace(".", "_") ?: ""
//
//    val databaseRef = FirebaseDatabase.getInstance().getReference("donations")
//    databaseRef.child(sanitizedEmail).get().addOnSuccessListener { snapshot ->
//        val donations = mutableListOf<Map<String, String>>()
//        for (donationSnapshot in snapshot.children) {
//            val donationData = donationSnapshot.value as? Map<String, String>
//            if (donationData != null) {
//                donations.add(donationData)
//            }
//        }
//        Log.d("donation list",donations.toString())
//        // Handle donations (e.g., pass to a composable to display)
//    }
//
//}

//allDonation
fun fetchAllDonations(onResult: (List<Map<String, String>>) -> Unit) {
    val databaseRef = FirebaseDatabase.getInstance().getReference("donations")
    databaseRef.get().addOnSuccessListener { snapshot ->
        val donations = mutableListOf<Map<String, String>>()
        for (donorSnapshot in snapshot.children) {
            for (donationSnapshot in donorSnapshot.children) {
                val donationData = donationSnapshot.value as? Map<String, String>
                if (donationData != null) {
                    donations.add(donationData)
                }
            }
        }
        onResult(donations)
    }.addOnFailureListener {
        onResult(emptyList())
    }
}
