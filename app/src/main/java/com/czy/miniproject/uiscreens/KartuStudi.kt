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
import com.czy.miniproject.models.KartuStudi
import com.czy.miniproject.models.User
import com.czy.miniproject.models.UserRole
import com.czy.miniproject.viewmodels.KartuStudiViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun KartuStudiScreen(
    user: User?,
    onNavigateBack: () -> Unit
) {
    val viewModel: KartuStudiViewModel = viewModel()
    val kartuStudiList by viewModel.kartuStudiList
    val isLoading by viewModel.isLoading
    val message by viewModel.message

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedKartuStudi by remember { mutableStateOf<KartuStudi?>(null) }

    LaunchedEffect(Unit) {
        when (user?.role) {
            UserRole.MAHASISWA -> {
                // Mahasiswa hanya bisa melihat kartu studinya sendiri
                viewModel.getKartuStudiByMahasiswa(user.id)
            }
            UserRole.KAPROGDI -> {
                // Kaprogdi bisa melihat semua kartu studi
                viewModel.getAllKartuStudi()
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
                    text = "Kartu Studi",
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
                        items(kartuStudiList) { kartuStudi ->
                            KartuStudiCard(
                                kartuStudi = kartuStudi,
                                canEdit = user?.role == UserRole.KAPROGDI,
                                onEdit = { selectedKartuStudi = kartuStudi },
                                onDelete = { viewModel.deleteKartuStudi(kartuStudi.id) }
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
    if (showAddDialog || selectedKartuStudi != null) {
        KartuStudiDialog(
            kartuStudi = selectedKartuStudi,
            onDismiss = {
                showAddDialog = false
                selectedKartuStudi = null
            },
            onSave = { kartuStudi ->
                if (selectedKartuStudi != null) {
                    viewModel.updateKartuStudi(selectedKartuStudi!!.id, kartuStudi)
                } else {
                    viewModel.addKartuStudi(kartuStudi)
                }
                showAddDialog = false
                selectedKartuStudi = null
            }
        )
    }
}

@Composable
fun KartuStudiCard(
    kartuStudi: KartuStudi,
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
                        text = "Mahasiswa ID: ${kartuStudi.mahasiswaId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mata Kuliah ID: ${kartuStudi.mataKuliahId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = "Semester: ${kartuStudi.semester} | Tahun: ${kartuStudi.tahunAkademik}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Status: ${kartuStudi.status}",
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
fun KartuStudiDialog(
    kartuStudi: KartuStudi?,
    onDismiss: () -> Unit,
    onSave: (KartuStudi) -> Unit
) {
    var mahasiswaId by remember { mutableStateOf(kartuStudi?.mahasiswaId ?: "") }
    var mataKuliahId by remember { mutableStateOf(kartuStudi?.mataKuliahId ?: "") }
    var semester by remember { mutableStateOf(kartuStudi?.semester?.toString() ?: "") }
    var tahunAkademik by remember { mutableStateOf(kartuStudi?.tahunAkademik ?: "") }
    var status by remember { mutableStateOf(kartuStudi?.status ?: "Aktif") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (kartuStudi == null) "Tambah Kartu Studi" else "Edit Kartu Studi",
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
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (mahasiswaId.isNotEmpty() && mataKuliahId.isNotEmpty() &&
                        semester.isNotEmpty() && tahunAkademik.isNotEmpty() && status.isNotEmpty()) {

                        val newKartuStudi = KartuStudi(
                            id = kartuStudi?.id ?: "",
                            mahasiswaId = mahasiswaId,
                            mataKuliahId = mataKuliahId,
                            semester = semester.toIntOrNull() ?: 0,
                            tahunAkademik = tahunAkademik,
                            status = status
                        )
                        onSave(newKartuStudi)
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