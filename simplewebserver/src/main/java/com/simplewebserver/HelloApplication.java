package com.simplewebserver;

import com.sun.net.httpserver.HttpServer; // Import kelas HttpServer untuk membuat server HTTP
import com.simplewebserver.req.RequestHandler; // Import kelas RequestHandler untuk menangani permintaan HTTP

import javafx.application.Application; // Import kelas Application dari JavaFX untuk memulai aplikasi JavaFX
import javafx.scene.Scene; // Import kelas Scene dari JavaFX untuk mengelola tampilan
import javafx.scene.control.Button; // Import kelas Button dari JavaFX untuk membuat tombol
import javafx.scene.control.TextArea; // Import kelas TextArea dari JavaFX untuk membuat area teks
import javafx.scene.layout.VBox; // Import kelas VBox dari JavaFX untuk mengatur node dalam kotak vertikal
import javafx.stage.Stage; // Import kelas Stage dari JavaFX untuk membuat jendela aplikasi

import javafx.geometry.Insets; // Import kelas Insets dari JavaFX untuk menambahkan ruang di sekitar tepi node
import javafx.scene.control.*; // Import semua kelas kontrol dari JavaFX
import javafx.scene.layout.GridPane; // Import kelas GridPane dari JavaFX untuk mengatur node dalam sel grid

import java.io.IOException; // Import kelas IOException untuk menangani pengecualian masukan/keluaran
import java.net.InetSocketAddress; // Import kelas InetSocketAddress untuk menentukan alamat IP dan nomor port
import java.text.SimpleDateFormat; // Import kelas SimpleDateFormat untuk memformat tanggal dan waktu
import java.util.Date; // Import kelas Date untuk merepresentasikan tanggal dan waktu
import java.util.concurrent.Executors; // Import kelas Executors untuk membuat eksekutor

import static com.simplewebserver.req.RequestHandler.*; // Mengimpor semua static method dari kelas RequestHandler

// Deklarasi untuk Main Application
public class HelloApplication extends Application {
    private RequestHandler requestHandler; // Deklarasi variabel requestHandler untuk menangani permintaan HTTP

    private TextArea logTextArea; // Deklarasi variabel logTextArea untuk menampilkan log server
    private int port; // Deklarasi variabel port untuk nomor port server
    private String webDirectory; // Deklarasi variabel webDirectory untuk direktori web server
    private String logDirectory; // Deklarasi variabel logDirectory untuk direktori log server
    private boolean serverRunning = false; // false karena belum dijalankan

    private static final String CONFIG_FILE = "config.properties"; // Deklarasi variabel konstanta CONFIG_FILE untuk nama file konfigurasi
    private HttpServer server; // Deklarasi variabel server untuk server HTTP

    // Memulai aplikasi JavaFX
    public static void main(String[] args) {
        launch(args);
    }

    // Metode yang dipanggil saat aplikasi dimulai
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SIMPLE WEB SERVER"); // Judul

        GridPane grid = new GridPane(); // Membuat objek GridPane untuk menempatkan elemen-elemen UI
        grid.setPadding(new Insets(10, 10, 10, 10)); // Mengatur jarak antara tepi grid dan elemen-elemen di dalamnya
        grid.setVgap(5); // Mengatur jarak vertikal antara baris
        grid.setHgap(5); // Mengatur jarak horizontal antara kolom
        grid.setStyle("-fx-background-color: skyblue;"); // Mengatur warna pada GridPane dengan warna skyblue

        Label portLabel = new Label("Port:"); // Membuat label "Port"
        grid.add(portLabel, 0, 0); // Menambahkan label "Port" ke grid pada kolom 0, baris 0
        TextField portField = new TextField(); // Membuat TextField untuk input port
        portField.setPrefWidth(295); // Mengatur panjang preferensi untuk TextField
        grid.add(portField, 1, 0); // Menambahkan TextField ke grid pada kolom 1, baris 0
        Button startButton = new Button("Start Server"); // Membuat tombol "Start Server"
        startButton.setPrefWidth(100); // Mengatur panjang preferensi untuk tombol
        grid.add(startButton, 2, 0); // Menambahkan tombol "Start Server" ke grid pada kolom 2, baris 0

