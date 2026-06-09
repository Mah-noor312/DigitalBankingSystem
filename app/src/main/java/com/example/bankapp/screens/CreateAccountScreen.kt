package com.example.bankapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }

    val blue = Color(0xFF064BD8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "← Back",
                color = blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Create New Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("User Email") },
            placeholder = { Text("Enter user email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Enter user email", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                val accountNumber = "ACC" + (100000..999999).random()

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val createdAt = sdf.format(Date())

                val accountData = hashMapOf(
                    "accountNumber" to accountNumber,
                    "userEmail" to email.trim(),
                    "balance" to 0.0,
                    "status" to "ACTIVE",
                    "isActivated" to false,
                    "createdAt" to createdAt
                )

                db.collection("accounts")
                    .document(accountNumber)
                    .set(accountData)
                    .addOnSuccessListener {
                        loading = false
                        successMsg = """
Account Created Successfully!

Email: ${email.trim()}
Account Number: $accountNumber

Give this account number to the user for activation.
""".trimIndent()
                        email = ""
                    }
                    .addOnFailureListener {
                        loading = false
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = blue)
        ) {
            Text(if (loading) "Creating..." else "Create Account")
        }

        if (successMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8EF))
            ) {
                Text(
                    text = successMsg,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}