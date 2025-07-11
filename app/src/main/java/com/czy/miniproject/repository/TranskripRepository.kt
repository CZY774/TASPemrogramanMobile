package com.czy.miniproject.repository

import com.czy.miniproject.models.TranskripNilai
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TranskripNilaiRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllTranskripNilai(): List<TranskripNilai> {
        return try {
            val querySnapshot = firestore.collection("transkrip_nilai").get().await()
            querySnapshot.toObjects(TranskripNilai::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTranskripNilai(transkrip: TranskripNilai): Result<String> {
        return try {
            val docRef = firestore.collection("transkrip_nilai").add(transkrip).await()
            // Update field id di dalam dokumen
            firestore.collection("transkrip_nilai")
                .document(docRef.id)
                .update("id", docRef.id)

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTranskripNilai(id: String, transkrip: TranskripNilai): Result<String> {
        return try {
            firestore.collection("transkrip_nilai").document(id).set(transkrip).await()
            Result.success("Transkrip nilai berhasil diupdate")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTranskripNilai(id: String): Result<String> {
        return try {
            firestore.collection("transkrip_nilai").document(id).delete().await()
            Result.success("Transkrip nilai berhasil dihapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTranskripByMahasiswa(mahasiswaId: String): List<TranskripNilai> {
        return try {
            val querySnapshot = firestore.collection("transkrip_nilai")
                .whereEqualTo("mahasiswaId", mahasiswaId)
                .get().await()
            querySnapshot.toObjects(TranskripNilai::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
