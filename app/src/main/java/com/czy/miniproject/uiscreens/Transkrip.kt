package com.czy.miniproject.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy.miniproject.models.TranskripNilai
import com.czy.miniproject.models.User
import com.czy.miniproject.models.UserRole
import com.czy.miniproject.viewmodels.TranskripNilaiViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun TranskripScreen(
    user: User?,
    onNavigateBack: () -> Unit
) {
    val viewModel: TranskripNilaiViewModel = viewModel()
    val transkripList by viewModel.transkripList
    val isLoading by viewModel.isLoading
    val message by viewModel.message

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTranskrip by remember { mutableStateOf<TranskripNilai?>(null) }

    LaunchedEffect(Unit) {
        when (user?.role) {
            UserRole.MAHASISWA -> {
                // Mahasiswa hanya bisa melihat transkripnya sendiri
                viewModel.getTranskripByMahasiswa(user.id)
            }
            UserRole.DOSEN -> {
                // Dosen bisa memasukkan nilai (tidak perlu load semua data)
            }
            UserRole.KAPROGDI -> {
                // Kaprogdi bisa melihat semua transkrip
                viewModel.getAllTranskrip()
            }
            else -> {}
        }
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            delay(2000)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF667eea)
                    )
                }

                Text(
                    text = "Transkrip Nilai",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                if (user?.role == UserRole.KAPROGDI || user?.role == UserRole.DOSEN) {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF667eea)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF667eea)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transkripList) { transkrip ->
                            TranskripCard(
                                transkrip = transkrip,
                                canEdit = user?.role == UserRole.KAPROGDI || user?.role == UserRole.DOSEN,
                                onEdit = { selectedTranskrip = transkrip },
                                onDelete = {
                                    if (user?.role == UserRole.KAPROGDI) {
                                        viewModel.deleteTranskrip(transkrip.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Message Display
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("berhasil")) Color(0xFF4CAF50) else Color(0xFFFF5722)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || selectedTranskrip != null) {
        TranskripDialog(
            transkrip = selectedTranskrip,
            onDismiss = {
                showAddDialog = false
                selectedTranskrip = null
            },
            onSave = { transkrip ->
                if (selectedTranskrip != null) {
                    viewModel.updateTranskrip(selectedTranskrip!!.id, transkrip)
                } else {
                    viewModel.addTranskrip(transkrip)
                }
                showAddDialog = false
                selectedTranskrip = null
            }
        )
    }
}

@Composable
fun TranskripCard(
    transkrip: TranskripNilai,
    canEdit: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Mahasiswa ID: ${transkrip.mahasiswaId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mata Kuliah ID: ${transkrip.mataKuliahId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = "Nilai: ${transkrip.nilai} (${transkrip.angka})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Semester: ${transkrip.semester} | Tahun: ${transkrip.tahunAkademik}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (canEdit) {
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TranskripDialog(
    transkrip: TranskripNilai?,
    onDismiss: () -> Unit,
    onSave: (TranskripNilai) -> Unit
) {
    var mahasiswaId by remember { mutableStateOf(transkrip?.mahasiswaId ?: "") }
    var mataKuliahId by remember { mutableStateOf(transkrip?.mataKuliahId ?: "") }
    var nilai by remember { mutableStateOf(transkrip?.nilai ?: "") }
    var angka by remember { mutableStateOf(transkrip?.angka?.toString() ?: "") }
    var semester by remember { mutableStateOf(transkrip?.semester?.toString() ?: "") }
    var tahunAkademik by remember { mutableStateOf(transkrip?.tahunAkademik ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (transkrip == null) "Tambah Transkrip Nilai" else "Edit Transkrip Nilai",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = mahasiswaId,
                    onValueChange = { mahasiswaId = it },
                    label = { Text("Mahasiswa ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mataKuliahId,
                    onValueChange = { mataKuliahId = it },
                    label = { Text("Mata Kuliah ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nilai,
                    onValueChange = { nilai = it },
                    label = { Text("Nilai Huruf") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = angka,
                    onValueChange = { angka = it },
                    label = { Text("Nilai Angka") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = tahunAkademik,
                    onValueChange = { tahunAkademik = it },
                    label = { Text("Tahun Akademik") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (mahasiswaId.isNotEmpty() && mataKuliahId.isNotEmpty() &&
                        nilai.isNotEmpty() && angka.isNotEmpty() &&
                        semester.isNotEmpty() && tahunAkademik.isNotEmpty()) {

                        val newTranskrip = TranskripNilai(
                            id = transkrip?.id ?: "",
                            mahasiswaId = mahasiswaId,
                            mataKuliahId = mataKuliahId,
                            nilai = nilai,
                            angka = angka.toDoubleOrNull() ?: 0.0,
                            semester = semester.toIntOrNull() ?: 0,
                            tahunAkademik = tahunAkademik
                        )
                        onSave(newTranskrip)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}