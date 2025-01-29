package com.example.foodapp.ui.Screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fooddonateapp.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun donor(navController: NavHostController) {
    val context = LocalContext.current
    val voteList = getVotes(context).collectAsState(initial = emptyList())


    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Donor Dashboard",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface)

        Spacer(Modifier.padding(vertical = 8.dp))

        CheckInCard(type = "Donate Food", time = "", drawable = R.drawable.foodlogo_removebg_preview){
            navController.navigate("add_FoodDonation")
//            {
//                popUpTo(navController.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                restoreState = true
//            }
        }
        Spacer(Modifier.padding(2.dp))
        CheckInCard(type = "Your donate List", time = "", drawable = R.drawable.foodlogo_removebg_preview){
            navController.navigate("FoodDonationlist")
//            {
//                popUpTo(navController.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                restoreState = true
//            }
        }
        Spacer(Modifier.padding(2.dp))
        CheckInCard(type = "Orders", time = "", drawable = R.drawable.foodlogo_removebg_preview){
            navController.navigate("Donor_orders")
//            {
//                popUpTo(navController.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                restoreState = true
//            }
        }
        Spacer(modifier = Modifier.padding(40.dp))

    }
}

@Composable
fun CheckInCard(
    type: String,
    time: String = "--:--",
    drawable: Int,
    modifier: Modifier = Modifier, // Allow passing a modifier for flexibility
    onClick: ()->Unit,
) {
    Card(
        onClick = {onClick()},
        shape = RoundedCornerShape(16.dp),
        modifier = modifier // Use the passed modifier
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = drawable),
                    contentDescription = "Check in/out",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(30))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Use width instead of padding for spacing
                Text(
                    text = type,
                    style = MaterialTheme.typography.bodyMedium // Use Material typography for consistency
                )
            }
        }
    }
}

@Composable
fun getVotes(context: Context): StateFlow<List<String>> {
    val _voteList = remember { MutableStateFlow<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Vote")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (!queryDocumentSnapshots.isEmpty) {
                    val list = queryDocumentSnapshots.documents
                    val tempVoteList = mutableListOf<String>()
                    for (d in list) {
                        tempVoteList.add(d.data.toString())
                    }
                    _voteList.value = tempVoteList
                } else {
                    Toast.makeText(
                        context,
                        "No data found in Database",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Fail to get the data. $e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    return _voteList
}








