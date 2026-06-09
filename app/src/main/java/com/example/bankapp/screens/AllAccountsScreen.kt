package com.example.bankapp.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

data class BankAccount(
    val accountNumber: String = "",
    val userEmail: String = "",
    val balance: Double = 0.0,
    val status: String = ""
)

@Composable
fun AllAccountsScreen(
    onBackClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var accounts by remember { mutableStateOf(listOf<BankAccount>()) }

    LaunchedEffect(Unit) {
        db.collection("accounts")
            .get()
            .addOnSuccessListener { result ->
                accounts = result.documents.mapNotNull {
                    it.toObject(BankAccount::class.java)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = "← Back",
            color = Color(0xFF064BD8),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = "All Accounts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        LazyColumn {
            items(accounts) { account ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEAF2FF)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Account No: ${account.accountNumber}", fontWeight = FontWeight.Bold)
                        Text("Email: ${account.userEmail}")
                        Text("Balance: Rs. ${account.balance}")
                        Text("Status: ${account.status}", color = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}