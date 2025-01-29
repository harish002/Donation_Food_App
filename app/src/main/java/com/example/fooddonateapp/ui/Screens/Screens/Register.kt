package com.example.foodapp.ui.Screens

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foodapp.ui.Component.custTextInput
import com.example.fooddonateapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") } // Default role
    val context = LocalContext.current
    val mAuth = FirebaseAuth.getInstance()

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
                modifier = Modifier.size(200.dp),
                painter = painterResource(R.drawable.foodlogo_removebg_preview),
                contentDescription = "app logo",
            )

            Text("Food Donation App", style = MaterialTheme.typography.titleLarge)

            custTextInput(label = "Username", value = username, onValueChange = { username = it })
            Spacer(modifier = Modifier.padding(10.dp))

            custTextInput(label = "Password", value = password, onValueChange = { password = it })
            Spacer(modifier = Modifier.padding(10.dp))

            custTextInput(label = "Email", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.padding(10.dp))

            custTextInput(label = "Phone", value = phone, onValueChange = { phone = it })
            Spacer(modifier = Modifier.padding(10.dp))

            // Role Selector
            var expanded by remember { mutableStateOf(false) }
            var selectedRole by remember { mutableStateOf("User") }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Role: ", style = MaterialTheme.typography.bodyLarge)

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = { expanded = true })
                            .background(Color.White)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedRole)

                        // Add the dropdown indicator icon
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("User", color = Color.White)
                            }, onClick = {
                                selectedRole = "User"
                                expanded = false
                            })
                        DropdownMenuItem(
                            text = {
                                Text("Donor", color = Color.White)
                            }, onClick = {
                                selectedRole = "Donor"
                                expanded = false
                            })
                    }
                }
            }




            Spacer(modifier = Modifier.padding(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    navController.navigate("login")
                }) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    enabled = (password.isNotBlank() && username.isNotBlank() && email.isNotBlank() && phone.isNotBlank()),
                    onClick = {
                    registerNewUser(
                        context,
                        mAuth,
                        UserRegister(username, password, phone, email, selectedRole),
                        navController
                    )
                }) {
                    Text("Register")
                }
            }
        }
    }
}


fun registerNewUser(
    context: Context,
    mAuth: FirebaseAuth,
    user: UserRegister,
    navController: NavHostController
) {
    mAuth.createUserWithEmailAndPassword(user.email, user.password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save user data with role in Firebase Realtime Database or Firestore
                val userId = mAuth.currentUser?.uid
                val databaseRef =
                    FirebaseDatabase.getInstance().getReference("users").child(userId!!)
                val userData = mapOf(
                    "username" to user.username,
                    "phone" to user.phone,
                    "email" to user.email,
                    "role" to user.role
                )


                databaseRef.setValue(userData).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate("login")
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to save user data: ${dbTask.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }



                }
            } else {
                Toast.makeText(
                    context,
                    "Registration failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}


data class UserRegister(
    val username: String,
    val password: String,
    val phone: String,
    val email: String,
    val role: String // New property for user role
)
