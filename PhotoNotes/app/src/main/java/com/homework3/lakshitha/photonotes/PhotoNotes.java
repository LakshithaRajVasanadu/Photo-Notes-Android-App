package com.homework3.lakshitha.photonotes;

import java.io.Serializable;

/**
 * Created by lakshitha on 2/12/16.
 */
public class PhotoNotes implements Serializable{

    private int id;
    private String caption;
    private String imagePath;

    public PhotoNotes(int id, String caption, String imagePath) {
        this.id = id;
        this.caption = caption;
        this.imagePath = imagePath;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
