module uz.soldercode.hotepad {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    requires org.controlsfx.controls;

    opens uz.soldercode.hotepad to javafx.fxml;
    exports uz.soldercode.hotepad;
}