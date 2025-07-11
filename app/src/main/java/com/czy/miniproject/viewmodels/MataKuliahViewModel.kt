package com.czy.miniproject.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy.miniproject.models.MataKuliah
import com.czy.miniproject.repository.MataKuliahRepository
import kotlinx.coroutines.launch

class MataKuliahViewModel : ViewModel() {
    private val repository = MataKuliahRepository()

    private val _mataKuliahList = mutableStateOf<List<MataKuliah>>(emptyList())
    val mataKuliahList: State<List<MataKuliah>> = _mataKuliahList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    fun getAllMataKuliah() {
        viewModelScope.launch {
            _isLoading.value = true
            _mataKuliahList.value = repository.getAllMataKuliah()
            _isLoading.value = false
        }
    }

    fun addMataKuliah(mataKuliah: MataKuliah) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addMataKuliah(mataKuliah)
            if (result.isSuccess) {
                _message.value = "Mata kuliah berhasil ditambahkan"
                getAllMataKuliah()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menambahkan mata kuliah"
            }
            _isLoading.value = false
        }
    }

    fun updateMataKuliah(id: String, mataKuliah: MataKuliah) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateMataKuliah(id, mataKuliah)
            if (result.isSuccess) {
                _message.value = "Mata kuliah berhasil diupdate"
                getAllMataKuliah()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal mengupdate mata kuliah"
            }
            _isLoading.value = false
        }
    }

    fun deleteMataKuliah(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteMataKuliah(id)
            if (result.isSuccess) {
                _message.value = "Mata kuliah berhasil dihapus"
                getAllMataKuliah()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Gagal menghapus mata kuliah"
            }
            _isLoading.value = false
        }
    }

    fun getMataKuliahByIds(ids: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _mataKuliahList.value = repository.getMataKuliahByIds(ids)
            _isLoading.value = false
        }
    }

    fun getMataKuliahByMahasiswa(mahasiswaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _mataKuliahList.value = repository.getMataKuliahByMahasiswa(mahasiswaId)
            _isLoading.value = false
        }
    }

    // Get Mata Kuliah by nama Dosen yang sedang login
    fun getMataKuliahByDosen(dosenName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _mataKuliahList.value = repository.getMataKuliahByDosen(dosenName)
            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _message.value = ""
    }
}