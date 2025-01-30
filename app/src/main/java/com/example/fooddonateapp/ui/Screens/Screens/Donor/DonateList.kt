package com.example.fooddonateapp.ui.Screens.Screens.Donor

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.example.foodapp.ui.Component.custTextInput
import com.example.foodapp.ui.Screens.getUserRole
import com.example.fooddonateapp.R
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
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
    navController: NavHostController
) {
    val donations = remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    val role = remember { mutableStateOf<String?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var selectedDonation by remember { mutableStateOf<Map<String, String>?>(null) }
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(top = 40.dp)
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = "Donations",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp, start = 12.dp)
        )

        if (donations.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No donations found.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(donations.value) { donation ->
                    DonationCard(
                        donation = donation,
                        role = role.value,
                        onCardClick = {
                            selectedDonation = donation
                            isPopupVisible = true // Show popup on card click
                        }
                    )

                    if(isPopupVisible){
                        var name by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var phone by remember { mutableStateOf("") }
                        var message by remember {mutableStateOf("")}
                        val databaseRef = FirebaseDatabase.getInstance().getReference("inquiries")

                        Popup(

                            onDismissRequest = {
                                isPopupVisible = false
                            },
                            alignment = Alignment.Center,
                            properties = PopupProperties(focusable = true,dismissOnBackPress = true)
                        ) {
                            Box(
                                modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                                .clickable { isPopupVisible = false }, // Close the popup when clicking outside
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

                                        custTextInput(
                                            label = "Name",
                                            value = name,
                                            onValueChange = { name = it })
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

                                        custTextInput(
                                            label = "Message",
                                            value = message,
                                            onValueChange = { message = it })
                                        Spacer(modifier = Modifier.padding(10.dp))

                                        Button(
                                            enabled = (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()),
                                            onClick = {
                                                val inquiryId = databaseRef.push().key // Generate a unique Inquiry ID
                                                val inquiryData = mapOf(
                                                    "name" to name,
                                                    "email" to email,
                                                    "phone" to phone,
                                                    "message" to message
                                                )

                                                if (inquiryId != null) {
                                                    val sanitizedEmail =
                                                        selectedDonation?.get("donatedBy")
                                                            ?.replace(".", "_")
                                                    if (sanitizedEmail != null) {
                                                        databaseRef.child(sanitizedEmail).child(inquiryId).setValue(inquiryData)
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Inquiry Sent Successfully!",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    isPopupVisible = false
                                                                    selectedDonation = null

                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Failed to send inquiry: ${task.exception?.message}",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    isPopupVisible = false
                                                                    selectedDonation = null
                                                                }
                                                            }
                                                    }
                                                }
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

}

@Composable
fun DonationCard(
    donation: Map<String, String>,
    role: String?,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (role == "User") {
                    onCardClick()
                } else {

                }
            }
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Food: ${donation["food"]}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text("Quantity: ${donation["quantity"]}", style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text("Location: ${donation["location"]}", style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text("Phone: ${donation["phone"]}", style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text("Donated By: ${donation["donatedBy"]}", style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.donate),
                contentDescription = "Donation",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 12.dp)
            )
        }

    }
}

@Composable
fun InquiryPopup(
    donation: Map<String, String>?,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val databaseRef = FirebaseDatabase.getInstance().getReference("inquiries")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }, // Close the popup when clicking outside
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Send Inquiry",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                custTextInput(label = "Name", value = name, onValueChange = { name = it })
                Spacer(modifier = Modifier.height(8.dp))

                custTextInput(label = "Email", value = email, onValueChange = { email = it })
                Spacer(modifier = Modifier.height(8.dp))

                custTextInput(label = "Phone", value = phone, onValueChange = { phone = it })
                Spacer(modifier = Modifier.height(8.dp))

                custTextInput(label = "Message", value = message, onValueChange = { message = it })
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank(),
                    colors = ButtonDefaults.elevatedButtonColors(),
                    onClick = {
                        val inquiryId = databaseRef.push().key
                        val inquiryData = mapOf(
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "message" to message
                        )

                        inquiryId?.let {
                            val sanitizedEmail = donation?.get("donatedBy")?.replace(".", "_")
                            sanitizedEmail?.let {
                                databaseRef.child(it).child(inquiryId).setValue(inquiryData)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Inquiry Sent Successfully!", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        }
                    }
                ) {
                    Text("Send Inquiry")
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