        Label webDirLabel = new Label("Web Directory:"); // Membuat label "Web Directory"
        grid.add(webDirLabel, 0, 1); // Menambahkan label "Web Directory" ke grid pada kolom 0, baris 1
        TextField webDirField = new TextField(); // Membuat TextField untuk input direktori web
        webDirField.setPrefWidth(295); // Mengatur panjang preferensi untuk TextField
        grid.add(webDirField, 1, 1); // Menambahkan TextField ke grid pada kolom 1, baris 1
        Button stopButton = new Button("Stop Server"); // Membuat tombol "Stop Server"
        stopButton.setPrefWidth(100); // Mengatur panjang preferensi untuk tombol
        grid.add(stopButton, 2, 1); // Menambahkan tombol "Stop Server" ke grid pada kolom 2, baris 1

        Label logDirLabel = new Label("Log Directory:"); // Membuat label "Log Directory"
        grid.add(logDirLabel, 0, 2); // Menambahkan label "Log Directory" ke grid pada kolom 0, baris 2
        TextField logDirField = new TextField(); // Membuat TextField untuk input direktori log
        logDirField.setPrefWidth(295); // Mengatur panjang preferensi untuk TextField
        grid.add(logDirField, 1, 2); // Menambahkan TextField ke grid pada kolom 1, baris 2
        Button viewLogsButton = new Button("View Logs"); // Membuat tombol "View Logs"
        viewLogsButton.setPrefWidth(100); // Mengatur panjang preferensi untuk tombol
        grid.add(viewLogsButton, 2, 2); // Menambahkan tombol "View Logs" ke grid pada kolom 2, baris 2

        logTextArea = new TextArea(); // Membuat TextArea untuk menampilkan log server
        logTextArea.setEditable(false); // Mengatur agar TextArea tidak dapat diedit
        grid.add(logTextArea, 0, 3, 3, 1); // Menambahkan TextArea ke grid, mengambil 3 kolom dan 1 baris dimulai dari kolom 0, baris 3

        // Mengatur nilai portField dengan nilai terakhir port yang diinput
        portField.setText(String.valueOf(getPort()));

        webDirField.setText(getWebDirectory()); // Mengatur nilai webDirField dengan nilai terakhir direktori web yang diinput
        logDirField.setText(getLogDirectory()); // Mengatur nilai logDirField dengan nilai terakhir direktori log yang diinput

        // Menambahkan aksi pada tombol startButton
        startButton.setOnAction(e -> {
            if (!serverRunning) { // Memeriksa apakah server tidak sedang berjalan
                port = Integer.parseInt(portField.getText()); // Mendapatkan nilai port dari input dan mengkonversi ke integer
                webDirectory = webDirField.getText(); // Mendapatkan direktori web dari input
                logDirectory = logDirField.getText(); // Mendapatkan direktori log dari input
                startServer(); // Memulai server
                startButton.setDisable(true); // Menonaktifkan tombol start setelah server dimulai
                stopButton.setDisable(false); // Mengaktifkan tombol stop setelah server dimulai
                // Simpan konfigurasi saat tombol start ditekan
                saveConfig(port, webDirectory, logDirectory); // Menyimpan konfigurasi ke file
            }
        });

        // Menambahkan aksi pada tombol stopButton
        stopButton.setOnAction(e -> {
            if (serverRunning) { // Memeriksa apakah server sedang berjalan
                stopServer(); // Menghentikan server
                startButton.setDisable(false); // Mengaktifkan tombol start setelah server dihentikan
                stopButton.setDisable(true); // Menonaktifkan tombol stop setelah server dihentikan
            }
        });

