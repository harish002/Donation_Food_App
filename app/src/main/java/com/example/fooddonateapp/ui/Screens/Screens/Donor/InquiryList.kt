package com.example.fooddonateapp.ui.Screens.Screens.Donor

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foodapp.ui.Screens.getUserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun InquiryList(
    mAuth: FirebaseAuth,
    navController: NavHostController
) {
    val inquiries = remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    val role = remember { mutableStateOf<String?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var selectedInquiry by remember { mutableStateOf<Map<String, String>?>(null) }
    val context = LocalContext.current

    LaunchedEffect(mAuth) {
        getUserRole { userRole ->
            role.value = userRole
            if (userRole == "User") {
                com.example.foodapp.ui.Screens.fetchAllDonations { result ->
                    inquiries.value = result
                }
            } else {
                val email = mAuth.currentUser?.email
                if (email != null) {
                    fetchUserInquiries(email) { result ->
                        inquiries.value = result
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .padding(top = 30.dp)
    ) {
        Text(
            text = "Inquiries",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(start = 6.dp)
        )

        if (inquiries.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No Inquiries found.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp) // Adds spacing at the bottom

            ) {
                items(inquiries.value) { inquiry ->
                    InquiryCard(
                        inquiry = inquiry,
                        role = role.value,
                        onCardClick = {
                            selectedInquiry = inquiry
                            isPopupVisible = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InquiryCard(
    inquiry: Map<String, String>,
    role: String?,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (role == "User") onCardClick() }
            .padding(horizontal = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Name: ${inquiry["name"]}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Email: ${inquiry["email"]}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Mobile No.: ${inquiry["phone"]}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Message: ${inquiry["message"]}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun fetchUserInquiries(email: String, onResult: (List<Map<String, String>>) -> Unit) {
    val sanitizedEmail = email.replace(".", "_")
    val databaseRef = FirebaseDatabase.getInstance().getReference("inquiries")

    databaseRef.child(sanitizedEmail).get().addOnSuccessListener { snapshot ->
        val inquiries = mutableListOf<Map<String, String>>()
        for (donationSnapshot in snapshot.children) {
            val inquiryData = donationSnapshot.value as? Map<String, String>
            if (inquiryData != null) {
                inquiries.add(inquiryData)
            }
        }
        onResult(inquiries) // Pass the donations back to the composable
    }.addOnFailureListener { exception ->
        onResult(emptyList()) // Return an empty list if there's an error
        Log.e("Firebase", "Error fetching donations", exception)
    }
}