package dk.easv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class ImageViewerWindowController implements Initializable
{
    private final List<Billed> images = new ArrayList<>();
    @FXML
    public Button btnSetTimer;
    @FXML
    private Slider slSlideshowSpeed;
    @FXML
    private Label lblImageColours;
    @FXML
    private Label lbShowImageName;
    private int currentImageIndex = 0;
    private BilledTask billedTask;
    private ExecutorService executorService;
    private int red = 0;
    private int blue = 0;
    private int green = 0;
    private int mixed = 0;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    @FXML
    private void handleBtnLoadAction() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Billed(f.toURI().toString(), (long) slSlideshowSpeed.getValue()));
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction() throws IOException {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() throws IOException {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage() throws IOException {
        red = 0;
        blue = 0;
        green = 0;
        mixed = 0;
        if (!images.isEmpty())
        {
            imageView.setImage(images.get(currentImageIndex));
            lbShowImageName.setText(images.get(currentImageIndex).getUrl());
            countImageColours(images.get(currentImageIndex));
        }
    }

    @FXML
    private void handleStartSlideshow(){
        if (billedTask == null){
            billedTask = new BilledTask(images);
            billedTask.messageProperty().addListener(((observable, oldValue, newValue) -> {
                lbShowImageName.setText(newValue);
            }));
            billedTask.valueProperty().addListener((observable, oldValue, newValue) -> {
               imageView.setImage(newValue);
            });
            billedTask.titleProperty().addListener(((observable, oldValue, newValue) -> {
                lblImageColours.setText(newValue);
            }));
            executorService.execute(billedTask);
            billedTask.setIsRunning(true);
        }
        else if (!billedTask.isRunning()){
            executorService.execute(billedTask);
            billedTask.messageProperty().addListener(((observable, oldValue, newValue) -> {
                lbShowImageName.setText(newValue);
            }));
            billedTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                imageView.setImage(newValue);
            });
            billedTask.titleProperty().addListener(((observable, oldValue, newValue) -> {
                lblImageColours.setText(newValue);
            }));
            billedTask.setIsRunning(true);
        }
    }
    @FXML
    private void handleStopSlideshow(){
        billedTask.setIsRunning(false);
        billedTask = null;
    }

    private void countImageColours(Image image) throws IOException{
        String imageURL = image.getUrl().replace("%20", " ");
        System.out.println(imageURL.substring(6));
        BufferedImage bufferedImage = ImageIO.read(new File(imageURL.substring(6)));
        int height = bufferedImage.getHeight(), width = bufferedImage.getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int RGB = bufferedImage.getRGB(x, y);
                int red = (RGB >> 16) & 255, green = (RGB >> 8) & 255, blue = RGB & 255;
                if (red > green && red > blue){
                        this.red++;
                } else if (blue > green && blue > red){
                    this.blue++;
                }else if (green > blue && green > red){
                    this.green++;
                }else if (blue == red && blue == green && red == green){
                    mixed++;
                }
            }
        }
        System.out.println("Red: "+red+"\nGreen: "+green+"\nBlue: "+blue+"\nMixed: "+mixed);
        lblImageColours.setText("Red: "+red+"\nGreen: "+green+"\nBlue: "+blue+"\nMixed: "+mixed);
    }

    @FXML
    private void handleSetTime(){
        images.get(currentImageIndex).setTime((long)slSlideshowSpeed.getValue());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executorService = Executors.newSingleThreadExecutor();
    }
}