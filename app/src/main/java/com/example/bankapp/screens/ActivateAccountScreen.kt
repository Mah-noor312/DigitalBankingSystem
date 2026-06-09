package com.example.bankapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ActivateAccountScreen(
    onBackClick: () -> Unit,
    onActivationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "← Back",
                color = Color(0xFF064BD8),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Activate Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Account Number") },
            placeholder = { Text("Enter your account number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter new password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm new password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (email.isBlank() || accountNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                // Check if account exists and is not activated
                val accountRef = db.collection("accounts").document(accountNumber)
                accountRef.get().addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        loading = false
                        Toast.makeText(context, "Account not found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    val isActivated = doc.getBoolean("isActivated") ?: false
                    val accountEmail = doc.getString("userEmail") ?: ""
                    if (isActivated) {
                        loading = false
                        Toast.makeText(context, "Account already activated", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    if (accountEmail != email.trim()) {
                        loading = false
                        Toast.makeText(context, "Email does not match account", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Create Firebase Auth user
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid ?: ""
                            // Save in users collection
                            val userData = hashMapOf(
                                "email" to email.trim(),
                                "role" to "user",
                                "accountNumber" to accountNumber
                            )
                            db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Update account activated
                                    accountRef.update("isActivated", true)
                                        .addOnSuccessListener {
                                            loading = false
                                            Toast.makeText(context, "Account activated successfully!", Toast.LENGTH_SHORT).show()
                                            onActivationSuccess()
                                        }
                                        .addOnFailureListener {
                                            loading = false
                                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener {
                                    loading = false
                                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            loading = false
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    loading = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064BD8))
        ) {
            Text(if (loading) "Activating..." else "Activate Account")
        }
    }
}