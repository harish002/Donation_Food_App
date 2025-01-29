package com.example.foodapp.ui.Screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fooddonateapp.ui.Screens.Screens.Donor.DonationList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun User( mAuth: FirebaseAuth,
          navController: NavHostController){

   val name= mAuth.currentUser?.email
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {/* No title */ }, // Leave the title empty or omit this line
                navigationIcon = {
                    if (scrollBehavior.state.collapsedFraction < 1f) {
                        CircularProfile(name) // Replace with dynamic data from api
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.LightGray,
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection) // Link scroll behavior
        ) {
                DonationList(mAuth)
        }
    }
}

private fun updateVote(
    context: Context,
    userRef: DocumentReference,
    selectedCandidate: String,
    navController: NavHostController,
    userId: String
) {
    userRef.get()
        .addOnSuccessListener { document ->
            val currentCount = document.getDouble(selectedCandidate)?.toInt() ?: 0
            userRef.update(selectedCandidate, currentCount + 1)
                .addOnSuccessListener {
                    Toast.makeText(context, "Vote recorded successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("Voted Successfully")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error recording vote: $e", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error recording vote: $e", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun RadioButton(
    text: String,
    isSelected: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOptionSelected(text) }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.RadioButton(
            selected = isSelected,
            onClick = { onOptionSelected(text) }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


// Save the JSON string to SharedPreferences
fun save_votes(context: Context, key: String) {
    val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("votes", key)
    editor.apply()
}

fun get_votes(context: Context): String? {
    val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    return sharedPref.getString("votes", null)
}


data class IsvoteData(
    val username: String,
    val isvoted: Boolean
)


//all Donation
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
                Log.d("allDonation", donationData.toString())
            }
        }
        onResult(donations)
    }.addOnFailureListener { exception ->
        onResult(emptyList())
        Log.e("Firebase", "Error fetching all donations", exception)

    }
}


fun getUserRole(onResult: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!)

    databaseRef.get().addOnSuccessListener { snapshot ->
        val role = snapshot.child("role").value as? String
        if (role != null) {
            onResult(role)
        }
        if (role != null) {
            Log.d("role value", role)
        }
    }.addOnFailureListener {
        Log.e("Firebase", "Error fetching user role", it)
        onResult("donor") // Default to "donor" on failure
    }
}



@Composable
fun CircularProfile(userData: String?) {
    val userName = userData
    val initials = userName?.split(" ")?.joinToString("") { it.take(1) }?.uppercase()

    Row(
        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(8.dp) // Optional padding for the row
    ) {
        // Circular Profile Icon
            Box(
                modifier = Modifier
                    .size(60.dp) // Size of the circular icon
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    ) // Circular shape with background color
                    .border(2.dp, Color.White, shape = CircleShape) // Optional border
                    .padding(8.dp), // Padding inside the circle
                contentAlignment = Alignment.Center
            ) {
                if (initials != null) {
                    Text(
                        text = initials,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
        // Welcome Message
        Column {
                            Text(
                        text = "Welcome",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

            userData?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}