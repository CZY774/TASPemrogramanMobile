package com.czy.miniproject.models

data class KartuStudi(
    val id: String = "",
    val mahasiswaId: String = "",
    val mataKuliahId: String = "",
    val semester: Int = 0,
    val tahunAkademik: String = "",
    val status: String = "Aktif"
)