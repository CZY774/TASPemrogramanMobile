package com.czy.miniproject.repository

import com.czy.miniproject.models.KartuStudi
import com.czy.miniproject.models.MataKuliah
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class MataKuliahRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllMataKuliah(): List<MataKuliah> {
        return try {
            val querySnapshot = firestore.collection("matakuliah").get().await()
            querySnapshot.toObjects<MataKuliah>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addMataKuliah(mataKuliah: MataKuliah): Result<String> {
        return try {
            val docRef = firestore.collection("matakuliah").add(mataKuliah).await()
            // Update field id di dalam dokumen
            firestore.collection("matakuliah")
                .document(docRef.id)
                .update("id", docRef.id)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMataKuliah(id: String, mataKuliah: MataKuliah): Result<String> {
        return try {
            firestore.collection("matakuliah").document(id).set(mataKuliah).await()
            Result.success("Mata kuliah berhasil diupdate")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMataKuliah(id: String): Result<String> {
        return try {
            firestore.collection("matakuliah").document(id).delete().await()
            Result.success("Mata kuliah berhasil dihapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Mata Kuliah by nama Dosen yang sedang login
    suspend fun getMataKuliahByDosen(dosenName: String): List<MataKuliah> {
        return try {
            val querySnapshot = FirebaseFirestore.getInstance()
                .collection("matakuliah")
                .whereEqualTo("dosen", dosenName)
                .get()
                .await()

            querySnapshot.toObjects<MataKuliah>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMataKuliahByIds(ids: List<String>): List<MataKuliah> {
        return try {
            if (ids.isEmpty()) return emptyList()

            val querySnapshot = firestore.collection("matakuliah")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()

            querySnapshot.toObjects<MataKuliah>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMataKuliahByMahasiswa(mahasiswaId: String): List<MataKuliah> {
        return try {
            // Pertama dapatkan kartu studi mahasiswa
            val kartuStudi = firestore.collection("kartu_studi")
                .whereEqualTo("mahasiswaId", mahasiswaId)
                .get()
                .await()
                .toObjects<KartuStudi>()

            // Kemudian dapatkan mata kuliah berdasarkan ID yang ada di kartu studi
            val mataKuliahIds = kartuStudi.map { it.mataKuliahId }
            if (mataKuliahIds.isEmpty()) return emptyList()

            getMataKuliahByIds(mataKuliahIds)
        } catch (e: Exception) {
            emptyList()
        }
    }
}