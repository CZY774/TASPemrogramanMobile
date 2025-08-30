# Aplikasi Manajemen Perkuliahan

Aplikasi mobile untuk manajemen perkuliahan yang dibangun dengan Jetpack Compose dan Firebase. Aplikasi ini mendukung tiga peran pengguna: Mahasiswa, Dosen, dan Kaprodi dengan hak akses yang berbeda-beda.

## Fitur Berdasarkan Peran

### 1. Mahasiswa
- **Mata Kuliah**
  - Melihat daftar mata kuliah beserta jadwal

- **Kartu Studi**
  - Melihat daftar mata kuliah yang diambil
  - Melihat status keaktifan mahasiswa

- **Transkrip Nilai**
  - Melihat transkrip nilai pribadi
  - Melihat IPK dan total SKS

- **Akun**
  - Ganti password
  - Logout

### 2. Dosen
- **Mata Kuliah**
  - Melihat daftar mata kuliah beserta jadwal sesuai dengan nama dosen yang sedang login

- **Transkrip Nilai**
  - Menginput nilai mahasiswa
  - Mengupdate nilai mahasiswa
  - Menghapus nilai mahasiswa

- **Akun**
  - Ganti password
  - Logout

### 3. Kaprodi
- **Manajemen Mata Kuliah**
  - Menambah mata kuliah baru
  - Melihat daftar mata kuliah beserta jadwal
  - Mengupdate informasi mata kuliah
  - Menghapus mata kuliah

- **Manajemen Kartu Studi**
  - Melihat daftar mahasiswa
  - Menambahkan mahasiswa ke mata kuliah
  - Menghapus mahasiswa dari mata kuliah

- **Manajemen Nilai**
  - Menginput nilai mahasiswa
  - Mengupdate nilai mahasiswa
  - Menghapus nilai mahasiswa
  - Melihat IPK mahasiswa

- **Akun**
  - Ganti password
  - Logout

## Teknologi yang Digunakan

- **Bahasa Pemrograman**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Penyimpanan Data**: Firebase Firestore
- **Autentikasi**: Firebase Authentication
- **Asynchronous Programming**: Kotlin Coroutines
- **Navigasi**: Navigation Component for Compose

## Persyaratan Sistem

- Android Studio Giraffe atau yang lebih baru
- Android SDK 24 (Android 7.0) atau lebih tinggi
- Kotlin 1.8.0 atau yang lebih baru
- JDK 11 atau yang lebih baru

## Instalasi

1. Clone repository ini
   ```bash
   git clone https://github.com/CZY774/TASPemrogramanMobile.git
   ```

2. Buka project di Android Studio

3. Tambahkan file `google-services.json` ke dalam direktori `app/`
   - Dapatkan file konfigurasi dari Firebase Console
   - Ikuti petunjuk di [dokumentasi Firebase](https://firebase.google.com/docs/android/setup)

4. Sync project dengan Gradle

5. Jalankan aplikasi di emulator atau perangkat fisik

## Struktur Database

Aplikasi ini menggunakan Firebase Firestore dengan struktur [koleksi](https://github.com/CZY774/TASPemrogramanMobile/blob/master/firebase-export.json) sebagai berikut:

### 1. users
Menyimpan data pengguna (Mahasiswa, Dosen, dan Kaprodi).
- `id` (string): ID unik pengguna
- `email` (string): Email pengguna
- `nama` (string): Nama lengkap
- `nim` (string): Nomor Induk Mahasiswa (hanya untuk mahasiswa)
- `nip` (string): Nomor Induk Pegawai (hanya untuk dosen/kaprodi)
- `role` (string): Peran pengguna (MAHASISWA/DOSEN/KAPROGDI)

### 2. matakuliah
Menyimpan data mata kuliah.
- `id` (string): ID unik mata kuliah
- `kode` (string): Kode mata kuliah
- `nama` (string): Nama mata kuliah
- `sks` (number): Jumlah SKS
- `dosen` (string): Nama dosen pengampu
- `jadwal` (string): Jadwal perkuliahan
- `semester` (number): Semester saat mata kuliah diambil

### 3. kartu_studi
Mencatat mata kuliah yang diambil mahasiswa.
- `id` (string): ID unik entri
- `mahasiswaId` (string): ID mahasiswa
- `mataKuliahId` (string): ID mata kuliah
- `semester` (number): Semester pengambilan
- `tahunAkademik` (string): Tahun akademik
- `status` (string): Status pengambilan (Aktif/Tidak Aktif)

### 4. transkrip_nilai
Menyimpan nilai mahasiswa untuk setiap mata kuliah.
- `id` (string): ID unik entri
- `mahasiswaId` (string): ID mahasiswa
- `mataKuliahId` (string): ID mata kuliah
- `nilai` (string): Nilai huruf (A/B/C/D/E)
- `angka` (number): Nilai angka
- `semester` (number): Semester pengambilan
- `tahunAkademik` (string): Tahun akademik

## Struktur Project

```
app/src/main/java/com/czy/miniproject/
├── MainActivity.kt          # Entry point aplikasi
├── models/                  # Data models
│   ├── KartuStudi.kt
│   ├── MataKuliah.kt
│   ├── TranskripNilai.kt
│   └── User.kt
├── repository/              # Layer akses data
│   ├── AuthRepository.kt
│   ├── KartuStudiRepository.kt
│   ├── MataKuliahRepository.kt
│   └── TranskripRepository.kt
├── theme/                   # Tema dan styling
│   └── Theme.kt
├── uiscreens/               # UI screens
│   ├── ChangePasswordScreen.kt
│   ├── DashboardScreen.kt
│   ├── KartuStudiScreen.kt
│   ├── LoginScreen.kt
│   ├── MataKuliahScreen.kt
│   ├── RegisterScreen.kt
│   └── TranskripScreen.kt
└── viewmodels/              # ViewModels
    ├── AuthViewModel.kt
    ├── KartuStudiViewModel.kt
    ├── MataKuliahViewModel.kt
    └── TranskripViewModel.kt
```

## <img src="https://media.giphy.com/media/hvRJCLFzcasrR4ia7z/giphy.gif" width="25px" alt="waving hand"> Get In Touch
<div align="center">
  <a href="https://www.instagram.com/corneliusyoga" target="_blank"><img src="https://img.shields.io/badge/Instagram-%23E4405F.svg?&style=for-the-badge&logo=instagram&logoColor=white" alt="Instagram"></a>&nbsp;
  <a href="https://www.linkedin.com/in/cornelius-yoga-783b6a291" target="_blank"><img src="https://img.shields.io/badge/LinkedIn-%230077B5.svg?&style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn"></a>&nbsp;
  <a href="https://www.youtube.com/channel/UCj0TlW5vLO6r_Nlwc8oFBpw" target="_blank"><img src="https://img.shields.io/badge/YouTube-%23FF0000.svg?&style=for-the-badge&logo=youtube&logoColor=white" alt="YouTube"></a>&nbsp;
  <a href="https://corneliusyoga.vercel.app" target="_blank"><img src="https://img.shields.io/badge/Portfolio-%23000000.svg?&style=for-the-badge&logo=react&logoColor=white" alt="Portfolio"></a>
  <br/><br/>
  <img src="https://komarev.com/ghpvc/?username=CZY774&style=flat-square&color=0366D6" alt="Profile Views" />
  <br/>
  <sub>Made by Cornelius Ardhani Yoga Pratama</sub>
</div>
