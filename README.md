# 🌐 Web Server Application 🌐

Selamat datang di proyek aplikasi web server! 🎉 Aplikasi ini menyediakan layanan akses konten melalui port tertentu dengan antarmuka grafis untuk konfigurasi dan tampilan log. Berikut adalah panduan untuk menggunakan aplikasi ini.

## 🚀 Fitur Utama

- **Antarmuka Grafis**: Menyediakan antarmuka untuk konfigurasi port, direktori web, dan direktori log serta tampilan log akses.
- **Konfigurasi**: 
  - **Port**: Mengatur port untuk akses.
  - **Direktori Web**: Menentukan lokasi penyimpanan file yang dapat diakses.
  - **Direktori Log**: Menentukan lokasi penyimpanan log akses.
- **Kontrol Server**:
  - **Tombol Start**: Menyalakan server.
  - **Tombol Stop**: Mematikan server.
- **Penanganan Request**: 
  - **Request GET**: Mendukung request GET.
  - **Pengaksesan Konten**: Mengirim file HTML, CSS, JS, gambar, dan file lainnya.
- **Log Akses**:
  - **Tampilan Log**: Menampilkan log akses di antarmuka grafis.
  - **Penyimpanan Log**: Menyimpan log akses di direktori dengan penamaan file sesuai tanggal.

## 🔧 Instalasi

1. **Clone Repository**:

   ```bash
   git clone https://github.com/username/web-server-app.git
   cd web-server-app
   ```
2. **Build dan Jalankan**:
   ```bash
   javac SimpleWebServer.java WebServerGUI.java
   java WebServerGUI
   ``` 

## ⚙️ Konfigurasi

1. **Port**:
   Masukkan port yang diinginkan pada kolom Port.
3. **Direktori Web**:
   Masukkan path direktori tempat file web disimpan pada kolom Web Directory.
5. **Direktori Log**
   Masukkan path direktori tempat log disimpan pada kolom Log Directory.

## 🌟 Penggunaan
1. **Menyalakan Server**:
   Klik tombol Start Server untuk memulai server.
3. **Mematikan Server**:
   Klik tombol Stop Server untuk menghentikan server.
4. **Mengakses Konten**:
   Akses konten melalui browser dengan URL seperti http://localhost:port/index.html.Akses direktori dengan URL seperti http://localhost:port/pages untuk melihat daftar file.
5. **Melihat Log**:
   Log akses akan ditampilkan di area log pada antarmuka grafis dan disimpan di direktori log dengan nama file sesuai tanggal.
