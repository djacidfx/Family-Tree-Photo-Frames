package com.demo.example.cameragallery;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.demo.example.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class ImagePicker {
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;
    public static final int PICK_IMAGE_REQUEST_CODE = 234;
    private static final String TAG = "ImagePicker";
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private static Bitmap bm = null;
    private static Uri imageUri = null;
    private static int minHeightQuality = 400;
    private static int minWidthQuality = 400;

    private ImagePicker() {
    }

    public static void pickImage(Activity activity) {
        pickImage(activity, activity.getString(R.string.pick_image_intent_text));
    }

    public static void pickImage(Fragment fragment) {
        pickImage(fragment, fragment.getString(R.string.pick_image_intent_text));
    }

    public static void pickImage(Activity activity, String str) {
        activity.startActivityForResult(getPickImageIntent(activity, str), PICK_IMAGE_REQUEST_CODE);
    }

    public static void pickImage(Fragment fragment, String str) {
        fragment.startActivityForResult(getPickImageIntent(fragment.getContext(), str), PICK_IMAGE_REQUEST_CODE);
    }

    public static void pickCameraImage(Activity activity) {
        activity.startActivityForResult(getPickCameraImageIntent(activity), PICK_IMAGE_REQUEST_CODE);
    }

    public static void pickCameraImage(Fragment fragment) {
        fragment.startActivityForResult(getPickCameraImageIntent(fragment.getContext()), PICK_IMAGE_REQUEST_CODE);
    }

    public static void pickGalleryImage(Activity activity, String str) {
        activity.startActivityForResult(getPickGalleryImageIntent(activity, str), PICK_IMAGE_REQUEST_CODE);
    }

    public static void pickGalleryImage(Fragment fragment, String str) {
        fragment.startActivityForResult(getPickGalleryImageIntent(fragment.getContext(), str), PICK_IMAGE_REQUEST_CODE);
    }

    public static Intent getPickImageIntent(Context context, String str) {
        List<Intent> addIntentsToList = addIntentsToList(context, new ArrayList(), new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        if (hasCameraAccess(context)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", "New Picture");
            contentValues.put("description", "From your Camera");
            imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra("output", imageUri);
            addIntentsToList = addIntentsToList(context, addIntentsToList, intent);
        }
        if (addIntentsToList.size() <= 0) {
            return null;
        }
        Intent createChooser = Intent.createChooser(addIntentsToList.remove(addIntentsToList.size() - 1), str);
        createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) addIntentsToList.toArray(new Parcelable[addIntentsToList.size()]));
        return createChooser;
    }

    public static Intent getPickCameraImageIntent(Context context) {
        if (!hasCameraAccess(context)) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "New Picture");
        contentValues.put("description", "From your Camera");
        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", imageUri);
        return intent;
    }

    public static Intent getPickGalleryImageIntent(Context context, String str) {
        List<Intent> addIntentsToList = addIntentsToList(context, new ArrayList(), new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        if (addIntentsToList.size() <= 0) {
            return null;
        }
        Intent createChooser = Intent.createChooser(addIntentsToList.remove(addIntentsToList.size() - 1), str);
        createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) addIntentsToList.toArray(new Parcelable[addIntentsToList.size()]));
        return createChooser;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        String str = TAG;
        Log.i(str, "Adding intents of type: " + intent.getAction());
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 0)) {
            String str2 = resolveInfo.activityInfo.packageName;
            Intent intent2 = new Intent(intent);
            intent2.setPackage(str2);
            list.add(intent2);
            String str3 = TAG;
            Log.i(str3, "App package: " + str2);
        }
        return list;
    }

    private static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.CAMERA") == 0;
    }

    public static Bitmap getImageFromResult(Context context, int i, int i2, Intent intent) {
        Uri uri;
        String str = TAG;
        Log.i(str, "getImageFromResult() called with: resultCode = [" + i2 + "]");
        if (i2 != -1 || i != 234) {
            return null;
        }
        boolean z = intent == null || intent.getData() == null || intent.getData().toString().contains(getTemporalFile(context).toString());
        if (z) {
            uri = imageUri;
        } else {
            uri = intent.getData();
        }
        Log.i(str, "selectedImage: " + uri);
        if (uri == null) {
            return null;
        }
        return ImageRotator.rotate(getImageResized(context, uri), ImageRotator.getRotation(context, uri, z));
    }

    public static Bitmap getImageFromUri(Context context, Uri uri) {
        return getImageResized(context, uri);
    }

    public static Uri getImageUriFromData(Context context, int i, int i2, Intent intent) {
        if (i2 != -1) {
            return null;
        }
        if (intent == null || intent.getData() == null || intent.getData().toString().contains(getTemporalFile(context).toString())) {
            return imageUri;
        }
        return intent.getData();
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", (String) null));
    }

    private static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
    }

    public static Bitmap getImageResized(Context context, Uri uri) {
        Bitmap decodeBitmap;
        int[] iArr = {5, 3, 2, 1};
        int i = 0;
        do {
            decodeBitmap = decodeBitmap(context, uri, iArr[i]);
            i++;
            if (decodeBitmap == null || (decodeBitmap.getWidth() >= minWidthQuality && decodeBitmap.getHeight() >= minHeightQuality)) {
                break;
            }
        } while (i < 4);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Final bitmap width = ");
        sb.append(decodeBitmap != null ? Integer.valueOf(decodeBitmap.getWidth()) : "No final bitmap");
        Log.i(str, sb.toString());
        return decodeBitmap;
    }

    private static Bitmap decodeBitmap(Context context, Uri uri, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = i;
        Bitmap bitmap = null;
        try {
            AssetFileDescriptor openAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            bitmap = BitmapFactory.decodeFileDescriptor(openAssetFileDescriptor.getFileDescriptor(), null, options);
            String str = TAG;
            Log.i(str, "Trying sample size " + options.inSampleSize + "\t\tBitmap width: " + bitmap.getWidth() + "\theight: " + bitmap.getHeight());
            openAssetFileDescriptor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (bitmap != null) {
            return bitmap;
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.add_photo);
    }
}
