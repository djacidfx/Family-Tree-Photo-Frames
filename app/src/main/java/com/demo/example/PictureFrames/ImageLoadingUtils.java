package com.demo.example.PictureFrames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

import com.demo.example.R;


public class ImageLoadingUtils {
    private Context context;
    public Bitmap icon;

    public ImageLoadingUtils(Context context) {
        this.context = context;
        this.icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon200);
    }

    public int convertDipToPixels(float f) {
        return (int) TypedValue.applyDimension(1, f, this.context.getResources().getDisplayMetrics());
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        int i3;
        int i4 = options.outHeight;
        int i5 = options.outWidth;
        if (i4 > i2 || i5 > i) {
            i3 = Math.round(((float) i4) / ((float) i2));
            int round = Math.round(((float) i5) / ((float) i));
            if (i3 >= round) {
                i3 = round;
            }
        } else {
            i3 = 1;
        }
        while (((float) (i5 * i4)) / ((float) (i3 * i3)) > ((float) (i * i2 * 2))) {
            i3++;
        }
        return i3;
    }

    public Bitmap decodeBitmapFromPath(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150.0f), convertDipToPixels(200.0f));
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(str, options);
    }
}
