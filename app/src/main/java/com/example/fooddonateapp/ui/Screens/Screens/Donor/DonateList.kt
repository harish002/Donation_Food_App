package com.example.fooddonateapp.ui.Screens.Screens.Donor

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.foodapp.ui.Component.custTextInput
import com.example.foodapp.ui.Screens.getUserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

//WORKING
//@Composable
//fun DonationList(
//    mAuth: FirebaseAuth,
//) {
//    val email = mAuth.currentUser?.email
//    val donations = remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
//
//    LaunchedEffect(email) {
//        if (email != null) {
//            fetchUserDonations(email) { result ->
//                donations.value = result
//            }
//        }
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        if (donations.value.isEmpty()) {
//            item {
//                Text(
//                    "No donations found.",
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        } else {
//            items(donations.value) { donation ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text("Food: ${donation["food"]}")
//                        Text("Quantity: ${donation["quantity"]}")
//                        Text("Location: ${donation["location"]}")
//                        Text("Phone: ${donation["phone"]}")
//                        Text("Donated By: ${donation["donatedBy"]}")
//                    }
//                }
//            }
//        }
//    }
//
//}

@Composable
fun DonationList(
    mAuth: FirebaseAuth,
) {
    val donations = remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    val role = remember { mutableStateOf<String?>(null) }
    var isPopupVisible by remember { mutableStateOf(false)}


    LaunchedEffect(mAuth) {
        getUserRole { userRole ->
            role.value = userRole
            if (userRole == "User") {
                com.example.foodapp.ui.Screens.fetchAllDonations { result ->
                    donations.value = result
                }
            } else {
                val email = mAuth.currentUser?.email
                if (email != null) {
                    fetchUserDonations(email) { result ->
                        donations.value = result
                    }
                }
            }
        }
    }

        LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (donations.value.isEmpty()) {
            item {
                Text(
                    "No donations found.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(donations.value) { donation ->
                Card(
                    modifier = Modifier
                        .clickable {
                            if (role.value == "User") {
                                isPopupVisible = true
                            } else {
                            }
                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Food: ${donation["food"]}")
                        Text("Quantity: ${donation["quantity"]}")
                        Text("Location: ${donation["location"]}")
                        Text("Phone: ${donation["phone"]}")
                        Text("Donated By: ${donation["donatedBy"]}")
                    }
                }

                if(isPopupVisible){
                    var name by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var phone by remember { mutableStateOf("") }
                    val databaseRef = FirebaseDatabase.getInstance().getReference("Inquiries")

                    Popup(

                        onDismissRequest = {
                            isPopupVisible = false
                        },
                        alignment = Alignment.Center,
                        properties = PopupProperties(focusable = true,dismissOnBackPress = true)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(1f)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Card (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, 16.dp)
                                ,
                                border = BorderStroke(width = 2.dp, color = Color.Gray)
                            ){
                                Column (
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(
                                        "Send Inquiry",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    custTextInput(label = "Name",

                                        value = name, onValueChange = { name = it })
                                    Spacer(modifier = Modifier.padding(10.dp))

                                    custTextInput(
                                        label = "test",
                                        value = password,
                                        onValueChange = { password = it })
                                    Spacer(modifier = Modifier.padding(10.dp))

                                    custTextInput(
                                        label = "Email",
                                        value = email,
                                        onValueChange = { email = it })
                                    Spacer(modifier = Modifier.padding(10.dp))

                                    custTextInput(
                                        label = "Phone",
                                        value = phone,
                                        onValueChange = { phone = it })
                                    Spacer(modifier = Modifier.padding(10.dp))

                                    Button(
                                        enabled = (password.isNotBlank() && name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()),
                                        onClick = {
                                            val inquiryId = databaseRef.push().key // Generate a unique donation ID
                                            val donationData = mapOf(
                                                "name" to name,
                                                "email" to email,
                                                "phone" to phone,
                                                "sendQueryTo" to email
                                            )

//                                            if (donationId != null) {
//                                                val sanitizedEmail = email.replace(".", "_")
//                                                databaseRef.child(sanitizedEmail).child(donationId).setValue(donationData)
//                                                    .addOnCompleteListener { task ->
//                                                        if (task.isSuccessful) {
//                                                            Toast.makeText(
//                                                                context,
//                                                                "Donation added successfully!",
//                                                                Toast.LENGTH_SHORT
//                                                            ).show()
//                                                            navController.popBackStack()
//                                                        } else {
//                                                            Toast.makeText(
//                                                                context,
//                                                                "Failed to add donation: ${task.exception?.message}",
//                                                                Toast.LENGTH_SHORT
//                                                            ).show()
//                                                        }
//                                                    }
//                                            }
                                        }) {
                                        Text("Send Inquiry")
                                    }
                                    Spacer(modifier = Modifier.padding(10.dp))
                                }
                            }
                        }

                    }
                }

            }
        }
    }

}


fun fetchUserDonations(email: String, onResult: (List<Map<String, String>>) -> Unit) {
    val sanitizedEmail = email.replace(".", "_")
    val databaseRef = FirebaseDatabase.getInstance().getReference("donations")

    databaseRef.child(sanitizedEmail).get().addOnSuccessListener { snapshot ->
        val donations = mutableListOf<Map<String, String>>()
        for (donationSnapshot in snapshot.children) {
            val donationData = donationSnapshot.value as? Map<String, String>
            if (donationData != null) {
                donations.add(donationData)
            }
        }
        onResult(donations) // Pass the donations back to the composable
    }.addOnFailureListener { exception ->
        onResult(emptyList()) // Return an empty list if there's an error
        Log.e("Firebase", "Error fetching donations", exception)
    }
}


