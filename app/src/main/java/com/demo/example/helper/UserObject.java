package com.demo.example.helper;

import android.graphics.Bitmap;
import android.widget.RelativeLayout;


public class UserObject {
    public static Bitmap bitmap;
    public static Bitmap bitmap1;
    public static Bitmap fullbitmap;
    public static int h_asp;
    public static RelativeLayout.LayoutParams layout;
    public static Bitmap mBackgroundBitmap;
    public static String mFileLocation;
    public static Bitmap mForgroundBitmap;
    public static int nav;
    public static int rsz;
    public static int w_asp;
    public float X0;
    public float X1;
    public float Y0;
    public float Y1;

    public static void setlyoutprms(RelativeLayout.LayoutParams layoutParams) {
    }

    public static RelativeLayout.LayoutParams getlyoutprms() {
        return layout;
    }

    public static int getId() {
        return nav;
    }

    public static void setId_fr_resizing(int i) {
        rsz = i;
    }

    public static int getId_fr_resizing() {
        return rsz;
    }

    public static void setFileLocation(String str) {
        mFileLocation = str;
    }

    public static String getFileLocation() {
        return mFileLocation;
    }

    public static void setId(int i) {
        nav = i;
    }

    public static int r_W_getId() {
        return w_asp;
    }

    public static void r_W_setId(int i) {
        w_asp = i;
    }

    public static int r_h_getId() {
        return h_asp;
    }

    public static void r_h_setId(int i) {
        h_asp = i;
    }

    public static Bitmap getFullBitmap() {
        return fullbitmap;
    }

    public static void setFullBitmap(Bitmap bitmap2) {
        fullbitmap = bitmap2;
    }

    public static void setSecondBitmap(Bitmap bitmap2) {
        bitmap1 = bitmap2;
    }

    public static Bitmap getSecondBitmap() {
        return bitmap1;
    }

    public static void setBitmap(Bitmap bitmap2) {
        bitmap1 = bitmap2;
    }

    public static Bitmap getBitmap() {
        return bitmap1;
    }

    public static void setForgroundBitmap(Bitmap bitmap2) {
        mForgroundBitmap = bitmap2;
    }

    public static Bitmap getForgroundBitmap() {
        return mForgroundBitmap;
    }

    public static void setBackgroundBitmap(Bitmap bitmap2) {
        mBackgroundBitmap = bitmap2;
    }

    public static Bitmap getBackgroundBitmap() {
        return mBackgroundBitmap;
    }
}
