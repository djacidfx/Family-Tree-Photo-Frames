package com.demo.example.Save;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import com.demo.example.AdAdmob;
import com.demo.example.R;

import com.demo.example.PictureFrames.FileUtils;
import com.demo.example.PictureFrames.FrameActivity;
import com.demo.example.PictureFrames.TransferBitmap;

import okhttp3.internal.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SaveActivity extends Activity {
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    Bitmap bitmap;
    Bitmap bmp;
    private Handler handler;
    ImageView iv_imageView;
    LinearLayout ll_back;
    LinearLayout ll_invitefriend;
    LinearLayout ll_setWall;
    LinearLayout ll_share;
    String pathfile;
    boolean isSaved = false;
    FileOutputStream out = null;


    public static void lambda$null$0(Task task) {
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(R.layout.save_activity);


        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.bannerAd), this);
        adAdmob.FullscreenAd(this);


        ImageView imageView = (ImageView) findViewById(R.id.iv_imageView);
        this.iv_imageView = imageView;
        imageView.setImageBitmap(FrameActivity.savepicbmp.copy(Bitmap.Config.ARGB_8888, true));
        this.bitmap = FrameActivity.savepicbmp.copy(Bitmap.Config.ARGB_8888, true);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_back);
        this.ll_back = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveActivity.this.onBackPressed();
            }
        });
        new SaveImage().execute(new Void[0]);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.ll_share);
        this.ll_share = linearLayout3;
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveActivity.this.share();
                Toast.makeText(SaveActivity.this.getApplicationContext(), "Lets Share...", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.ll_setWall);
        this.ll_setWall = linearLayout4;
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    WallpaperManager.getInstance(SaveActivity.this.getApplicationContext()).setBitmap(FrameActivity.savepicbmp.copy(Bitmap.Config.ARGB_8888, true));
                    SaveActivity.this.handler = new Handler();
                    SaveActivity.this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SaveActivity.this.getApplicationContext(), "Wallpaper set successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        LinearLayout linearLayout5 = (LinearLayout) findViewById(R.id.ll_invitefriend);
        this.ll_invitefriend = linearLayout5;
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SaveActivity.this).setIcon(17301543).setMessage("Do you want to go to home page?").setPositiveButton(SaveActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SaveActivity.this.setResult(111);
                        SaveActivity.this.finish();
                    }
                }).setNegativeButton(SaveActivity.this.getResources().getString(R.string.no), (DialogInterface.OnClickListener) null).show();
            }
        });

    }


    class SaveImage extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        String fileName = null;

        SaveImage() {
            this.dialog = ProgressDialog.show(SaveActivity.this, SaveActivity.this.getString(R.string.saving_title), SaveActivity.this.getString(R.string.saving_to_sd), true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                SaveActivity saveActivity = SaveActivity.this;
                saveActivity.bmp = saveActivity.bitmap.copy(Bitmap.Config.ARGB_8888, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Void doInBackground(Void... voidArr) {
            SaveActivity saveActivity;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + SaveActivity.this.getResources().getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date date = new Date();
                String str = file.getAbsolutePath() + "/" + simpleDateFormat.format(date) + FileUtils.IMAGE_EXTENSION_JPEG;
                this.fileName = str;
                SaveActivity.this.pathfile = str;
            } catch (Exception unused) {
            }
            try {
                try {
                    try {
                        SaveActivity.this.out = new FileOutputStream(this.fileName);
                        SaveActivity saveActivity2 = SaveActivity.this;
                        saveActivity2.isSaved = saveActivity2.bmp.compress(Bitmap.CompressFormat.JPEG, 100, SaveActivity.this.out);
                        SaveActivity.this.updateMedia(this.fileName);
                        SaveActivity.this.out.close();
                        saveActivity = SaveActivity.this;
                    } catch (Exception e) {
                        e.printStackTrace();
                        saveActivity = SaveActivity.this;
                    }
                    saveActivity.out.close();
                    return null;
                } catch (Throwable unused2) {
                    return null;
                }
            } catch (Throwable th) {
                try {
                    SaveActivity.this.out.close();
                } catch (Throwable unused3) {
                }
                throw th;
            }
        }


        public void onPostExecute(Void r3) {
            super.onPostExecute(r3);
            this.dialog.dismiss();
            Toast.makeText(SaveActivity.this.getApplicationContext(), "Saved in Gallery", Toast.LENGTH_SHORT).show();
            if (TransferBitmap.isShowCount) {
                TransferBitmap.isShowCount = false;
            }
        }
    }


    public void share() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/jpeg");
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "title");
        contentValues.put("mime_type", "image/jpeg");
        Uri insert = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            OutputStream openOutputStream = getContentResolver().openOutputStream(insert);
            this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, openOutputStream);
            openOutputStream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        intent.putExtra("android.intent.extra.STREAM", insert);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }


    public void updateMedia(String str) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{str}, null, null);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(17301543).setMessage("Do you want to exit?").setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SaveActivity.this.finish();
            }
        }).setNegativeButton(getResources().getString(R.string.no), (DialogInterface.OnClickListener) null).show();
    }
}
