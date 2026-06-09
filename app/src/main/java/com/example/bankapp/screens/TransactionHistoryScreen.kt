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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val type: String = "",
    val amount: Double = 0.0,
    val fromAccount: String? = null,
    val toAccount: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null
)

@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    var transactions by remember { mutableStateOf(listOf<Transaction>()) }

    LaunchedEffect(uid) {
        db.collection("transactions")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                transactions = result.documents
                    .mapNotNull { it.toObject(Transaction::class.java) }
                    .sortedByDescending { it.timestamp?.seconds ?: 0 }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "← Back",
            color = Color(0xFF064BD8),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Transaction History",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val date = tx.timestamp?.toDate()
                        val formattedDate = date?.let { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it) } ?: ""

                        Text(
                            text = "${tx.type.uppercase()} - Rs. ${tx.amount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        tx.fromAccount?.let {
                            Text("From: $it", fontSize = 14.sp)
                        }
                        tx.toAccount?.let {
                            Text("To: $it", fontSize = 14.sp)
                        }

                        Text(formattedDate, fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}