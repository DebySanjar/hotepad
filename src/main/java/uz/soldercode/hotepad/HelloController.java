package uz.soldercode.hotepad;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HelloController {

    @FXML
    private TextArea matnMaydoni;
    @FXML
    private Label statusLabel;

    // Menyu elementlari
    @FXML
    private MenuItem yangiFayl, ochish, saqlash, saqlashBoshqa, chiqish;
    @FXML
    private MenuItem kesish, nusxalash, qoyish, hammasiniTanlash, topish, almashtirish;
    @FXML
    private MenuItem shriftTanlash, qatorOchirish, sanaSoat;
    @FXML
    private MenuItem yordim, haqida;

    private File joriyFayl;
    private boolean ozgartirilgan = false;

    @FXML
    public void initialize() {
        matnMaydoni.setFont(Font.font("Consolas", 14));
        matnMaydoni.setWrapText(true);

        matnMaydoni.textProperty().addListener((obs, old, newVal) -> {
            ozgartirilgan = true;
            statusniYangilash();
        });

        hotKeylarniOrnatish();
    }

    private void hotKeylarniOrnatish() {
        yangiFayl.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        ochish.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saqlash.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saqlashBoshqa.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        kesish.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        nusxalash.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        qoyish.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        hammasiniTanlash.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        topish.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        almashtirish.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        sanaSoat.setAccelerator(new KeyCodeCombination(KeyCode.F5));
    }

    // FAYL OPERATSIYALARI
    @FXML
    private void yangiFaylYaratish() {
        if (ozgartirilgan && !saqlashTasdiqlash()) return;
        matnMaydoni.clear();
        joriyFayl = null;
        ozgartirilgan = false;
        statusniYangilash();
    }

    @FXML
    private void faylOchish() {
        if (ozgartirilgan && !saqlashTasdiqlash()) return;
        ochishFayl();
    }

    @FXML
    private void faylSaqlash() {
        if (joriyFayl == null) faylSaqlashBoshqa();
        else faylgaYozish(joriyFayl);
    }

    @FXML
    private void faylSaqlashBoshqa() {
        saqlashBoshqaFayl();
    }

    @FXML
    private void chop() {
        chopEtish();
    }

    @FXML
    private void dasturniYopish() {
        if (ozgartirilgan && !saqlashTasdiqlash()) return;
        Platform.exit();
    }


    @FXML
    private void matnKesish() {
        matnMaydoni.cut();
    }

    @FXML
    private void matnNusxalash() {
        matnMaydoni.copy();
    }

    @FXML
    private void matnQoyish() {
        matnMaydoni.paste();
    }

    @FXML
    private void hammasiniTanlash() {
        matnMaydoni.selectAll();
    }

    @FXML
    private void bekorQilish() {
        matnMaydoni.undo();
    }

    @FXML
    private void qaytarish() {
        matnMaydoni.redo();
    }

    @FXML
    private void qatorOchirish() {
        joriyQatorniOchirish();
    }

    @FXML
    private void topish() {
        topishFunksiyasi();
    }

    @FXML
    private void almashtirish() {
        almashtirishFunksiyasi();
    }

    @FXML
    private void sanaSoatKiritish() {
        sanaVaSoatniKiritish();
    }


    @FXML
    private void shriftTanlash() {
        Font currentFont = matnMaydoni.getFont();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Shrift tanlash");
        dialog.initOwner(getSahna());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new javafx.geometry.Insets(20));

        // Shrift nomi
        ComboBox<String> familyBox = new ComboBox<>();
        familyBox.getItems().addAll(Font.getFontNames());
        familyBox.setValue(currentFont.getFamily());
        familyBox.setPrefWidth(220);

        // Hajm — ComboBox<String> qilib oldik (xavfsiz!)
        ComboBox<String> sizeBox = new ComboBox<>();
        for (int i = 8; i <= 72; i += 2) sizeBox.getItems().add(String.valueOf(i));
        sizeBox.setEditable(true); // yozish mumkin
        sizeBox.setValue(String.valueOf((int) currentFont.getSize()));

        // Qalin va Egri
        CheckBox boldCheck = new CheckBox("Qalin");
        CheckBox italicCheck = new CheckBox("Egri");

        String style = currentFont.getStyle().toLowerCase();
        boldCheck.setSelected(style.contains("bold"));
        italicCheck.setSelected(style.contains("italic"));

        grid.add(new Label("Shrift:"), 0, 0);
        grid.add(familyBox, 1, 0);
        grid.add(new Label("Hajmi:"), 0, 1);
        grid.add(sizeBox, 1, 1);
        grid.add(boldCheck, 0, 2);
        grid.add(italicCheck, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                String family = familyBox.getValue();
                String sizeText = sizeBox.getValue();

                double size = 14; // default
                try {
                    size = Double.parseDouble(sizeText);
                    if (size < 1) size = 14;
                    if (size > 200) size = 200;
                } catch (Exception ignored) {
                }

                FontWeight weight = boldCheck.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL;
                FontPosture posture = italicCheck.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR;

                Font newFont = Font.font(family, weight, posture, size);
                matnMaydoni.setFont(newFont);
            }
        });
    }

    @FXML
    private void yordamKorsatish() {
        malumotXabari("Yordam", "Professional NotePad - JavaFX\n\nHotkeys:\nCtrl+N, O, S, X, C, V, F, H, F5...");
    }

    @FXML
    private void haqidaMalumot() {
        malumotXabari("Dastur haqida", "Professional NotePad v1.0\n© 2025");
    }

    // Yordamchi metodlar
    private void ochishFayl() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Matn fayllari", "*.txt"), new FileChooser.ExtensionFilter("Hammasi", "*.*"));
        File f = fc.showOpenDialog(getSahna());
        if (f != null) {
            try {
                matnMaydoni.setText(Files.readString(f.toPath()));
                joriyFayl = f;
                ozgartirilgan = false;
                statusniYangilash();
            } catch (IOException e) {
                xatolikXabari("Xatolik", "Faylni ochib bo'lmadi!");
            }
        }
    }

    private void saqlashBoshqaFayl() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Matn fayllari", "*.txt"));
        if (joriyFayl != null) fc.setInitialFileName(joriyFayl.getName());
        File f = fc.showSaveDialog(getSahna());
        if (f != null) {
            faylgaYozish(f);
            joriyFayl = f;
        }
    }

    private void faylgaYozish(File f) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(matnMaydoni.getText());
            ozgartirilgan = false;
            statusniYangilash();
        } catch (IOException e) {
            xatolikXabari("Xatolik", "Saqlashda xatolik!");
        }
    }

    private void chopEtish() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(getSahna())) {
            job.printPage(matnMaydoni);
            job.endJob();
        }
    }

    private void joriyQatorniOchirish() {
        int pos = matnMaydoni.getCaretPosition();
        String text = matnMaydoni.getText();
        int start = text.lastIndexOf('\n', pos - 1) + 1;
        int end = text.indexOf('\n', pos);
        if (end == -1) end = text.length();
        matnMaydoni.deleteText(start, end);
    }

    private void topishFunksiyasi() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Topish");
        d.setHeaderText("Qidiruv");
        d.setContentText("Matn:");
        d.showAndWait().ifPresent(s -> {
            int i = matnMaydoni.getText().indexOf(s);
            if (i != -1) matnMaydoni.selectRange(i, i + s.length());
            else malumotXabari("Topilmadi", "Berilgan matn topilmadi.");
        });
    }

    private void almashtirishFunksiyasi() {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Almashtirish");
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        TextField tf1 = new TextField();
        tf1.setPromptText("Topish");
        TextField tf2 = new TextField();
        tf2.setPromptText("Almashtirish");
        g.addRow(0, new Label("Topish:"), tf1);
        g.addRow(1, new Label("Almashtirish:"), tf2);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            String yangi = matnMaydoni.getText().replace(tf1.getText(), tf2.getText());
            matnMaydoni.setText(yangi);
        });
    }

    private void sanaVaSoatniKiritish() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        matnMaydoni.insertText(matnMaydoni.getCaretPosition(), now);
    }

    private boolean saqlashTasdiqlash() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "O'zgarishlarni saqlashni xohlaysizmi?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        a.setTitle("Saqlash");
        a.setHeaderText("Faylda o'zgarishlar bor");
        return a.showAndWait().filter(b -> b == ButtonType.YES).map(b -> {
            faylSaqlash();
            return true;
        }).orElseGet(() -> a.getResult() == ButtonType.NO);
    }

    private void statusniYangilash() {
        if (statusLabel == null) return;
        String fname = joriyFayl != null ? joriyFayl.getName() : "Yangi fayl";
        String star = ozgartirilgan ? "*" : "";
        int lines = matnMaydoni.getText().split("\n").length;
        int chars = matnMaydoni.getText().length();
        statusLabel.setText(String.format("%s%s | Qatorlar: %d | Belgilar: %d", star, fname, lines, chars));
    }

    private Stage getSahna() {
        return (Stage) matnMaydoni.getScene().getWindow();
    }

    private void xatolikXabari(String title, String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void malumotXabari(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}