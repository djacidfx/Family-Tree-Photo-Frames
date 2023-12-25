package com.demo.example.stickerapihhitter;


public class CategoryValues {
    String Description;
    String Image;
    String categoryId;
    String categoryName;

    public CategoryValues(String str, String str2, String str3, String str4) {
        this.categoryId = str;
        this.categoryName = str2;
        this.Image = str3;
        this.Description = str4;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String str) {
        this.categoryId = str;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String str) {
        this.categoryName = str;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String str) {
        this.Image = str;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String str) {
        this.Description = str;
    }
}
