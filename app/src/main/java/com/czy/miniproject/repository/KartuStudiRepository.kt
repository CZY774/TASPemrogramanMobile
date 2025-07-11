package com.czy.miniproject.repository

import com.czy.miniproject.models.KartuStudi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class KartuStudiRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllKartuStudi(): List<KartuStudi> {
        return try {
            val querySnapshot = firestore.collection("kartu_studi").get().await()
            querySnapshot.toObjects(KartuStudi::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addKartuStudi(kartuStudi: KartuStudi): Result<String> {
        return try {
            val docRef = firestore.collection("kartu_studi").add(kartuStudi).await()
            // Update field id di dalam dokumen
            firestore.collection("kartu_studi")
                .document(docRef.id)
                .update("id", docRef.id)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateKartuStudi(id: String, kartuStudi: KartuStudi): Result<String> {
        return try {
            firestore.collection("kartu_studi").document(id).set(kartuStudi).await()
            Result.success("Kartu studi berhasil diupdate")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteKartuStudi(id: String): Result<String> {
        return try {
            firestore.collection("kartu_studi").document(id).delete().await()
            Result.success("Kartu studi berhasil dihapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getKartuStudiByMahasiswa(mahasiswaId: String): List<KartuStudi> {
        return try {
            val querySnapshot = firestore.collection("kartu_studi")
                .whereEqualTo("mahasiswaId", mahasiswaId)
                .get().await()
            querySnapshot.toObjects(KartuStudi::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
