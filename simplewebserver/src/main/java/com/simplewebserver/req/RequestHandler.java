package com.simplewebserver.req;

import java.io.*; // Mengimpor semua kelas yang terkait dengan operasi input/output
import java.net.HttpURLConnection; // Mengimpor kelas untuk koneksi HTTP
import java.net.InetAddress; // Mengimpor kelas untuk representasi alamat IP
import java.text.SimpleDateFormat; // Mengimpor kelas SimpleDateFormat untuk pemformatan tanggal
import java.util.Date; // Mengimpor kelas untuk representasi tanggal dan waktu
import java.util.Properties; // Mengimpor kelas untuk bekerja dengan file konfigurasi
import java.io.File; // Mengimpor kelas untuk bekerja dengan file
import java.io.FileInputStream; // Mengimpor kelas FileInputStream untuk membaca file
import java.io.IOException; // Mengimpor kelas IOException untuk penanganan kesalahan input/output
import java.io.OutputStream; // Mengimpor kelas OutputStream untuk menulis data keluar
import java.nio.file.Files; // Mengimpor kelas untuk operasi file
import java.nio.file.Path; // Mengimpor kelas untuk merepresentasikan lokasi file
import java.nio.file.Paths; // Mengimpor kelas untuk membuat objek Path

import javafx.application.Platform; // Mengimpor kelas untuk menjalankan tugas di utas JavaFX
import javafx.scene.control.TextArea; // Mengimpor kelas TextArea dari JavaFX untuk menampilkan teks

import com.sun.net.httpserver.HttpExchange; // Mengimpor kelas HttpExchange untuk mewakili pertukaran HTTP
import com.sun.net.httpserver.HttpHandler; // Mengimpor kelas HttpHandler untuk menangani permintaan HTTP

// Deklarasi kelas RequestHandler yang mengimplementasikan HttpHandler
public class RequestHandler implements HttpHandler {

    private String webDirectory; // Deklarasi variabel untuk direktori web
    private String logDirectory; // Deklarasi variabel untuk direktori log
    private static final String CONFIG_FILE = "D:\\PBO EarlyBird\\simplewebserver\\config.properties"; // Deklarasi konstanta untuk lokasi file konfigurasi
    public TextArea logTextArea; // Deklarasi variabel untuk TextArea yang digunakan untuk menampilkan log

    // Konstruktor untuk kelas RequestHandler
    public RequestHandler(String webDirectory, String logDirectory, TextArea logTextArea) {
        this.webDirectory = webDirectory; // Menginisialisasi direktori web
        this.logDirectory = logDirectory; // Menginisialisasi direktori log
        this.logTextArea = logTextArea; // Menginisialisasi TextArea untuk log
    }

    @Override
    // Implementasi metode handle dari antarmuka HttpHandler
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath(); // Mendapatkan jalur permintaan dari URI (Uniform Resource Identifier) permintaan

        // inetAddress => kelas dari paket java.net yang digunakan untuk mewakili alamat IP baik IPv4 maupun IPv6
        InetAddress clientAddress = exchange.getRemoteAddress().getAddress(); // Mendapatkan alamat IP klien
        String clientIpAddress = clientAddress.getHostAddress(); // Mendapatkan alamat IP klien

        logAccess(clientIpAddress, requestPath); // Memanggil metode logAccess untuk mencatat akses

        Path requestedFile = Paths.get(webDirectory, requestPath.substring(1)); // Mendapatkan Path file yang diminta
        if (Files.isDirectory(requestedFile)) { // Memeriksa apakah permintaan adalah sebuah direktori
            handleFolderRequest(exchange, requestedFile); // Memanggil metode handleFolderRequest untuk menangani permintaan direktori
        } else {
            handleFileRequest(exchange, requestedFile); // Memanggil metode handleFileRequest untuk menangani permintaan file
        }

