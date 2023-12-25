package com.demo.example.helper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import java.io.InputStream;
import java.util.ArrayList;


public class Utils {
    public static Bitmap decodeSampledBitmapFromStream(InputStream inputStream, int i, int i2) {
        Log.i("reqWidth&reqHeight", "reqWidth" + i + " reqHeight" + i2);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, i, i2);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        if (i3 <= i2 && i4 <= i) {
            return 1;
        }
        int round = Math.round(((float) i3) / ((float) i2));
        int round2 = Math.round(((float) i4) / ((float) i));
        return round < round2 ? round : round2;
    }

    public static ArrayList<String> getAlbumThumbnailsforHomePage(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor loadInBackground = new CursorLoader(context, MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "date_added", "media_type", "mime_type", "title"}, "media_type=1", null, "date_added DESC LIMIT 50").loadInBackground();
        int columnIndex = loadInBackground.getColumnIndex("_data");
        if (loadInBackground.moveToFirst()) {
            String str = null;
            do {
                Cursor queryMiniThumbnail = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), (long) Integer.parseInt(loadInBackground.getString(loadInBackground.getInt(columnIndex)).trim()), 1, null);
                if (queryMiniThumbnail != null && queryMiniThumbnail.getCount() > 0) {
                    queryMiniThumbnail.moveToFirst();
                    str = queryMiniThumbnail.getString(queryMiniThumbnail.getColumnIndex("_data")).trim();
                }
                arrayList.add(str);
            } while (loadInBackground.moveToNext());
            loadInBackground.close();
            return arrayList;
        }
        loadInBackground.close();
        return arrayList;
    }

    public static ArrayList<String> getAlbumThumbnailsForHomepagePreHonecomb(Context context) {
        Cursor query = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{"_data", "image_id"}, null, null, "image_id DESC LIMIT 50 ");
        int columnIndex = query.getColumnIndex("_data");
        ArrayList<String> arrayList = new ArrayList<>(query.getCount());
        if (query.moveToFirst()) {
            do {
                String string = query.getString(query.getInt(columnIndex));
                query.getString(query.getColumnIndex("image_id"));
                arrayList.add(string.trim());
            } while (query.moveToNext());
            query.close();
            return arrayList;
        }
        query.close();
        return arrayList;
    }
}