        // Menambahkan aksi pada tombol viewLogsButton
        viewLogsButton.setOnAction(e -> {
            Stage logStage = new Stage(); // Membuat jendela baru untuk menampilkan log
            logStage.setTitle("Server Logs"); // Mengatur judul jendela
            TextArea logViewTextArea = new TextArea(); // Membuat TextArea untuk menampilkan log
            logViewTextArea.setEditable(false); // Mengatur agar TextArea tidak dapat diedit
            logViewTextArea.setPrefWidth(700); // Mengatur lebar preferensi untuk area teks log view
            logViewTextArea.setPrefHeight(500); // Mengatur tinggi preferensi untuk area teks log view
            logViewTextArea.setText(logTextArea.getText()); // Mengisi TextArea dengan log yang ada
            Scene logScene = new Scene(new VBox(logViewTextArea), 700, 500); // Membuat tampilan baru dengan VBox yang berisi TextArea log
            logStage.setScene(logScene); // Mengatur tampilan untuk jendela baru
            logStage.show(); // Menampilkan jendela baru
        });

        // Mengatur warna tombol
        startButton.setStyle("-fx-background-color: lightgrey; -fx-text-fill: black;"); // Mengatur gaya tombol startButton dengan latar belakang abu-abu terang dan teks berwarna hitam
        stopButton.setStyle("-fx-background-color: lightgrey; -fx-text-fill: black;"); // Mengatur gaya tombol stopButton dengan latar belakang abu-abu terang dan teks berwarna hitam
        viewLogsButton.setStyle("-fx-background-color: lightgrey; -fx-text-fill: black;"); // Mengatur gaya tombol viewLogsButton dengan latar belakang abu-abu terang dan teks berwarna hitam

        Scene scene = new Scene(grid, 500, 310); // Membuat objek Scene dengan grid sebagai root node
        primaryStage.setScene(scene); // Mengatur Scene untuk jendela utama
        primaryStage.show(); // Menampilkan jendela utama
    }

    // Metode untuk menghentikan server
    private void stopServer() {
        if (server != null) { // Memeriksa apakah server tidak null
            stop(); // Memanggil metode stop untuk menghentikan server
            serverRunning = false; // Menandai bahwa server telah berhenti
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Membuat objek SimpleDateFormat untuk format tanggal
            String dateString = dateFormat.format(new Date()); // Mendapatkan tanggal dalam format yang ditentukan
            // Menambahkan log server telah berhenti ke logTextArea dan file log
            logTextArea.appendText("[" + new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date()) + "] " + "Server stopped\n");
            requestHandler.appendToLogFile("[" + new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date()) + "] " + "Server stopped" + "\n", "server_" + dateString + ".log");
        }
    }

    // Metode untuk memulai server
    private void startServer() {
        requestHandler = new RequestHandler(webDirectory, logDirectory, logTextArea); // Menginisialisasi requestHandler
        start(); // Memanggil metode start untuk memulai server
        serverRunning = true; // Menandai bahwa server telah berjalan
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Membuat objek SimpleDateFormat untuk format tanggal
        String dateString = dateFormat.format(new Date()); // Mendapatkan tanggal dalam format yang ditentukan
        // Menambahkan log server telah dimulai ke logTextArea dan file log
        logTextArea.appendText("[" + new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date()) + "] " + "Server started on port " + port + "\n");
        requestHandler.appendToLogFile("[" + new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date()) + "] " + "Server started on port " + port + "\n", "server_" + dateString + ".log");
    }

    // Metode untuk memulai server HTTP
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0); // Membuat server HTTP dengan port yang ditentukan
            RequestHandler requestHandler = new RequestHandler(webDirectory, logDirectory, logTextArea); // Menginisialisasi requestHandler dengan parameter yang sesuai
            server.createContext("/", requestHandler); // Mengatur konteks root ("/") ke RequestHandler yang baru diinisialisasi
            server.setExecutor(Executors.newFixedThreadPool(10)); // Mengatur executor untuk menangani permintaan
            server.start(); // Memulai server
            System.out.println("Server started on port " + port); // Menampilkan pesan bahwa server telah dimulai
        } catch (IOException e) { // Menangani IOException jika gagal memulai server
            System.err.println("Error starting server: " + e.getMessage()); // Menampilkan pesan kesalahan
            e.printStackTrace(); // Melacak jejak kesalahan
        }
    }

    // Metode untuk menghentikan server
    public void stop() {
        if (server != null) { // Memeriksa apakah server tidak null
            server.stop(0); // Menghentikan server dengan delay 0 milidetik
            System.out.println("Server stopped" + "\n"); // Menampilkan pesan bahwa server telah berhenti
        }
    }
}
