package com.czy.miniproject.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy.miniproject.models.User
import com.czy.miniproject.repository.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.login(identifier, password)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _message.value = "Login berhasil"
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Login gagal"
            }
            _isLoading.value = false
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.register(user)
                if (result.isSuccess) {
                    _message.value = "Registrasi berhasil"
                } else {
                    _message.value = result.exceptionOrNull()?.message ?: "Registrasi gagal"
                    // Log error untuk debugging
                    println("Register error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Terjadi kesalahan saat registrasi"
                println("Register exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Di AuthViewModel.kt
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    _message.value = "Tidak ada pengguna yang login"
                    return@launch
                }

                if (newPassword.length < 6) {
                    _message.value = "Password baru harus minimal 6 karakter"
                    return@launch
                }

                val credential = EmailAuthProvider
                    .getCredential(user.email ?: "", oldPassword)

                try {
                    // Re-authenticate
                    user.reauthenticate(credential).await()

                    // Update password
                    user.updatePassword(newPassword).await()
                    _message.value = "Password berhasil diubah"
                } catch (e: Exception) {
                    when (e.message) {
                        "The password is invalid or the user does not have a password." -> 
                            _message.value = "Password lama salah"
                        "We have blocked all requests from this device due to unusual activity. Try again later." -> 
                            _message.value = "Terlalu banyak percobaan. Tunggu beberapa saat lalu coba lagi."
                        else -> 
                            _message.value = "Gagal mengubah password: ${e.message ?: "Terjadi kesalahan"}"
                    }
                }
            } catch (e: Exception) {
                _message.value = "Terjadi kesalahan: ${e.message ?: "Tidak diketahui"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _currentUser.value = null
        _message.value = "Logout berhasil"
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun setMessage(message: String) {
        _message.value = message
    }
}