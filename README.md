# Digital Banking System Report

## Project Title

Digital Banking System using Kotlin, Jetpack Compose and Firebase

## Project Goal

The goal of this project is to build a simple digital banking system where two types of users can use the app:

1. Manager
2. User

The manager creates bank accounts for users, and users activate their assigned accounts to perform banking transactions.

## Technologies Used

* Kotlin
* Jetpack Compose
* Firebase Authentication
* Firebase Firestore
* Android Studio

## User Roles

### Manager

The manager can:

* Login to the app
* Create bank accounts for users
* View all created accounts
* Logout from the system

### User

The user can:

* Activate assigned account
* Login to the app
* View account dashboard
* View current balance
* Deposit money
* Withdraw money
* Transfer money
* View transaction history

## Complete Workflow

### 1. Manager Workflow

First, the manager logs in using email and password. After successful login, the system checks the manager role from Firestore and opens the Manager Dashboard.

From the dashboard, the manager can create a new bank account by entering the user email. The system automatically generates an account number. The account is saved in Firestore with balance 0, status ACTIVE, and isActivated set to false.

The manager can also view all created accounts on the All Accounts screen.

### Manager Flow

```text
Manager Login
↓
Role Check from Firestore
↓
Manager Dashboard
↓
Create Account / View All Accounts
↓
Account created with:
- User Email
- Account Number
- Balance = 0
- Status = ACTIVE
- isActivated = false
```

## 2. User Activation Workflow

After the manager creates an account, the user activates the assigned account. The user enters email, account number, new password and confirm password.

The system checks whether the account exists in Firestore and whether it is not already activated. If the email and account number match, Firebase Authentication creates the user account. Then Firestore saves the user role as user and updates the account isActivated field to true.

### User Activation Flow

```text
Activate Account
↓
Enter Email
↓
Enter Account Number
↓
Enter Password
↓
System verifies account
↓
Firebase Auth creates user
↓
Firestore saves user role
↓
Account becomes activated
```

## 3. User Login Workflow

After activation, the user can login using email and password. The system checks the role from Firestore. If the role is user, the app opens the User Dashboard.

### User Login Flow

```text
User Login
↓
Firebase Authentication
↓
Firestore Role Check
↓
User Dashboard
```

## 4. User Dashboard

The User Dashboard shows:

* User email
* Account number
* Current balance
* Deposit button
* Withdraw button
* Transfer button
* Transaction history button
* Logout button

## 5. Deposit Workflow

When the user deposits money, the system adds the entered amount to the current balance. A transaction record is also saved in the transactions collection.

### Deposit Flow

```text
Enter Amount
↓
Balance increases
↓
Transaction record saved
```

## 6. Withdraw Workflow

When the user withdraws money, the system first checks the current balance. If the balance is enough, the amount is deducted. If the balance is not enough, the system shows an insufficient balance message.

### Withdraw Flow

```text
Enter Amount
↓
Check Balance
↓
If enough balance:
    Deduct amount
    Save transaction
Else:
    Show insufficient balance
```

## 7. Transfer Workflow

For transfer, the user enters receiver account number and amount. The system checks if the receiver account exists and if the sender has enough balance.

Firestore transaction is used so that money is deducted from sender and added to receiver safely. If any step fails, the whole transfer fails.

### Transfer Flow

```text
Enter Receiver Account Number
↓
Enter Amount
↓
Check Receiver Account
↓
Check Sender Balance
↓
Deduct from Sender
↓
Add to Receiver
↓
Save Transaction Record
```

## 8. Transaction History Workflow

Every deposit, withdraw and transfer creates a transaction record in Firestore. The Transaction History screen fetches the current user’s transactions and displays them in a list.

### History Flow

```text
Open Transaction History
↓
Fetch transactions by current userId
↓
Show transaction type, amount and date
```

## Firestore Database Structure

### users Collection

```text
users
 └── userId
      email
      role
      accountNumber
```

### accounts Collection

```text
accounts
 └── accountNumber
      accountNumber
      userEmail
      balance
      status
      isActivated
      createdAt
```

### transactions Collection

```text
transactions
 └── transactionId
      userId
      accountNumber
      type
      amount
      status
      timestamp
      fromAccount
      toAccount
```

## Banking Rules Implemented

### Balance Rules

* Balance can never go below 0.
* Withdraw is allowed only when the user has enough balance.

### Transaction Rules

* Every financial action creates a transaction record.
* Transactions are stored in Firestore.
* Transactions are not deleted.

### Transfer Rules

* Amount is deducted from sender account.
* Amount is added to receiver account.
* Firestore transaction is used.
* If any step fails, the transfer fails.

## Screens in the App

1. Login Screen
2. Activate Account Screen
3. Manager Dashboard Screen
4. Create Account Screen
5. All Accounts Screen
6. User Dashboard Screen
7. Deposit Screen
8. Withdraw Screen
9. Transfer Screen
10. Transaction History Screen

## Conclusion

This project successfully implements a simple digital banking system using Kotlin, Jetpack Compose and Firebase. The manager can create and view accounts, while users can activate their accounts, login, view balance and perform banking transactions. The system follows important banking rules such as preventing negative balance, recording every transaction and performing safe money transfers.
