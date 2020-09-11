package org.example;

import com.neurotec.tutorials.util.LibraryManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {


    private static Scene scene;


    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("helloword"), 1000, 700);
        stage.setTitle("Simple Faces Sample");
        Image icon = new Image(getClass().getResourceAsStream("/images/Logo.png"));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        LibraryManager.initLibraryPath();
        HelloWordController controller = new HelloWordController();
        controller.obtainLicense();
        launch();
    }

//    public void obtainLicenses(BasePanel panel) {
//        try {
//            if (!panel.isObtained()) {
//                boolean status = FaceTools.getInstance().obtainLicenses(panel.getRequiredLicenses());
//                FaceTools.getInstance().obtainLicenses(panel.getOptionalLicenses());
//                panel.getLicensing().setRequiredComponents(panel.getRequiredLicenses());
//                panel.getLicensing().setOptionalComponents(panel.getOptionalLicenses());
//                panel.updateLicensing(status);
//            }
//        } catch (Exception e) {
//            showError(null, e);
//        }
//    }

}