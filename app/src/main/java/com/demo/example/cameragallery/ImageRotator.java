package com.demo.example.cameragallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public final class ImageRotator {
    private static final String TAG = "ImageRotator";

    private ImageRotator() {
    }

    public static int getRotation(Context context, Uri uri, boolean z) {
        int i;
        if (z) {
            i = getRotationFromCamera(context, uri);
        } else {
            i = getRotationFromGallery(context, uri);
        }
        String str = TAG;
        Log.i(str, "Image rotation: " + i);
        return i;
    }

    private static int getRotationFromCamera(Context context, Uri uri) {
        try {
            context.getContentResolver().notifyChange(uri, null);
            int attributeInt = new ExifInterface(uri.getPath()).getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 1);
            if (attributeInt == 3) {
                return 180;
            }
            if (attributeInt == 6) {
                return 90;
            }
            if (attributeInt != 8) {
                return 0;
            }
            return 270;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        } 
        return result;
    }

    public static Bitmap rotate(Bitmap bitmap, int i) {
        if (bitmap == null || i == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate((float) i);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
