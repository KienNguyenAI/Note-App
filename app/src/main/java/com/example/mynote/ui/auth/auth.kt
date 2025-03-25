package com.example.mynote.ui.auth


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

val auth = FirebaseAuth.getInstance()

fun createAccount(email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Đăng ký thành công!")
            } else {

                onResult(false, task.exception?.message ?: "Lỗi đăng ký")
            }
        }
}


fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                onResult(true, "Đăng nhập thành công!")
            } else {

                onResult(false, task.exception?.message ?: "Lỗi đăng nhập")
            }
        }
}


fun getCurrentUser(): FirebaseUser? {
    return auth.currentUser
}

fun logoutUser() {
    auth.signOut()
}