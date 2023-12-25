package com.demo.example.helper;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;


public class Util {
    public static final int FLIP_HORIZONTAL = 2;
    public static final int FLIP_VERTICAL = 1;

    public static float a(int i) {
        if (i == 6) {
            return 90.0f;
        }
        if (i == 3) {
            return 180.0f;
        }
        return i == 8 ? 270.0f : 0.0f;
    }

    public static int a(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"orientation"}, null, null, null);
        if (query == null) {
            return -1;
        }
        try {
            if (query.moveToFirst()) {
                return query.getInt(0);
            }
            return -1;
        } finally {
            query.close();
        }
    }

    public static Bitmap a(Resources resources, String str) {
        try {
            return BitmapFactory.decodeStream(resources.getAssets().open(str));
        } catch (IOException unused) {
            return null;
        }
    }

    public static Bitmap scaleCenterCrop(Bitmap bitmap, int i, int i2) {
        float f = (float) i2;
        float width = (float) bitmap.getWidth();
        float f2 = (float) i;
        float height = (float) bitmap.getHeight();
        float max = Math.max(f / width, f2 / height);
        float f3 = width * max;
        float f4 = max * height;
        float f5 = (f - f3) / 2.0f;
        float f6 = (f2 - f4) / 2.0f;
        RectF rectF = new RectF(f5, f6, f3 + f5, f4 + f6);
        Bitmap createBitmap = Bitmap.createBitmap(i2, i, bitmap.getConfig());
        new Canvas(createBitmap).drawBitmap(bitmap, (Rect) null, rectF, (Paint) null);
        return createBitmap;
    }

    public static Bitmap flip(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        if (i == 1) {
            matrix.preScale(1.0f, -1.0f);
        } else if (i != 2) {
            return null;
        } else {
            matrix.preScale(-1.0f, 1.0f);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
