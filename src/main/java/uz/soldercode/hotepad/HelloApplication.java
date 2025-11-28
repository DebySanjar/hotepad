package uz.soldercode.hotepad;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage sahna) throws IOException {
        FXMLLoader yuklash = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene ekran = new Scene(yuklash.load(), 1000, 700);


        try {
            sahna.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/notepad.png"))));
        } catch (Exception e) {
            System.out.println("Ikonka yuklanmadi");
        }

        sahna.setTitle("Hotpad");
        sahna.setScene(ekran);
        sahna.show();
    }

}