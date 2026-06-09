package com.example.bankapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserDashboardScreen(
    onDepositClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
    onTransferClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var accountNumber by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf(0.0) }
    var email by remember { mutableStateOf("") }

    val blue = Color(0xFF064BD8)

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { userDoc ->
                    email = userDoc.getString("email") ?: ""
                    val accNo = userDoc.getString("accountNumber") ?: ""
                    accountNumber = accNo

                    if (accNo.isNotEmpty()) {
                        db.collection("accounts").document(accNo).get()
                            .addOnSuccessListener { accDoc ->
                                balance = accDoc.getDouble("balance") ?: 0.0
                            }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .background(blue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "My Dashboard",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Account Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("Email", color = Color.Gray, fontSize = 13.sp)
                    Text(email, fontSize = 15.sp)

                    Spacer(Modifier.height(12.dp))

                    Text("Account Number", color = Color.Gray, fontSize = 13.sp)
                    Text(accountNumber, fontSize = 17.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(12.dp))

                    Text("Current Balance", color = Color.Gray, fontSize = 13.sp)
                    Text(
                        text = "Rs. $balance",
                        color = Color(0xFF0B8F3A),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                UserActionCard(
                    title = "Deposit",
                    subtitle = "Add Money",
                    icon = "💰",
                    bgColor = Color(0xFFEAF8EF),
                    modifier = Modifier.weight(1f),
                    onClick = onDepositClick
                )

                Spacer(Modifier.width(12.dp))

                UserActionCard(
                    title = "Withdraw",
                    subtitle = "Cash Out",
                    icon = "💸",
                    bgColor = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f),
                    onClick = onWithdrawClick
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                UserActionCard(
                    title = "Transfer",
                    subtitle = "Send Money",
                    icon = "🔄",
                    bgColor = Color(0xFFF3E8FF),
                    modifier = Modifier.weight(1f),
                    onClick = onTransferClick
                )

                Spacer(Modifier.width(12.dp))

                UserActionCard(
                    title = "History",
                    subtitle = "Transactions",
                    icon = "📜",
                    bgColor = Color(0xFFEAF2FF),
                    modifier = Modifier.weight(1f),
                    onClick = onHistoryClick
                )
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    auth.signOut()
                    onLogoutClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun UserActionCard(
    title: String,
    subtitle: String,
    icon: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}