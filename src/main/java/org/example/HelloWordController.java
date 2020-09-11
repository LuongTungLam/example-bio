package org.example;

import com.neurotec.biometrics.*;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.biometrics.swing.NFaceView;
import com.neurotec.images.NImage;
import com.neurotec.licensing.NLicense;
import com.neurotec.tutorials.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.EnumSet;

public class HelloWordController {

    @FXML
    private Button btnOpen;

    @FXML
    private StackPane pane;

    @FXML
    private StackPane paneEnroll;

    @FXML
    private Pane paneHeader;

    @FXML
    private Button btnOpenEnroll;

    @FXML
    private Label lblObitan;


    private static final String license = "FaceClient";

    NBiometricClient biometricClient = null;
    NFaceView faceView = null;
    NSubject subject = null;
    NFace face = null;
    NImage image = null;
    NBiometricTask task = null;

    public void obtainLicense() {


        try {
            // Obtain license
            if (!NLicense.obtain("192.168.100.15", 6000, license)) {
                System.err.format("Could not obtain license: %s%n", license);
                System.exit(-1);
            } else {
                Label label = new Label("" + license);
                lblObitan = new Label();
                lblObitan.setText(String.valueOf(label));
            }
        } catch (Throwable th) {
            Utils.handleError(th);
        }
    }

    public void chooserFIle(ActionEvent event) throws MalformedURLException, FileNotFoundException {
        reset();
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = fc.showOpenDialog(null);


        if (file != null) {
//            File[] files = file.listFiles();
//            String[] str = Arrays.stream(files).map(File::getAbsolutePath).toArray(String[]::new);
            try {
                biometricClient = new NBiometricClient();
                subject = new NSubject();
                face = new NFace();
                faceView = new NFaceView();
                image = NImage.fromFile(String.valueOf(file));
                face.setImage(image);
                subject.getFaces().add(face);
                subject.setMultipleSubjects(true);

                boolean isAddtionalFunctionalityEnabled = license.equals("FaceClient") || license.equals("FaceFastExtractor") || license.equals("SentiVeillance");
                if (isAddtionalFunctionalityEnabled) {
                    biometricClient.setFacesDetectAllFeaturePoints(true);
                    biometricClient.setFacesDetectBaseFeaturePoints(true);
                    biometricClient.setFacesRecognizeExpression(true);
                    biometricClient.setFacesRecognizeEmotion(true);
                    biometricClient.setFacesDetectProperties(true);
                    biometricClient.setFacesDetermineGender(true);
                    biometricClient.setFacesDetermineAge(true);
                    biometricClient.setFacesDetermineEthnicity(true);
                }
                task = biometricClient.createTask(EnumSet.of(NBiometricOperation.DETECT_SEGMENTS), subject);
                biometricClient.performTask(task);
                if (task.getStatus() == NBiometricStatus.OK) {
                    NBiometricStatus status = biometricClient.createTemplate(subject);
//
                    if (status == NBiometricStatus.OK) {
                        faceView.setSize(image.getWidth(), image.getHeight());

                        // Settings face with template to face view
                        faceView.setFace(subject.getFaces().get(0));
                        BufferedImage bufferedImage = new BufferedImage(faceView.getWidth(), faceView.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics2D = bufferedImage.createGraphics();
                        faceView.printAll(graphics2D);
                        ImageIO.write(bufferedImage, "jpg", new File(String.valueOf(file)));
                    } else {
                        return;
                    }
                }
                ImageView imageView = new ImageView(file.toURI().toURL().toExternalForm());
                imageView.setPreserveRatio(true);
                imageView.fitWidthProperty().bind(pane.widthProperty());
                imageView.fitHeightProperty().bind(pane.heightProperty());
                StackPane.setAlignment(imageView, Pos.CENTER);
                pane.getChildren().add(imageView);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (task != null) task.dispose();
                if (face != null) face.dispose();
                if (subject != null) subject.dispose();
                if (biometricClient != null) biometricClient.dispose();
            }
        }
    }

    public void reset() {
        pane.getChildren().clear();
    }

    public void chooserFIleEnroll(ActionEvent event) {
        resetEnroll();
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = fc.showOpenDialog(null);
        if (file != null) {
            try {
                ImageView imageView = new ImageView(file.toURI().toURL().toExternalForm());
                imageView.setPreserveRatio(true);
                imageView.fitWidthProperty().bind(paneEnroll.widthProperty());
                imageView.fitHeightProperty().bind(paneEnroll.heightProperty());
                StackPane.setAlignment(imageView, Pos.CENTER);
                paneEnroll.getChildren().add(imageView);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void resetEnroll() {
        paneEnroll.getChildren().clear();
    }
}
