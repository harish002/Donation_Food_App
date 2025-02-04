package com.example.foodapp.ui.Screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.fooddonateapp.R
import com.example.fooddonateapp.ui.Screens.Screens.ContactItem
import com.example.fooddonateapp.ui.Screens.Screens.Donor.DonationList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun User(mAuth: FirebaseAuth, navController: NavHostController) {
    var name = remember { mutableStateOf<String?>(null) }

    val id = mAuth.currentUser?.uid

    val role = remember { mutableStateOf<String?>(null) }

    val databaseRef = id?.let { FirebaseDatabase.getInstance().getReference("users").child(it) }

    databaseRef?.child("username")?.get()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val username = task.result?.value as? String
            // Use the fetched phone number
            name.value = username

            Log.d("Username", "Fetched phone: $username")
        } else {
            // Handle failure
            name.value = null
            Log.e("PhoneNumber", "Failed to fetch phone number", task.exception)
        }
    }

    LaunchedEffect(mAuth) {
        getUserRole { userRole ->
            role.value = userRole
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            CircularProfile(name.value, mAuth = mAuth)

            Spacer(modifier = Modifier.height(12.dp))

            if (role.value == "User") {
                val items = listOf(
                    Pair("Donations", R.drawable.donationbox) to { navController.navigate("FoodDonationlist") },
                    Pair("About Us", R.drawable.aboutus) to { navController.navigate("about_us") },
                    Pair("Contact Us", R.drawable.contact) to { navController.navigate("contact_us") }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { (item, action) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable { action() },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.elevatedCardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp)
                                    .padding(horizontal = 12.dp)
                                ,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.second),
                                    contentDescription = item.first,
                                    modifier = Modifier.size(100.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

            }
            else if (role.value == "Donor") {
                val items = listOf(
                    Pair("Donate Food", R.drawable.donate) to { navController.navigate("add_FoodDonation") },
                    Pair("Your Donate List", R.drawable.donationbox) to { navController.navigate("FoodDonationlist") },
                    Pair("Inquiries", R.drawable.foodappbg) to { navController.navigate("Donor_orders") } ,
                    Pair("About Us", R.drawable.aboutus) to { navController.navigate("about_us") },
                    Pair("Contact Us", R.drawable.contact) to { navController.navigate("contact_us") }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { (item, action) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { action() },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.elevatedCardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.second),
                                    contentDescription = item.first,
                                    modifier = Modifier.size(100.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }




            Button(
                onClick = {
                    mAuth.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Clears the entire backstack
                        launchSingleTop = true // Prevents duplicate destinations
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    "Logout",
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = Color.White
                )
            }



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



@SuppressLint("RestrictedApi")
@Composable
fun CircularProfile(userData: String?, mAuth: FirebaseAuth) {
    var userName = userData
    val id = mAuth.currentUser?.uid
    val initials = userName?.split(" ")?.joinToString("") { it.take(1) }?.uppercase()

    val role = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(mAuth) {
        getUserRole { userRole ->
            role.value = userRole
        }
    }

    // State to manage the dialog visibility
    val showProfileDialog = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(top = 30.dp, start = 8.dp)
    ) {
        // Circular Profile Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    Color.White,
                    shape = CircleShape
                )
                .border(2.dp, Color.Black, shape = CircleShape)
                .padding(8.dp)
                .clickable { showProfileDialog.value = true }, // Show dialog on tap
            contentAlignment = Alignment.Center
        ) {
            if (initials != null) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

    // Profile Dialog
    if (showProfileDialog.value) {
        AlertDialog(
            onDismissRequest = { showProfileDialog.value = false },
            title = { Text("Profile Details") },
            text = {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    var number = remember { mutableStateOf<String?>(null) }
                    var name = remember { mutableStateOf<String?>(null) }
                    val databaseRef = id?.let { FirebaseDatabase.getInstance().getReference("users").child(it) }
                    if (databaseRef != null) {
                        databaseRef.child("phone").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val phone = task.result?.value as? String
                                // Use the fetched phone number
                                number.value = phone

                                Log.d("PhoneNumber", "Fetched phone: $phone")
                            } else {
                                // Handle failure
                                number.value = null
                                Log.e("PhoneNumber", "Failed to fetch phone number", task.exception)
                            }
                        }

                        databaseRef.child("username").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val username = task.result?.value as? String
                                // Use the fetched phone number
                                name.value = username

                                Log.d("Username", "Fetched phone: $username")
                            } else {
                                // Handle failure
                                name.value = null
                                Log.e("PhoneNumber", "Failed to fetch phone number", task.exception)
                            }
                        }
                    }



                    ProfileItem(icon = Icons.Default.AccountCircle, text = "Username", value = "${name.value?.capitalize()}")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileItem(icon = Icons.Default.Email, text = "Email-Id", value = mAuth.currentUser?.email ?: "Not available")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileItem(icon = Icons.Default.Phone, text = "Phone", value = number.value ?: "Not available")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileItem(icon = Icons.Default.Person, text = "Role", value = role.value ?: "Not available" )


                    // Add more details as needed
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog.value = false }) {
                    Text("Close")
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ProfileItem(icon: ImageVector, text: String, value: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(24.dp))

            Spacer(modifier = Modifier.width(6.dp))

            Text(text = text, style = MaterialTheme.typography.bodyLarge,fontSize = 18.sp)

        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontSize = 20.sp, modifier = Modifier.padding(start = 6.dp), fontWeight = FontWeight.Bold)
    }

}
