package com.demo.example.Parser;

import java.io.Serializable;


public class Frame_Photo implements Serializable {
    private static final long serialVersionUID = 1;
    public String Coordinate;
    public int FullImage;
    public int ThumnailId;
    public String fullImageURL;
    public String imageid;
    public String thumnailIdURL;

    public int getThumnailId() {
        return this.ThumnailId;
    }

    public void setThumnailId(int i) {
        this.ThumnailId = i;
    }

    public int getFullImage() {
        return this.FullImage;
    }

    public void setFullImage(int i) {
        this.FullImage = i;
    }

    public String getCordinate() {
        return this.Coordinate;
    }

    public void setCordinate(String str) {
        this.Coordinate = str;
    }

    public String getThumnailIdURL() {
        return this.thumnailIdURL;
    }

    public void setThumnailIdURL(String str) {
        this.thumnailIdURL = str;
    }

    public String getFullImageURL() {
        return this.fullImageURL;
    }

    public void setFullImageURL(String str) {
        this.fullImageURL = str;
    }

    public String getImageId() {
        return this.imageid;
    }

    public void setImageId(String str) {
        this.imageid = str;
    }
}
