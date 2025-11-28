package uz.soldercode.hotepad;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {

    @FXML
    private TextArea matnMaydoni;
    @FXML
    private Label statusLabel;
    @FXML
    private HBox statusBar;
    @FXML
    private CheckMenuItem statusBarMenu;
    @FXML
    private CheckMenuItem wordWrapMenu;

    @FXML
    private MenuItem yangiFayl, ochish, saqlash, saqlashBoshqa, chiqish, pageSettings;
    @FXML
    private MenuItem kesish, nusxalash, qoyish, hammasiniTanlash, topish, almashtirish;
    @FXML
    private MenuItem shriftTanlash, qatorOchirish, sanaSoat;
    @FXML
    private MenuItem yordim, haqida;

    private File joriyFayl;
    private boolean ozgartirilgan = false;

    private String currentFontFamily = "Consolas";
    private double currentFontSize = 14;
    private String currentFontStyle = "Regular";

    private double pageMarginTop = 15.0;
    private double pageMarginBottom = 15.0;
    private double pageMarginLeft = 15.0;
    private double pageMarginRight = 15.0;
    private String pageHeader = "";
    private PageOrientation pageOrientation = PageOrientation.PORTRAIT;
    private Paper pagePaper = Paper.A4;

    @FXML
    public void initialize() {
        matnMaydoni.setFont(Font.font(currentFontFamily, currentFontSize));
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
    private void sahifaSozlamalari() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sahifa sozlamalari");
        dialog.initOwner(getSahna());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField headerField = new TextField(pageHeader);
        headerField.setPromptText("Sarlavha (masalan: &f - fayl nomi)");
        headerField.setPrefWidth(300);

        Spinner<Double> topMargin = new Spinner<>(0, 100, pageMarginTop, 1);
        topMargin.setEditable(true);
        topMargin.setPrefWidth(100);

        Spinner<Double> bottomMargin = new Spinner<>(0, 100, pageMarginBottom, 1);
        bottomMargin.setEditable(true);
        bottomMargin.setPrefWidth(100);

        Spinner<Double> leftMargin = new Spinner<>(0, 100, pageMarginLeft, 1);
        leftMargin.setEditable(true);
        leftMargin.setPrefWidth(100);

        Spinner<Double> rightMargin = new Spinner<>(0, 100, pageMarginRight, 1);
        rightMargin.setEditable(true);
        rightMargin.setPrefWidth(100);

        ComboBox<String> orientationBox = new ComboBox<>();
        orientationBox.getItems().addAll("Portrait", "Landscape");
        orientationBox.setValue(pageOrientation == PageOrientation.PORTRAIT ? "Portrait" : "Landscape");

        ComboBox<String> paperBox = new ComboBox<>();
        paperBox.getItems().addAll("A4", "Letter", "Legal");
        paperBox.setValue("A4");

        int row = 0;
        grid.add(new Label("Sarlavha:"), 0, row);
        grid.add(headerField, 1, row++);

        grid.add(new Label("Yuqori margin (mm):"), 0, row);
        grid.add(topMargin, 1, row++);

        grid.add(new Label("Pastki margin (mm):"), 0, row);
        grid.add(bottomMargin, 1, row++);

        grid.add(new Label("Chap margin (mm):"), 0, row);
        grid.add(leftMargin, 1, row++);

        grid.add(new Label("O'ng margin (mm):"), 0, row);
        grid.add(rightMargin, 1, row++);

        grid.add(new Label("Yo'nalish:"), 0, row);
        grid.add(orientationBox, 1, row++);

        grid.add(new Label("Qog'oz formati:"), 0, row);
        grid.add(paperBox, 1, row++);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                pageHeader = headerField.getText();
                pageMarginTop = topMargin.getValue();
                pageMarginBottom = bottomMargin.getValue();
                pageMarginLeft = leftMargin.getValue();
                pageMarginRight = rightMargin.getValue();
                pageOrientation = orientationBox.getValue().equals("Portrait") ?
                        PageOrientation.PORTRAIT : PageOrientation.LANDSCAPE;

                switch (paperBox.getValue()) {
                    case "Letter":
                        pagePaper = Paper.LEGAL;
                        break;
                    case "Legal":
                        pagePaper = Paper.LEGAL;
                        break;
                    default:
                        pagePaper = Paper.A4;
                }
            }
        });
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
    private void statusBarToggle() {
        statusBar.setVisible(statusBarMenu.isSelected());
        statusBar.setManaged(statusBarMenu.isSelected());
    }

    @FXML
    private void wordWrapToggle() {
        matnMaydoni.setWrapText(wordWrapMenu.isSelected());
    }

    @FXML
    private void shriftTanlash() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Font");
        dialog.initOwner(getSahna());

        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        HBox topBox = new HBox(15);

        VBox fontBox = new VBox(5);
        Label fontLabel = new Label("Font:");
        ListView<String> fontList = new ListView<>();
        fontList.setPrefHeight(150);
        fontList.setPrefWidth(200);

        List<String> allFonts = Font.getFontNames();
        fontList.getItems().addAll(allFonts);
        fontList.getSelectionModel().select(currentFontFamily);
        fontList.scrollTo(currentFontFamily);

        TextField fontField = new TextField(currentFontFamily);
        fontField.setPrefWidth(200);
        fontBox.getChildren().addAll(fontLabel, fontField, fontList);

        VBox styleBox = new VBox(5);
        Label styleLabel = new Label("Font Style:");
        ListView<String> styleList = new ListView<>();
        styleList.setPrefHeight(150);
        styleList.setPrefWidth(140);
        styleList.getItems().addAll("Regular", "Italic", "Bold", "Bold Italic");
        styleList.getSelectionModel().select(currentFontStyle);

        TextField styleField = new TextField(currentFontStyle);
        styleField.setPrefWidth(140);
        styleBox.getChildren().addAll(styleLabel, styleField, styleList);

        VBox sizeBox = new VBox(5);
        Label sizeLabel = new Label("Size:");
        ListView<String> sizeList = new ListView<>();
        sizeList.setPrefHeight(150);
        sizeList.setPrefWidth(80);

        for (int i = 8; i <= 72; i += 2) {
            sizeList.getItems().add(String.valueOf(i));
        }
        sizeList.getSelectionModel().select(String.valueOf((int) currentFontSize));

        TextField sizeField = new TextField(String.valueOf((int) currentFontSize));
        sizeField.setPrefWidth(80);
        sizeBox.getChildren().addAll(sizeLabel, sizeField, sizeList);

        topBox.getChildren().addAll(fontBox, styleBox, sizeBox);

        Label sampleLabel = new Label("Sample");
        TextArea sampleArea = new TextArea("AaBbYyZz");
        sampleArea.setPrefHeight(80);
        sampleArea.setEditable(false);
        sampleArea.setStyle("-fx-control-inner-background: white;");

        VBox sampleBox = new VBox(5, sampleLabel, sampleArea);

        Label scriptLabel = new Label("Script:");
        ComboBox<String> scriptBox = new ComboBox<>();
        scriptBox.getItems().addAll("Western", "Cyrillic", "Arabic", "Hebrew");
        scriptBox.setValue("Western");
        scriptBox.setPrefWidth(300);

        mainBox.getChildren().addAll(topBox, sampleBox, scriptLabel, scriptBox);

        Runnable updateSample = () -> {
            String family = fontField.getText();
            String style = styleField.getText();
            double size = 14;
            try {
                size = Double.parseDouble(sizeField.getText());
            } catch (Exception ignored) {
            }

            Font font = createFontFromStyle(family, style, size);
            sampleArea.setFont(font);
        };

        fontList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                fontField.setText(newVal);
                updateSample.run();
            }
        });

        styleList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                styleField.setText(newVal);
                updateSample.run();
            }
        });

        sizeList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                sizeField.setText(newVal);
                updateSample.run();
            }
        });

        fontField.textProperty().addListener((obs, old, newVal) -> updateSample.run());
        styleField.textProperty().addListener((obs, old, newVal) -> updateSample.run());
        sizeField.textProperty().addListener((obs, old, newVal) -> updateSample.run());

        updateSample.run();

        dialog.getDialogPane().setContent(mainBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                currentFontFamily = fontField.getText();
                currentFontStyle = styleField.getText();
                try {
                    currentFontSize = Double.parseDouble(sizeField.getText());
                    if (currentFontSize < 1) currentFontSize = 8;
                    if (currentFontSize > 200) currentFontSize = 72;
                } catch (Exception ignored) {
                    currentFontSize = 14;
                }

                Font newFont = createFontFromStyle(currentFontFamily, currentFontStyle, currentFontSize);
                matnMaydoni.setFont(newFont);
            }
        });
    }

    private Font createFontFromStyle(String family, String style, double size) {
        String cssStyle = "";

        switch (style) {
            case "Italic":
                cssStyle = "-fx-font-style: italic;";
                break;
            case "Bold":
                cssStyle = "-fx-font-weight: bold;";
                break;
            case "Bold Italic":
                cssStyle = "-fx-font-weight: bold; -fx-font-style: italic;";
                break;
            default:
                cssStyle = "-fx-font-weight: normal; -fx-font-style: normal;";
        }

        matnMaydoni.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1fpx; %s",
                family, size, cssStyle));

        return Font.font(family, size);
    }

    @FXML
    private void yordamKorsatish() {
        malumotXabari("Yordam", "Professional NotePad - JavaFX\n\nHotkeys:\nCtrl+N, O, S, X, C, V, F, H, F5...");
    }

    @FXML
    private void haqidaMalumot() {
        malumotXabari("Dastur haqida", "Professional NotePad v1.0\n© 2025");
    }

    private void ochishFayl() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Matn fayllari", "*.txt"),
                new FileChooser.ExtensionFilter("Hammasi", "*.*")
        );
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
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "O'zgarishlarni saqlashni xohlaysizmi?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
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