        String requestLog = exchange.getRequestMethod() + " " + requestPath + " " + exchange.getProtocol(); // Membuat log permintaan
        log(requestLog); // Memanggil metode log untuk mencatat log
        Platform.runLater(() -> { // Menjalankan tugas di JavaFX
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); // Membuat objek SimpleDateFormat untuk format tanggal
            String timestamp = dateFormat.format(new Date()); // Mendapatkan timestamp
            if (!logTextArea.getText().contains(requestLog)) { // Memeriksa apakah log sudah ditampilkan sebelumnya
                logTextArea.appendText("[" + timestamp + "] Request From " + "/" + clientIpAddress + " "  + requestLog + "\n"); // Menambahkan log ke TextArea
            }
        });
    }

    // Metode untuk menangani permintaan file
    private void handleFolderRequest(HttpExchange exchange, Path folderPath) throws IOException {
        File[] files = folderPath.toFile().listFiles(); // Mendapatkan daftar file dalam folder
        StringBuilder response = new StringBuilder(); // Membuat StringBuilder untuk membangun respon
        response.append("<html><head><style>"); // Memulai bagian <head> dari HTML dan mendefinisikan gaya
        response.append("body { background-color: skyblue; }"); // Gaya untuk latar belakang body
        response.append("table { width: 80%; margin: auto; border-collapse: collapse; background-color: #f2f2f2; }"); // Gaya untuk tabel
        response.append("th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }"); // Gaya untuk sel header dan sel data
        response.append("th { background-color: #cccccc; }"); // Gaya untuk latar belakang sel header
        response.append("</style></head><body>"); // Menutup tag style dan memulai body

        response.append("<h1>Index of ").append(folderPath.toString()).append("</h1>"); // Memulai respon HTML
        response.append("<table>");

        // Menambahkan header tabel
        response.append("<tr><th>Name</th><th>Type</th><th>Size (Bytes)</th></tr>");

        if (files != null) {
            long totalSize = 0; // Inisialisasi total ukuran direktori

            for (File file : files) { // Loop melalui setiap file dalam folder
                String link = file.isDirectory() ? file.getName() + "/" : file.getName(); // Mendapatkan link untuk setiap file
                response.append("<tr>"); // Memulai baris baru
                response.append("<td><a href=\"").append(link).append("\">").append(file.getName()).append("</a></td>"); // Menambahkan kolom nama dengan tautan

                // Menentukan jenis file berdasarkan ekstensi
                String fileType;
                if (file.isDirectory()) {
                    fileType = "Directory"; // Jika file adalah direktori
                } else {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); // Mendapatkan ekstensi file
                    switch (fileExtension) {
                        case "pdf":
                            fileType = "PDF";
                            break;
                        case "css":
                            fileType = "CSS";
                            break;
                        case "js":
                            fileType = "JavaScript";
                            break;
                        case "jpg":
                        case "jpeg":
                        case "png":
                        case "gif":
                            fileType = "Image";
                            break;
                        default:
                            fileType = "Unknown"; // Jika ekstensi file tidak dikenali
                            break;
                    }
                }

                // Menambahkan kolom tipe
                response.append("<td>").append(fileType).append("</td>");

                // Menambahkan kolom ukuran
                long fileSize = file.isDirectory() ? getDirectorySize(file.toPath()) : file.length(); // Mendapatkan ukuran file atau direktori
                response.append("<td>").append(fileSize).append("</td>"); // Menambahkan ukuran file/direktori dalam tag <td>

                response.append("</tr>"); // Menutup baris

                totalSize += fileSize; // Menambahkan ukuran file/direktori ke totalSize
            }

            // Menambahkan baris untuk total ukuran direktori
            response.append("<tr>"); // Memulai baris baru dalam tabel HTML
            response.append("<td><strong>Total Size:</strong></td>"); // Kolom pertama untuk teks "Total Size"
            response.append("<td></td>"); // Kolom tipe untuk total size kosong
            response.append("<td><strong>").append(totalSize).append("</strong></td>"); // Kolom ukuran dengan total size
            response.append("</tr>"); // Menutup baris dalam tabel HTML
        }

        response.append("</table>"); // Menutup tabel
        response.append("</body></html>"); // Menutup respon HTML

        sendResponse(exchange, HttpURLConnection.HTTP_OK, response.toString()); // Mengirim respon ke klien
    }

    // Metode untuk menghitung ukuran total dari sebuah direktori (rekursif)
    private long getDirectorySize(Path dirPath) {
        try {
            return Files.walk(dirPath) // Melakukan traversal rekursif dari direktori
                    .filter(Files::isRegularFile) // Hanya mempertimbangkan file biasa (bukan direktori atau symlink)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p); // Mengambil ukuran file dalam bytes
                        } catch (IOException e) {
                            e.printStackTrace();
                            return 0L; // Mengembalikan 0 jika terjadi kesalahan IO saat mengambil ukuran file
                        }
                    })
                    .sum(); // Menjumlahkan ukuran file-file yang ditemukan
        } catch (IOException e) {
            e.printStackTrace();
            return 0L; // Mengembalikan 0 jika terjadi kesalahan IO saat traversing direktori
        }
    }

    // Metode untuk menangani permintaan file
    private void handleFileRequest(HttpExchange exchange, Path filePath) throws IOException {
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) { // Memeriksa apakah file ada dan bukan direktori
            byte[] fileBytes = Files.readAllBytes(filePath); // Membaca semua byte dari file
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, fileBytes.length); // Mengirim status OK ke klien
            OutputStream outputStream = exchange.getResponseBody(); // Mendapatkan aliran keluaran respons
            outputStream.write(fileBytes); // Menulis byte file ke aliran keluaran
            outputStream.close(); // Menutup aliran keluaran
        } else { // Jika file tidak ditemukan
            String response = "Not Found"; // Respon not found
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.getBytes().length); // Mengirim status not found ke klien
            OutputStream outputStream = exchange.getResponseBody(); // Mendapatkan aliran keluaran respons
            outputStream.write(response.getBytes()); // Menulis respon not found ke aliran keluaran
            outputStream.close(); // Menutup aliran keluaran
        }
    }

    // Metode untuk mencatat akses
    public void logAccess(String ipAddress, String requestPath) {
        String logFilePath = generateLogFilePath(); // Mendapatkan lokasi file log
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Objek SimpleDateFormat untuk format tanggal
        String dateString = dateFormat.format(new Date()); // Mendapatkan tanggal dalam format yang diinginkan
        String logEntry = "[" + new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date()) + "] Request From " + "/" + ipAddress + " GET " + requestPath + " HTTP/1.1" + "\n"; // Membuat entri log
        appendToLogFile(logEntry, "server_" + dateString + ".log"); // Menambahkan entri log ke file log
        try {
            File logFile = new File(logFilePath); // Membuat objek File untuk file log
            if (!logFile.exists()) { // Memeriksa apakah file log sudah ada
                logFile.createNewFile(); // Jika belum, buat file log baru
            }
            Files.write(Paths.get(logFilePath), logEntry.getBytes(), java.nio.file.StandardOpenOption.APPEND); // Menulis entri log ke file log
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
        }
    }

    // Metode untuk menghasilkan lokasi file log
    public String generateLogFilePath() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Objek SimpleDateFormat untuk format tanggal
        String dateString = dateFormat.format(new Date()); // Mendapatkan tanggal dalam format yang diinginkan
        return Paths.get(logDirectory, "server_" + dateString + ".log").toString(); // Mengembalikan lokasi file log
    }

    // Metode untuk mengirim respons ke klien
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length); // Mengirim status kode dan panjang respons
        OutputStream outputStream = exchange.getResponseBody(); // Mendapatkan aliran keluaran respons
        outputStream.write(response.getBytes()); // Menulis respons ke aliran keluaran
        outputStream.close(); // Menutup aliran keluaran
    }

   // Metode untuk mencatat log
    private void log(String message) {

    }


    // Metode untuk menambahkan entri log ke file log
    public void appendToLogFile(String logMessage, String logFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(logDirectory, logFileName).toString(), true))) { // Membuka file log untuk menambahkan entri
            writer.append(logMessage); // Menambahkan entri log ke file log
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
        }
    }

    // Metode untuk menyimpan konfigurasi server ke dalam file properti
    public static void saveConfig(int port, String webDirectory, String logDirectory) {
        try {
            Properties prop = new Properties(); // Membuat objek Properties untuk menyimpan konfigurasi
            prop.setProperty("port", String.valueOf(port)); // Menetapkan port ke properti
            prop.setProperty("webDirectory", webDirectory); // Menetapkan direktori web ke properti
            prop.setProperty("logDirectory", logDirectory); // Menetapkan direktori log ke properti
            prop.store(new FileOutputStream(CONFIG_FILE), null); // Menyimpan properti ke file konfigurasi
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
        }
    }

    /// Metode untuk mendapatkan nilai port dari file konfigurasi
    public static int getPort() {
        try {
            Properties prop = new Properties(); // Membuat objek Properties untuk membaca konfigurasi
            prop.load(new FileInputStream(CONFIG_FILE)); // Memuat konfigurasi dari file
            return Integer.parseInt(prop.getProperty("port")); // Mendapatkan nilai port dari properti
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
            return 8080; // Default port
        }
    }

    // Metode untuk mendapatkan lokasi direktori web dari file konfigurasi
    public static String getWebDirectory() {
        try {
            Properties prop = new Properties(); // Membuat objek Properties untuk membaca konfigurasi
            prop.load(new FileInputStream(CONFIG_FILE)); // Memuat konfigurasi dari file
            return prop.getProperty("webDirectory"); // Mendapatkan lokasi direktori web dari properti
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
            return ""; // Default web directory
        }
    }

    // Mendapatkan letak log server dari file konfigurasi config.properties
    public static String getLogDirectory() {
        try {
            Properties prop = new Properties(); // Membuat objek Properties untuk membaca konfigurasi
            prop.load(new FileInputStream(CONFIG_FILE)); // Memuat konfigurasi dari file
            return prop.getProperty("logDirectory"); // Mendapatkan lokasi direktori log dari properti
        } catch (IOException e) { // Menangani kesalahan IOException
            e.printStackTrace(); // Mencetak jejak kesalahan
            return ""; // Default log directory
        }
    }


}
