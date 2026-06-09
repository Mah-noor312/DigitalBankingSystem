package com.example.bankapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DepositScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var amount by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val blue = Color(0xFF064BD8)

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                accountNumber = it.getString("accountNumber") ?: ""
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
            color = blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Deposit Money",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))
        Text("Account: $accountNumber", color = Color.DarkGray)

        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val depositAmount = amount.toDoubleOrNull()

                if (depositAmount == null || depositAmount <= 0) {
                    Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (accountNumber.isBlank()) {
                    Toast.makeText(context, "Account not found", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true
                val uid = auth.currentUser?.uid ?: ""

                val accountRef = db.collection("accounts").document(accountNumber)

                db.runTransaction { transaction ->
                    val snapshot = transaction.get(accountRef)
                    val currentBalance = snapshot.getDouble("balance") ?: 0.0
                    val newBalance = currentBalance + depositAmount

                    transaction.update(accountRef, "balance", newBalance)

                    val transactionRef = db.collection("transactions").document()
                    val transactionData = hashMapOf(
                        "userId" to uid,
                        "accountNumber" to accountNumber,
                        "type" to "deposit",
                        "amount" to depositAmount,
                        "status" to "success",
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    transaction.set(transactionRef, transactionData)
                }.addOnSuccessListener {
                    loading = false
                    amount = ""
                    Toast.makeText(context, "Amount deposited successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    loading = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue),
            enabled = !loading
        ) {
            Text(if (loading) "Processing..." else "Deposit")
        }
    }
}