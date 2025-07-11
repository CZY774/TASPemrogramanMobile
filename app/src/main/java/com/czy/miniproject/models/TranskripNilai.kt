package com.czy.miniproject.models

data class TranskripNilai(
    val id: String = "",
    val mahasiswaId: String = "",
    val mataKuliahId: String = "",
    val nilai: String = "",
    val angka: Double = 0.0,
    val semester: Int = 0,
    val tahunAkademik: String = ""
)