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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.czy.miniproject.models.MataKuliah
import com.czy.miniproject.models.TranskripNilai
import com.czy.miniproject.models.User
import com.czy.miniproject.models.UserRole
import com.czy.miniproject.viewmodels.KartuStudiViewModel
import com.czy.miniproject.viewmodels.MataKuliahViewModel
import com.czy.miniproject.viewmodels.TranskripNilaiViewModel

@Composable
fun DashboardScreen(
    user: User?,
    onLogout: () -> Unit,
    navController: NavController
) {
    val mataKuliahViewModel: MataKuliahViewModel = viewModel()
    val kartuStudiViewModel: KartuStudiViewModel = viewModel()
    val transkripViewModel: TranskripNilaiViewModel = viewModel()

    var totalSKS by remember { mutableStateOf(0) }
    var ipk by remember { mutableStateOf("0.00") }

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

    // Fungsi untuk menghitung IPK dalam format yang benar dengan mempertimbangkan bobot SKS
    fun calculateIPK(transkripList: List<TranskripNilai>, mataKuliahList: List<MataKuliah>): String {
        if (transkripList.isEmpty() || mataKuliahList.isEmpty()) return "0.00"

        val sksMap = mataKuliahList.associate { it.id to it.sks }
        var totalNilaiTerbobot = 0.0
        var totalSKS = 0

        transkripList.forEach { transkrip ->
            val sks = sksMap[transkrip.mataKuliahId] ?: 0
            val nilaiAngka = convertGradeToValue(transkrip.nilai) // Ganti ini
            totalNilaiTerbobot += nilaiAngka * sks
            totalSKS += sks
        }

        return if (totalSKS > 0) "%.2f".format(totalNilaiTerbobot / totalSKS) else "0.00"
    }

    LaunchedEffect(user) {
        if (user != null) {
            when (user.role) {
                UserRole.MAHASISWA -> {
                    kartuStudiViewModel.getKartuStudiByMahasiswa(user.id)
                    transkripViewModel.getTranskripByMahasiswa(user.id)
                }
                UserRole.DOSEN -> {
                    mataKuliahViewModel.getMataKuliahByDosen(user.nama)
                }
                UserRole.KAPROGDI -> {
                    mataKuliahViewModel.getAllMataKuliah()
                    kartuStudiViewModel.getAllKartuStudi()
                    transkripViewModel.getAllTranskrip()
                }
            }
        }
    }

    // Perhitungan SKS dan IPK untuk mahasiswa
    LaunchedEffect(kartuStudiViewModel.kartuStudiList.value, transkripViewModel.transkripList.value, mataKuliahViewModel.mataKuliahList.value) {
        if (user?.role == UserRole.MAHASISWA) {
            // Hitung total SKS dari mata kuliah yang diambil
            val mataKuliahIds = kartuStudiViewModel.kartuStudiList.value.map { it.mataKuliahId }
            if (mataKuliahIds.isNotEmpty()) {
                mataKuliahViewModel.getMataKuliahByIds(mataKuliahIds)
            }

            // Hitung IPK dengan mempertimbangkan bobot SKS
            ipk = calculateIPK(
                transkripViewModel.transkripList.value,
                mataKuliahViewModel.mataKuliahList.value
            )
        }
    }

    // Update total SKS ketika data mata kuliah berubah
    LaunchedEffect(mataKuliahViewModel.mataKuliahList.value) {
        if (user?.role == UserRole.MAHASISWA) {
            totalSKS = mataKuliahViewModel.mataKuliahList.value.sumOf { it.sks }
        }
    }

    user?.let { currentUser ->
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selamat Datang!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = currentUser.nama,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF667eea)
                            )
                            Text(
                                text = currentUser.role.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .background(
                                    Color(0xFFFF5722),
                                    CircleShape
                                )
                                .size(48.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Card (for Mahasiswa)
                if (currentUser.role == UserRole.MAHASISWA) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Total SKS",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "$totalSKS",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF667eea)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "IPK",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = ipk,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF667eea)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Menu Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mata Kuliah
//                    if (currentUser.role != UserRole.MAHASISWA) {
                        item {
                            MenuCard(
                                title = "Mata Kuliah",
                                icon = Icons.Default.Book,
                                color = Color(0xFF4CAF50),
                                onClick = { navController.navigate("mata_kuliah") }
                            )
                        }
//                    }

                    // Kartu Studi
                    if (currentUser.role != UserRole.DOSEN) {
                        item {
                            MenuCard(
                                title = "Kartu Studi",
                                icon = Icons.AutoMirrored.Filled.Assignment,
                                color = Color(0xFF2196F3),
                                onClick = { navController.navigate("kartu_studi") }
                            )
                        }
                    }

                    // Transkrip Nilai
//                    if (currentUser.role == UserRole.KAPROGDI || currentUser.role == UserRole.DOSEN) {
                        item {
                            MenuCard(
                                title = "Transkrip Nilai",
                                icon = Icons.Default.Description,
                                color = Color(0xFF9C27B0),
                                onClick = { navController.navigate("transkrip") }
                            )
                        }
//                    }

                    // Ganti Password
                    item {
                        MenuCard(
                            title = "Ganti Password",
                            icon = Icons.Default.Security,
                            color = Color(0xFFFF9800),
                            onClick = { navController.navigate("change_password") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Informasi Akun",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(
                            label = if (currentUser.role == UserRole.MAHASISWA) "NIM" else "NIP",
                            value = if (currentUser.role == UserRole.MAHASISWA) currentUser.nim else currentUser.nip
                        )

                        InfoRow(
                            label = "Email",
                            value = currentUser.email
                        )

                        InfoRow(
                            label = "Role",
                            value = currentUser.role.name
                        )
                    }
                }
            }
        }
    }
}