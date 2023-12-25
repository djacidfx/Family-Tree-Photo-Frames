package com.demo.example.PictureFrames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.demo.example.R;


public class Permission_Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static boolean checkPermission(Context context, final Activity activity, boolean z) {
        String string = activity.getResources().getString(R.string.permission_message);
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.CAMERA") == 0) {
            return true;
        }
        if ((ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.CAMERA")) && !z) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setCancelable(false);
            builder.setMessage(string + "?");
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { 
                @Override 
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.finish();
                }
            });
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { 
                @Override 
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 123);
                }
            });
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 123);
        }
        return false;
    }


    public static boolean checkPermission1(Context context, final Activity activity, boolean z) {
        String string = activity.getResources().getString(R.string.permission_message);
        if (ContextCompat.checkSelfPermission(context, "android.permission.CAMERA") == 0) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.CAMERA") && !z) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setCancelable(false);
            builder.setMessage(string + "?");
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.finish();
                }
            });
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity, new String[]{"android.permission.CAMERA"}, 1231);
                }
            });
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.CAMERA"}, 1231);
        }
        return false;
    }
}
