package com.czy.miniproject.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy.miniproject.models.MataKuliah
import com.czy.miniproject.models.TranskripNilai
import com.czy.miniproject.repository.TranskripNilaiRepository
import kotlinx.coroutines.launch

class TranskripNilaiViewModel : ViewModel() {
    private val repository = TranskripNilaiRepository()

    private val _transkripList = mutableStateOf<List<TranskripNilai>>(emptyList())
    val transkripList: State<List<TranskripNilai>> = _transkripList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    private val _ipk = mutableStateOf(0.0)
    val ipk: State<Double> = _ipk

    fun getAllTranskrip() {
        viewModelScope.launch {
            _isLoading.value = true
            _transkripList.value = repository.getAllTranskripNilai()
            _isLoading.value = false
        }
    }

    fun addTranskrip(transkrip: TranskripNilai) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addTranskripNilai(transkrip)
            if (result.isSuccess) {
                _message.value = "Transkrip berhasil ditambahkan"
                getAllTranskrip()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menambahkan transkrip"
            }
            _isLoading.value = false
        }
    }

    fun updateTranskrip(id: String, transkrip: TranskripNilai) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateTranskripNilai(id, transkrip)
            if (result.isSuccess) {
                _message.value = "Transkrip berhasil diupdate"
                getAllTranskrip()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal mengupdate transkrip"
            }
            _isLoading.value = false
        }
    }

    fun deleteTranskrip(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteTranskripNilai(id)
            if (result.isSuccess) {
                _message.value = "Transkrip berhasil dihapus"
                getAllTranskrip()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menghapus transkrip"
            }
            _isLoading.value = false
        }
    }

    fun getTranskripByMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _transkripList.value = repository.getTranskripByMahasiswa(mahasiswaId)
            _isLoading.value = false
        }
    }

    companion object {
        fun convertGradeToValue(grade: String): Double {
            return when (grade.uppercase()) {
                "A" -> 4.0
                "AB" -> 3.5
                "B" -> 3.0
                "BC" -> 2.5
                "C" -> 2.0
                "CD" -> 1.5
                "D" -> 1.0
                "E" -> 0.0
                else -> 0.0
            }
        }
    }

    // Fungsi calculateIPK yang sudah direvisi
    fun calculateIPK(mataKuliahList: List<MataKuliah>) {
        if (mataKuliahList.isEmpty() || _transkripList.value.isEmpty()) {
            _ipk.value = 0.0
            return
        }

        val sksMap = mataKuliahList.associate { it.id to it.sks }
        var totalNilaiTerbobot = 0.0
        var totalSKS = 0

        _transkripList.value.forEach { transkrip ->
            val sks = sksMap[transkrip.mataKuliahId] ?: 0
            val nilaiAngka = convertGradeToValue(transkrip.nilai)
            totalNilaiTerbobot += nilaiAngka * sks
            totalSKS += sks
        }

        _ipk.value = if (totalSKS > 0) totalNilaiTerbobot / totalSKS else 0.0
    }

    fun clearMessage() {
        _message.value = ""
    }
}
