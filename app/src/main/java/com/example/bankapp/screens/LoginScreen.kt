package com.example.bankapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onManagerLogin: () -> Unit,
    onUserLogin: () -> Unit,
    onActivateClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val blue = Color(0xFF064BD8)
    val darkBlue = Color(0xFF001A70)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(55.dp))

        Text(text = "🏦", fontSize = 60.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Digital Banking System",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = darkBlue
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Secure • Simple • Reliable",
            fontSize = 14.sp,
            color = darkBlue
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = blue
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Login to continue",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            leadingIcon = { Text("✉️") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            leadingIcon = { Text("🔒") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Enter email and password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener {
                        val uid = auth.currentUser?.uid

                        if (uid != null) {
                            db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    loading = false
                                    val role = document.getString("role")

                                    when (role) {
                                        "manager" -> onManagerLogin()
                                        "user" -> onUserLogin()
                                        else -> Toast.makeText(context, "Role not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    loading = false
                                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        loading = false
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue),
            enabled = !loading
        ) {
            Text(
                text = if (loading) "Logging in..." else "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Activate Your Account",
            color = blue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onActivateClick() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Secure banking for your better future.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(35.dp))
    }
}