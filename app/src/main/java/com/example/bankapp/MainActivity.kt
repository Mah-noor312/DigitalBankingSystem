package com.example.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.bankapp.screens.*
import com.example.bankapp.ui.theme.BankappTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BankappTheme {
                var currentScreen by remember { mutableStateOf("login") }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onManagerLogin = { currentScreen = "manager" },
                        onUserLogin = { currentScreen = "user" },
                        onActivateClick = { currentScreen = "activate" }
                    )

                    "activate" -> ActivateAccountScreen(
                        onBackClick = { currentScreen = "login" },
                        onActivationSuccess = { currentScreen = "login" }
                    )

                    "manager" -> ManagerDashboardScreen(
                        onCreateAccountClick = { currentScreen = "createAccount" },
                        onViewAccountsClick = { currentScreen = "allAccounts" },
                        onLogoutClick = {
                            FirebaseAuth.getInstance().signOut()
                            currentScreen = "login"
                        }
                    )

                    "createAccount" -> CreateAccountScreen(
                        onBackClick = { currentScreen = "manager" }
                    )

                    "allAccounts" -> AllAccountsScreen(
                        onBackClick = { currentScreen = "manager" }
                    )

                    "user" -> UserDashboardScreen(
                        onDepositClick = { currentScreen = "deposit" },
                        onWithdrawClick = { currentScreen = "withdraw" },
                        onTransferClick = { currentScreen = "transfer" },
                        onHistoryClick = { currentScreen = "history" },
                        onLogoutClick = { currentScreen = "login" }
                    )

                    "deposit" -> DepositScreen(
                        onBackClick = { currentScreen = "user" }
                    )
                    "withdraw" -> WithdrawScreen(
                        onBackClick = { currentScreen = "user" }
                    )
                    "transfer" -> TransferScreen(
                        onBackClick = { currentScreen = "user" }
                    )
                    "history" -> TransactionHistoryScreen(
                        onBackClick = { currentScreen = "user" }
                    )

                }
            }
        }
    }
}