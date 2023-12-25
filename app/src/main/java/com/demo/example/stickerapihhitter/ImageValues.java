package com.demo.example.stickerapihhitter;


public class ImageValues {
    String CategoryId;
    String ImageId;
    String ImageName;
    String Thumbnail;

    public ImageValues(String str, String str2, String str3, String str4) {
        this.ImageId = str;
        this.CategoryId = str2;
        this.Thumbnail = str3;
        this.ImageName = str4;
    }

    public String getImageId() {
        return this.ImageId;
    }

    public void setImageId(String str) {
        this.ImageId = str;
    }

    public String getCategoryId() {
        return this.CategoryId;
    }

    public void setCategoryId(String str) {
        this.CategoryId = str;
    }

    public String getThumbnail() {
        return this.Thumbnail;
    }

    public void setThumbnail(String str) {
        this.Thumbnail = str;
    }

    public String getImageName() {
        return this.ImageName;
    }

    public void setImageName(String str) {
        this.ImageName = str;
    }
}
