package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BilledTask extends Task<Billed> {

    private List<Billed> Billeder = new ArrayList<>();

    private int whereInList = 0;

    private boolean isRunning = false;

    private int red = 0;
    private int blue = 0;
    private  int green = 0;
    private int mixed = 0;

    public BilledTask(List<Billed> billeder){
        Billeder = billeder;
    }

    public boolean runs(){
        return isRunning;
    }

    public void setIsRunning(boolean running){
        isRunning = running;
    }

    private String countImageColours(Image image) throws IOException {
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
        return ("Red: "+red+"\nGreen: "+green+"\nBlue: "+blue+"\nMixed: "+mixed);
    }

    @Override
    protected Billed call() throws Exception {
        Billed imageShown = Billeder.get(0);
        while (isRunning) {
            if (Billeder.size() > whereInList) {
                red = 0;
                blue = 0;
                green = 0;
                mixed = 0;
                imageShown = Billeder.get(whereInList++);
                updateValue(imageShown);
                updateMessage(imageShown.getUrl());
                updateTitle(countImageColours(imageShown));
                TimeUnit.SECONDS.sleep(imageShown.getTime());
            }
            else if (Billeder.size() == whereInList){
                whereInList = 0;
            }
        }
        return Billeder.get(whereInList);
    }
}
