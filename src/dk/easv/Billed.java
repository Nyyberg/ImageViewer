package dk.easv;

import javafx.scene.image.Image;


public class Billed extends Image {
    private long timer;

    public Billed(String url, long timer) {
        super(url);
        this.timer = timer;
    }
    public long getTime(){
        return timer;
    }

    public void setTime(long value){
        this.timer = value;
    }
}
