package com.czy.miniproject.models

data class User(
    val id: String = "",
    val nim: String = "",
    val nip: String = "",
    val nama: String = "",
    val email: String = "",
    val password: String = "",
    val role: UserRole = UserRole.MAHASISWA
)

enum class UserRole {
    MAHASISWA, DOSEN, KAPROGDI
}