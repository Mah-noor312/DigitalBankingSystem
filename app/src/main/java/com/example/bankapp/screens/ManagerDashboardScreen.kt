package com.example.bankapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManagerDashboardScreen(
    onCreateAccountClick: () -> Unit,
    onViewAccountsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val blue = Color(0xFF064BD8)

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
                text = "Manager Dashboard",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Welcome Manager 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Manage all bank accounts",
                color = Color.DarkGray
            )

            Spacer(Modifier.height(28.dp))

            DashboardCard(
                icon = "👤➕",
                title = "Create Account",
                subtitle = "Create new bank account for users",
                bgColor = Color(0xFFEAF2FF),
                onClick = onCreateAccountClick
            )

            Spacer(Modifier.height(16.dp))

            DashboardCard(
                icon = "📋",
                title = "View All Accounts",
                subtitle = "View all created bank accounts",
                bgColor = Color(0xFFEAF8EF),
                onClick = onViewAccountsClick
            )

            Spacer(Modifier.height(16.dp))

            DashboardCard(
                icon = "🚪",
                title = "Logout",
                subtitle = "Sign out from your account",
                bgColor = Color(0xFFFFEAEA),
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun DashboardCard(
    icon: String,
    title: String,
    subtitle: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 34.sp)

            Spacer(Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }

            Text(text = "›", fontSize = 34.sp)
        }
    }
}