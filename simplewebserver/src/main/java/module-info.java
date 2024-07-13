// Mendefinisikan modul-modul yang dibentuk oleh proyek Java modular
module com.simplewebserver {
    requires javafx.controls; // Membutuhkan modul JavaFX Controls
    requires javafx.fxml; // Membutuhkan modul JavaFX FXML
    requires javafx.web; // Membutuhkan modul JavaFX Web

    requires org.controlsfx.controls; // Membutuhkan library ControlsFX
    requires com.dlsc.formsfx; // Membutuhkan library FormsFX
    requires net.synedra.validatorfx; // Membutuhkan library ValidatorFX
    requires org.kordamp.ikonli.javafx; // Membutuhkan library Ikonli JavaFX
    requires org.kordamp.bootstrapfx.core; // Membutuhkan library BootstrapFX
    requires eu.hansolo.tilesfx; // Membutuhkan library TilesFX
    requires com.almasb.fxgl.all; // Membutuhkan library FXGL
    requires jdk.httpserver; // Membutuhkan modul JDK HttpServer

    opens com.simplewebserver to javafx.fxml; // Membuka paket com.simplewebserver untuk JavaFX FXML
    exports com.simplewebserver; // Mengekspor paket com.simplewebserver
}
