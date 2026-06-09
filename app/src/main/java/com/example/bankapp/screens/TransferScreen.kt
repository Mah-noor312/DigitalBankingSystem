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
fun TransferScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var senderAccount by remember { mutableStateOf("") }
    var receiverAccount by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val blue = Color(0xFF064BD8)

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                senderAccount = it.getString("accountNumber") ?: ""
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

        Text("Transfer Money", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))
        Text("From: $senderAccount", color = Color.DarkGray)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = receiverAccount,
            onValueChange = { receiverAccount = it },
            label = { Text("Receiver Account Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val transferAmount = amount.toDoubleOrNull()

                if (receiverAccount.isBlank() || transferAmount == null || transferAmount <= 0) {
                    Toast.makeText(context, "Enter valid details", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (receiverAccount == senderAccount) {
                    Toast.makeText(context, "Cannot transfer to same account", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true
                val uid = auth.currentUser?.uid ?: ""

                val senderRef = db.collection("accounts").document(senderAccount)
                val receiverRef = db.collection("accounts").document(receiverAccount)

                db.runTransaction { transaction ->
                    val senderSnap = transaction.get(senderRef)
                    val receiverSnap = transaction.get(receiverRef)

                    if (!senderSnap.exists()) {
                        throw Exception("Sender account not found")
                    }

                    if (!receiverSnap.exists()) {
                        throw Exception("Receiver account not found")
                    }

                    val senderBalance = senderSnap.getDouble("balance") ?: 0.0
                    val receiverBalance = receiverSnap.getDouble("balance") ?: 0.0

                    if (senderBalance < transferAmount) {
                        throw Exception("Insufficient balance")
                    }

                    transaction.update(senderRef, "balance", senderBalance - transferAmount)
                    transaction.update(receiverRef, "balance", receiverBalance + transferAmount)

                    val transactionRef = db.collection("transactions").document()
                    transaction.set(
                        transactionRef,
                        hashMapOf(
                            "userId" to uid,
                            "accountNumber" to senderAccount,
                            "type" to "transfer",
                            "fromAccount" to senderAccount,
                            "toAccount" to receiverAccount,
                            "amount" to transferAmount,
                            "status" to "success",
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                    )
                }.addOnSuccessListener {
                    loading = false
                    amount = ""
                    receiverAccount = ""
                    Toast.makeText(context, "Transfer successful", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    loading = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = blue)
        ) {
            Text(if (loading) "Processing..." else "Transfer")
        }
    }
}