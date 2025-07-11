package com.czy.miniproject.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.czy.miniproject.models.MataKuliah
import com.czy.miniproject.models.User
import com.czy.miniproject.models.UserRole
import com.czy.miniproject.viewmodels.MataKuliahViewModel
import kotlinx.coroutines.delay

@Composable
fun MataKuliahScreen(
    user: User?,
    onNavigateBack: () -> Unit
) {
    val viewModel: MataKuliahViewModel = viewModel()
    val mataKuliahList by viewModel.mataKuliahList
    val isLoading by viewModel.isLoading
    val message by viewModel.message

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMataKuliah by remember { mutableStateOf<MataKuliah?>(null) }

    LaunchedEffect(Unit) {
        when (user?.role) {
            UserRole.MAHASISWA -> {
                // Mahasiswa hanya bisa melihat matakuliah yang sudah diambil
                viewModel.getMataKuliahByMahasiswa(user.id)
            }
            UserRole.DOSEN -> {
                // Dosen bisa melihat semua matakuliah yang diajarnya
                viewModel.getMataKuliahByDosen(user.nama)
            }
            UserRole.KAPROGDI -> {
                // Kaprogdi bisa melihat semua matakuliah
                viewModel.getAllMataKuliah()
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
                    text = "Mata Kuliah",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                if (user?.role == UserRole.KAPROGDI) {
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
                        items(mataKuliahList) { mataKuliah ->
                            MataKuliahCard(
                                mataKuliah = mataKuliah,
                                canEdit = user?.role == UserRole.KAPROGDI,
                                onEdit = { selectedMataKuliah = mataKuliah },
                                onDelete = { viewModel.deleteMataKuliah(mataKuliah.id) }
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
    if (showAddDialog || selectedMataKuliah != null) {
        MataKuliahDialog(
            mataKuliah = selectedMataKuliah,
            onDismiss = {
                showAddDialog = false
                selectedMataKuliah = null
            },
            onSave = { mataKuliah ->
                if (selectedMataKuliah != null) {
                    viewModel.updateMataKuliah(selectedMataKuliah!!.id, mataKuliah)
                } else {
                    viewModel.addMataKuliah(mataKuliah)
                }
                showAddDialog = false
                selectedMataKuliah = null
            }
        )
    }
}

@Composable
fun MataKuliahCard(
    mataKuliah: MataKuliah,
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
                        text = mataKuliah.kode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = mataKuliah.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "SKS: ${mataKuliah.sks} | Semester: ${mataKuliah.semester}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Jadwal: ${mataKuliah.jadwal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Dosen: ${mataKuliah.dosen}",
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
fun MataKuliahDialog(
    mataKuliah: MataKuliah?,
    onDismiss: () -> Unit,
    onSave: (MataKuliah) -> Unit
) {
    var kode by remember { mutableStateOf(mataKuliah?.kode ?: "") }
    var nama by remember { mutableStateOf(mataKuliah?.nama ?: "") }
    var sks by remember { mutableStateOf(mataKuliah?.sks?.toString() ?: "") }
    var semester by remember { mutableStateOf(mataKuliah?.semester?.toString() ?: "") }
    var jadwal by remember { mutableStateOf(mataKuliah?.jadwal ?: "") }
    var dosen by remember { mutableStateOf(mataKuliah?.dosen ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (mataKuliah == null) "Tambah Mata Kuliah" else "Edit Mata Kuliah",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = kode,
                    onValueChange = { kode = it },
                    label = { Text("Kode") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Mata Kuliah") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sks,
                    onValueChange = { sks = it },
                    label = { Text("SKS") },
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
                    value = jadwal,
                    onValueChange = { jadwal = it },
                    label = { Text("Jadwal") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dosen,
                    onValueChange = { dosen = it },
                    label = { Text("Dosen") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (kode.isNotEmpty() && nama.isNotEmpty() && sks.isNotEmpty() &&
                        semester.isNotEmpty() && jadwal.isNotEmpty() && dosen.isNotEmpty()) {

                        val newMataKuliah = MataKuliah(
                            id = mataKuliah?.id ?: "",
                            kode = kode,
                            nama = nama,
                            sks = sks.toIntOrNull() ?: 0,
                            semester = semester.toIntOrNull() ?: 0,
                            jadwal = jadwal,
                            dosen = dosen
                        )
                        onSave(newMataKuliah)
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