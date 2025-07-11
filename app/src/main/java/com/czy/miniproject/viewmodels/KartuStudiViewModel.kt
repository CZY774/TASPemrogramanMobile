package com.czy.miniproject.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy.miniproject.models.KartuStudi
import com.czy.miniproject.models.MataKuliah
import com.czy.miniproject.repository.KartuStudiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KartuStudiViewModel : ViewModel() {
    private val repository = KartuStudiRepository()

    private val _kartuStudiList = mutableStateOf<List<KartuStudi>>(emptyList())
    val kartuStudiList: State<List<KartuStudi>> = _kartuStudiList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    private val _totalSKS = mutableStateOf(0)
    val totalSKS: State<Int> = _totalSKS

    fun getAllKartuStudi() {
        viewModelScope.launch {
            _isLoading.value = true
            _kartuStudiList.value = repository.getAllKartuStudi()
            _isLoading.value = false
        }
    }

    fun addKartuStudi(kartuStudi: KartuStudi) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addKartuStudi(kartuStudi)
            if (result.isSuccess) {
                _message.value = "Kartu studi berhasil ditambahkan"
                getAllKartuStudi()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menambahkan kartu studi"
            }
            _isLoading.value = false
        }
    }

    fun updateKartuStudi(id: String, kartuStudi: KartuStudi) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateKartuStudi(id, kartuStudi)
            if (result.isSuccess) {
                _message.value = "Kartu studi berhasil diupdate"
                getAllKartuStudi()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal mengupdate kartu studi"
            }
            _isLoading.value = false
        }
    }

    fun deleteKartuStudi(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteKartuStudi(id)
            if (result.isSuccess) {
                _message.value = "Kartu studi berhasil dihapus"
                getAllKartuStudi()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menghapus kartu studi"
            }
            _isLoading.value = false
        }
    }

    fun getKartuStudiByMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _kartuStudiList.value = repository.getKartuStudiByMahasiswa(mahasiswaId)
            _isLoading.value = false
        }
    }

    fun calculateTotalSKS(mataKuliahList: List<MataKuliah>) {
        _totalSKS.value = mataKuliahList.sumOf { it.sks }
    }

    fun clearMessage() {
        _message.value = ""
    }
}
