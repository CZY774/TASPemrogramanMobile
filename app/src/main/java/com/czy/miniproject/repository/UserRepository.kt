package com.czy.miniproject.repository

import com.czy.miniproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(user: User): Result<String> {
        return try {
            // Validasi input
            if (user.nama.isEmpty()) {
                throw Exception("Nama harus diisi")
            }
            
            if (user.email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                throw Exception("Email tidak valid")
            }
            
            if (user.password.isEmpty() || user.password.length < 6) {
                throw Exception("Password harus minimal 6 karakter")
            }
            
            // Validasi NIM
            if (!user.nim.startsWith("67") || user.nim.length != 9 || !user.nim.all { it.isDigit() }) {
                throw Exception("NIM harus dimulai dengan 67, terdiri dari 9 angka")
            }
            
            // Validasi NIP
            if (!user.nip.startsWith("67") || user.nip.length != 5 || !user.nip.all { it.isDigit() }) {
                throw Exception("NIP harus dimulai dengan 67, terdiri dari 5 angka")
            }
            
            // Cek apakah email sudah terdaftar
            val emailQuery = firestore.collection("users").whereEqualTo("email", user.email).get().await()
            if (!emailQuery.isEmpty) {
                throw Exception("Email sudah terdaftar")
            }
            
            // Cek apakah NIM sudah terdaftar
            val nimQuery = firestore.collection("users").whereEqualTo("nim", user.nim).get().await()
            if (!nimQuery.isEmpty) {
                throw Exception("NIM sudah terdaftar")
            }
            
            // Cek apakah NIP sudah terdaftar
            val nipQuery = firestore.collection("users").whereEqualTo("nip", user.nip).get().await()
            if (!nipQuery.isEmpty) {
                throw Exception("NIP sudah terdaftar")
            }

            // Buat user di Firebase Auth
            val result = auth.createUserWithEmailAndPassword(user.email, user.password).await()
            val userId = result.user?.uid ?: throw Exception("Gagal membuat akun")

            // Simpan data user ke Firestore
            val userData = user.copy(
                id = userId,
                password = "" // Jangan simpan password
            )

            firestore.collection("users").document(userId).set(userData).await()
            Result.success("Registrasi berhasil")
            
        } catch (e: Exception) {
            when (e.message) {
                "The email address is already in use by another account." -> 
                    Result.failure(Exception("Email sudah digunakan"))
                "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                    Result.failure(Exception("Koneksi jaringan bermasalah"))
                else -> Result.failure(e)
            }
        }
    }

    suspend fun login(identifier: String, password: String): Result<User> {
        return try {
            val email = if (identifier.contains("@")) {
                identifier
            } else {
                val userQuery = if (identifier.startsWith("67") && identifier.length == 9) {
                    firestore.collection("users").whereEqualTo("nim", identifier)
                } else {
                    firestore.collection("users").whereEqualTo("nip", identifier)
                }

                val querySnapshot = userQuery.get().await()
                if (querySnapshot.isEmpty) {
                    throw Exception("User tidak ditemukan")
                }

                querySnapshot.documents.first().getString("email") ?: throw Exception("Email tidak ditemukan")
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID tidak ditemukan")

            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject<User>() ?: throw Exception("Data user tidak ditemukan")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): User? {
        return auth.currentUser?.let { firebaseUser ->
            // Implementasi untuk mendapatkan user dari Firestore
            null
        }
    }
}