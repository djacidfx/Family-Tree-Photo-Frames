package com.demo.example.PictureFrames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.demo.example.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.demo.example.Parser.Coordinates;
import com.demo.example.Parser.Frame_Parser;
import com.demo.example.Parser.Frame_Photo;
import com.demo.example.Save.SaveActivity;
import com.demo.example.cameragallery.ImagePicker;
import com.demo.example.helper.HorizontalListView;
import com.demo.example.helper.UserObject;
import com.demo.example.helper.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class FrameActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnTouchListener, DiscreteSeekBar.OnProgressChangeListener {
    private static final int ACTION_REQUEST_GALLERY = 99;
    public static String ID = null;
    private static final int IMAGE_CAPTURE = 13;
    public static Bitmap bitmap = null;
    public static Context context = null;
    public static String cordinate = null;
    public static Bitmap currentApiBitmap = null;
    public static String currentCoordinate = null;
    public static ArrayList<CustomFrameLayoutFrame> customFrameLayouts = null;
    static FrameActivity frameActivity = null;
    public static boolean frameCaller = false;
    public static int imageCount = 0;
    public static String imagePath = "";
    public static boolean isPicsaved = false;
    public static String link = null;
    public static String pathFrame = null;
    public static String pathSticker = null;
    public static Bitmap savedBitmap = null;
    public static Bitmap savepicbmp = null;
    public static int selected_frame_id = -1;
    public static boolean stickerCaller = false;
    public static String thumbnailURL;
    RelativeLayout PhotoLayout;
    AdapterFilterList adapter;
    Bitmap bmp;
    RelativeLayout body;
    CollageViewMakerSticker collage_sticker;
    Bitmap currentChangeBmp;
    Bitmap currentOrgnBmp;
    ProgressBar dottedBar;
    HorizontalListView fltrvw;
    Bitmap frame_bitmap;
    HorizontalListView horizontallistview;
    String image_URI;
    ImageView ivFrame;
    ImageView iv_delete;
    ImageView iv_filterClose;
    List<String> list;
    LinearLayout ll_apply;
    LinearLayout ll_back;
    LinearLayout ll_save;
    TypedArray lutArray;
    Bitmap lutBmp;
    String mCurrentPhotoPath;
    HorizontalImageAdapter mHorizontalListAdapter;
    Animation myAnim;
    Frame_Parser parser;
    int pos;
    float previousBitmapHeight;
    float previousBitmapWidth;
    ImageView progressMain;
    RelativeLayout r_anticlock_rotate;
    RelativeLayout r_clock_rotate;
    RelativeLayout r_color;
    RelativeLayout r_edit;
    RelativeLayout r_flip;
    RelativeLayout r_framewithstrk;
    RelativeLayout r_tools;
    RenderScriptLutColorFilter rs_color_filter;
    DiscreteSeekBar sb_color;
    LinearLayout showGrid;
    public int viewHeight;
    public int viewWidth;
    ArrayList<Frame_Photo> _feed = new ArrayList<>();
    int aviary = 0;
    boolean ratio = true;
    Target targetSticker = new Target() {
        @Override
        public void onPrepareLoad(Drawable drawable) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap2, Picasso.LoadedFrom loadedFrom) {
            FrameActivity.this.ll_save.setVisibility(View.GONE);
            FrameActivity.this.ll_apply.setVisibility(View.VISIBLE);
            FrameActivity.this.dottedBar.setVisibility(View.INVISIBLE);
            FrameActivity.this.collage_sticker.loadImages(FrameActivity.this, bitmap2);
            FrameActivity.pathSticker = null;
        }

        @Override
        public void onBitmapFailed(Exception exc, Drawable drawable) {
            Toast.makeText(FrameActivity.this, "Internet Connection Problem Occured", Toast.LENGTH_SHORT).show();
            FrameActivity.this.dottedBar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(R.layout.frame_activityframe);

        this.lutArray = getResources().obtainTypedArray(R.array.lut_image_id);
        this.rs_color_filter = new RenderScriptLutColorFilter(this);
        this.fltrvw = (HorizontalListView) findViewById(R.id.fltrvw);
        this.iv_filterClose = (ImageView) findViewById(R.id.iv_filterClose);
        HorizontalListView horizontalListView = (HorizontalListView) findViewById(R.id.horizontallistview1);
        this.horizontallistview = horizontalListView;
        horizontalListView.setOnItemClickListener(this);
        this.fltrvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Iterator<CustomFrameLayoutFrame> it2 = FrameActivity.customFrameLayouts.iterator();
                while (it2.hasNext()) {
                    CustomFrameLayoutFrame next = it2.next();
                    if (next.getFrame_id() == FrameActivity.selected_frame_id) {
                        FrameActivity.bitmap = next.getselBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    }
                }
                if (i != 0) {
                    FrameActivity frameActivity2 = FrameActivity.this;
                    frameActivity2.lutBmp = BitmapFactory.decodeResource(frameActivity2.getResources(), FrameActivity.this.lutArray.getResourceId(i, R.drawable.icon200));
                    FrameActivity.bitmap = FrameActivity.this.rs_color_filter.renderImage(FrameActivity.bitmap, FrameActivity.this.lutBmp);
                }
                Iterator<CustomFrameLayoutFrame> it3 = FrameActivity.customFrameLayouts.iterator();
                while (it3.hasNext()) {
                    CustomFrameLayoutFrame next2 = it3.next();
                    if (next2.getFrame_id() == FrameActivity.selected_frame_id) {
                        next2.setImageAttach(true);
                    }
                }
            }
        });
        this.iv_filterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameActivity.this.fltrvw.setVisibility(View.GONE);
                FrameActivity.this.iv_filterClose.setVisibility(View.GONE);
            }
        });
        this.ivFrame = (ImageView) findViewById(R.id.ivFrame);
        this.PhotoLayout = (RelativeLayout) findViewById(R.id.PhotoLayout);
        ImageView imageView = (ImageView) findViewById(R.id.progressMain);
        this.progressMain = imageView;
        imageView.setVisibility(View.GONE);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        this.body = relativeLayout;
        relativeLayout.setOnTouchListener(this);
        CollageViewMakerSticker collageViewMakerSticker = (CollageViewMakerSticker) findViewById(R.id.collage_sticker);
        this.collage_sticker = collageViewMakerSticker;
        collageViewMakerSticker.mImages.clear();
        initialization();
        frameActivity = new FrameActivity();
        this.myAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely);
        Frame_Parser frame_Parser = new Frame_Parser();
        this.parser = frame_Parser;
        this._feed = frame_Parser.getFRAMEThumbnails(this);
        HorizontalImageAdapter horizontalImageAdapter = new HorizontalImageAdapter();
        this.mHorizontalListAdapter = horizontalImageAdapter;
        this.horizontallistview.setAdapter((ListAdapter) horizontalImageAdapter);
        final Frame_Photo item = getItem(0);
        this.mHorizontalListAdapter.selected_mark_pos = 0;
        this.mHorizontalListAdapter.notifyDataSetChanged();
        if (item.imageid == null) {
            this.ratio = true;
            this.progressMain.setVisibility(View.VISIBLE);
            this.progressMain.startAnimation(this.myAnim);
            final int i = item.FullImage;
            final int i2 = item.ThumnailId;
            final String str = item.Coordinate;
            if (!(i == 0 || i2 == 0)) {
                this.body.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        FrameActivity.this.body.getViewTreeObserver().removeOnPreDrawListener(this);
                        FrameActivity frameActivity2 = FrameActivity.this;
                        frameActivity2.viewWidth = frameActivity2.body.getMeasuredWidth();
                        FrameActivity frameActivity3 = FrameActivity.this;
                        frameActivity3.viewHeight = frameActivity3.body.getMeasuredHeight();
                        new NewTask(i, i2, str).execute(new String[0]);
                        return true;
                    }
                });
            }
        } else {
            this.ratio = false;
            currentCoordinate = item.Coordinate;
            this.body.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    FrameActivity.this.body.getViewTreeObserver().removeOnPreDrawListener(this);
                    FrameActivity frameActivity2 = FrameActivity.this;
                    frameActivity2.viewWidth = frameActivity2.body.getMeasuredWidth();
                    FrameActivity frameActivity3 = FrameActivity.this;
                    frameActivity3.viewHeight = frameActivity3.body.getMeasuredHeight();
                    FrameActivity.this.setFrameFromSDCard(item);
                    return true;
                }
            });
        }

    }

    void initialization() {
        this.r_tools = (RelativeLayout) findViewById(R.id.r_tools);
        this.r_framewithstrk = (RelativeLayout) findViewById(R.id.r_framewithstrk);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r_anticlock_rotate);
        this.r_anticlock_rotate = relativeLayout;
        relativeLayout.setOnClickListener(this);
        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.r_clock_rotate);
        this.r_clock_rotate = relativeLayout2;
        relativeLayout2.setOnClickListener(this);
        RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.r_flip);
        this.r_flip = relativeLayout3;
        relativeLayout3.setOnClickListener(this);
        RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.r_edit);
        this.r_edit = relativeLayout4;
        relativeLayout4.setOnClickListener(this);
        RelativeLayout relativeLayout5 = (RelativeLayout) findViewById(R.id.r_color);
        this.r_color = relativeLayout5;
        relativeLayout5.setOnClickListener(this);
        DiscreteSeekBar discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.sb_color);
        this.sb_color = discreteSeekBar;
        discreteSeekBar.setVisibility(View.INVISIBLE);
        this.sb_color.setOnProgressChangeListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.iv_delete);
        this.iv_delete = imageView;
        imageView.setOnClickListener(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_back);
        this.ll_back = linearLayout;
        linearLayout.setOnClickListener(this);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.ll_apply);
        this.ll_apply = linearLayout2;
        linearLayout2.setOnClickListener(this);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.ll_save);
        this.ll_save = linearLayout3;
        linearLayout3.setOnClickListener(this);
        this.dottedBar = (ProgressBar) findViewById(R.id.progress);
    }


    class ViewHolder {
        ImageView cover;
        ImageView select_mark;

        ViewHolder() {
        }
    }


    public class HorizontalImageAdapter extends BaseAdapter {
        String imageID;
        String mFavImageID;
        private LayoutInflater mInflater;
        Frame_Photo photo;
        public int selected_mark_pos = -1;

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        public HorizontalImageAdapter() {
            this.mInflater = (LayoutInflater) FrameActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return FrameActivity.this._feed.size();
        }

        @Override
        public Frame_Photo getItem(int i) {
            return FrameActivity.this._feed.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view2 = this.mInflater.inflate(R.layout.frame_grid_item, (ViewGroup) null);
                viewHolder.cover = (ImageView) view2.findViewById(R.id.cover);
                viewHolder.select_mark = (ImageView) view2.findViewById(R.id.icon_background);
                view2.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                view2 = view;
            }
            if (this.selected_mark_pos == i) {
                viewHolder.select_mark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.select_mark.setVisibility(View.GONE);
            }
            Frame_Photo item = getItem(i);
            this.photo = item;
            if (item.imageid == null) {
                viewHolder.cover.setImageResource(this.photo.ThumnailId);
            } else {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Frame");
                viewHolder.cover.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath() + "/Frame_Thumb" + this.photo.imageid + FileUtils.IMAGE_EXTENSION_PNG));
            }
            viewHolder.cover.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.cover.setPadding(9, 18, 9, 18);
            return view2;
        }
    }

    public Frame_Photo getItem(int i) {
        return this._feed.get(i);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Frame_Photo item = getItem(i);
        this.fltrvw.setVisibility(View.GONE);
        this.iv_filterClose.setVisibility(View.GONE);
        this.mHorizontalListAdapter.selected_mark_pos = i;
        this.mHorizontalListAdapter.notifyDataSetChanged();
        if (item.imageid == null) {
            this.ratio = true;
            this.progressMain.setVisibility(View.VISIBLE);
            this.progressMain.startAnimation(this.myAnim);
            int i2 = item.FullImage;
            int i3 = item.ThumnailId;
            String str = item.Coordinate;
            if (!(i2 == 0 || i3 == 0)) {
                this.body.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.body.getLayoutParams();
                layoutParams.addRule(13);
                layoutParams.addRule(3, R.id.header_layout);
                layoutParams.addRule(2, R.id.lower_layout);
                this.body.invalidate();
                Log.d("abcd", "Coordinate value :::" + str);
                new NewTask(i2, i3, str).execute(new String[0]);
            }
        } else {
            this.ratio = false;
            currentCoordinate = item.Coordinate;
            this.progressMain.setVisibility(View.VISIBLE);
            this.progressMain.startAnimation(this.myAnim);
            this.body.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.body.getLayoutParams();
            layoutParams2.addRule(13);
            layoutParams2.addRule(3, R.id.header_layout);
            layoutParams2.addRule(2, R.id.lower_layout);
            this.body.invalidate();
            setFrameFromSDCard(item);
        }
        this.r_tools.setVisibility(View.INVISIBLE);
        this.sb_color.setProgress(180);
        this.sb_color.setVisibility(View.INVISIBLE);
        this.iv_delete.setVisibility(View.INVISIBLE);
        selected_frame_id = -1;
    }

    int hh = 800, ww = 800;


    class NewTask extends AsyncTask<String, Void, Void> {
        ArrayList<Coordinates> allCoordinatesLists;
        Bitmap bmpthumb;
        String coordinates;
        float[] dimension;
        float previousBitmapHeight;
        float previousBitmapWidth;
        int resourceId;
        int resourceIdThumb;

        public NewTask(int i, int i2, String str) {
            this.resourceIdThumb = 0;
            this.coordinates = null;
            this.resourceId = i;
            this.resourceIdThumb = i2;
            this.coordinates = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        public Void doInBackground(String... strArr) {
            FrameActivity frameActivity = FrameActivity.this;
            frameActivity.bmp = BitmapFactory.decodeResource(frameActivity.getResources(), this.resourceId);
            this.bmpthumb = BitmapFactory.decodeResource(FrameActivity.this.getResources(), this.resourceIdThumb);


            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
            Bitmap bmpa = BitmapFactory.decodeResource(frameActivity.getResources(),
                    resourceId, o);
            ww = bmpa.getWidth();
            hh = bmpa.getHeight();


            Log.e("ww", "" + ww);
            Log.e("hh", "" + hh);

            return null;
        }


        public void onPostExecute(Void r4) {
            super.onPostExecute(r4);
            FrameActivity frameActivity = FrameActivity.this;
            new NewTaskforImageDownload(frameActivity.bmp, this.coordinates).execute(new String[0]);
        }
    }



    public class NewTaskforImageDownload extends AsyncTask<String, Void, Void> {
        ArrayList<Coordinates> allCoordinatesLists;
        String coordinates;
        float[] dimension;
        int resourceId = 0;

        public NewTaskforImageDownload(Bitmap bitmap, String str) {
            this.coordinates = null;
            ArrayList<Coordinates> arrayList = new ArrayList<>();
            this.allCoordinatesLists = arrayList;
            arrayList.clear();
            FrameActivity.this.frame_bitmap = bitmap;
            this.coordinates = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FrameActivity.this.body.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    FrameActivity.this.body.getViewTreeObserver().removeOnPreDrawListener(this);
                    FrameActivity.this.viewWidth = FrameActivity.this.body.getMeasuredWidth();
                    FrameActivity.this.viewHeight = FrameActivity.this.body.getMeasuredHeight();
                    return true;
                }
            });
        }


        public Void doInBackground(String... strArr) {
            if (FrameActivity.this.frame_bitmap == null) {
                FrameActivity frameActivity = FrameActivity.this;
                frameActivity.frame_bitmap = BitmapFactory.decodeResource(frameActivity.getResources(), this.resourceId);


            }
            if (FrameActivity.this.ratio) {
                FrameActivity frameActivity2 = FrameActivity.this;
                frameActivity2.previousBitmapWidth = (float) frameActivity2.frame_bitmap.getWidth();
                FrameActivity frameActivity3 = FrameActivity.this;
                frameActivity3.previousBitmapHeight = (float) frameActivity3.frame_bitmap.getHeight();
            } else {
                FrameActivity frameActivity4 = FrameActivity.this;
                frameActivity4.previousBitmapWidth = (float) frameActivity4.frame_bitmap.getWidth();
                FrameActivity frameActivity5 = FrameActivity.this;
                frameActivity5.previousBitmapHeight = (float) frameActivity5.frame_bitmap.getHeight();
            }


            float f = ((float) FrameActivity.this.viewWidth) / FrameActivity.this.previousBitmapWidth;
            float f2 = ((float) FrameActivity.this.viewHeight) / FrameActivity.this.previousBitmapHeight;
            Matrix matrix = new Matrix();
            if (f > f2) {
                matrix.setScale(f, f);
                FrameActivity frameActivity6 = FrameActivity.this;
                frameActivity6.frame_bitmap = Bitmap.createBitmap(frameActivity6.frame_bitmap, 0, 0, FrameActivity.this.frame_bitmap.getWidth(), FrameActivity.this.frame_bitmap.getHeight(), matrix, true);
                matrix.getValues(new float[9]);
                this.allCoordinatesLists = FrameActivity.this.findDimensionListFrontImage(f, f, this.coordinates);
                return null;
            }
            matrix.setScale(f2, f2);
            FrameActivity frameActivity7 = FrameActivity.this;
            frameActivity7.frame_bitmap = Bitmap.createBitmap(frameActivity7.frame_bitmap, 0, 0, FrameActivity.this.frame_bitmap.getWidth(), FrameActivity.this.frame_bitmap.getHeight(), matrix, true);
            matrix.getValues(new float[9]);
            this.allCoordinatesLists = FrameActivity.this.findDimensionListFrontImage(f2, f2, this.coordinates);
            return null;
        }


        public void onPostExecute(Void r4) {
            super.onPostExecute(r4);
            FrameActivity.this.ivFrame.setImageBitmap(FrameActivity.this.frame_bitmap);
            FrameActivity frameActivity = FrameActivity.this;
            frameActivity.currentOrgnBmp = frameActivity.frame_bitmap.copy(Bitmap.Config.ARGB_8888, true);
            FrameActivity.this.ivFrame.invalidate();
            FrameActivity.this.ivFrame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    FrameActivity.this.ivFrame.getViewTreeObserver().removeOnPreDrawListener(this);
                    int measuredHeight = FrameActivity.this.ivFrame.getMeasuredHeight();
                    int measuredWidth = FrameActivity.this.ivFrame.getMeasuredWidth();
                    CollageViewMakerSticker.curr_X = (float) (measuredWidth / 2);
                    CollageViewMakerSticker.curr_Y = (float) (measuredHeight / 2);
                    FrameActivity.this.body.setLayoutParams(new RelativeLayout.LayoutParams(measuredWidth, measuredHeight));
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) FrameActivity.this.body.getLayoutParams();
                    layoutParams.addRule(13);
                    layoutParams.addRule(3, R.id.header_layout);
                    layoutParams.addRule(2, R.id.lower_layout);
                    FrameActivity.this.body.invalidate();


                    Log.e("measuredWidth", "" + measuredWidth);
                    Log.e("measuredHeight", "" + measuredHeight);
                    Log.e("previousBitmapWidth", "" + previousBitmapWidth);
                    Log.e("previousBitmapHeight", "" + previousBitmapHeight);

                    Log.e("coordinates", "" + coordinates);
                    float aa = ((float) measuredWidth) / ww;
                    float bb = ((float) measuredHeight) / hh;

                    Log.e("measuredWidth", "" + aa);


                    FrameActivity.this.PhotoLayout.setLayoutParams(new RelativeLayout.LayoutParams(measuredWidth, measuredHeight));
                    ((RelativeLayout.LayoutParams) FrameActivity.this.PhotoLayout.getLayoutParams()).addRule(13);
                    FrameActivity.this.PhotoLayout.invalidate();
                    NewTaskforImageDownload newTaskforImageDownload = NewTaskforImageDownload.this;


                    newTaskforImageDownload.allCoordinatesLists = FrameActivity.this.findDimensionListFrontImage(aa, bb, NewTaskforImageDownload.this.coordinates);


                    FrameActivity.customFrameLayouts = new ArrayList<>();
                    FrameActivity.customFrameLayouts.clear();
                    FrameActivity.this.PhotoLayout.removeAllViews();
                    for (int i = 0; i < NewTaskforImageDownload.this.allCoordinatesLists.size(); i++) {
                        CustomFrameLayoutFrame customFrameLayoutFrame = new CustomFrameLayoutFrame(FrameActivity.this.getApplicationContext(), FrameActivity.this);
                        float f = NewTaskforImageDownload.this.allCoordinatesLists.get(i).X0;
                        float f2 = NewTaskforImageDownload.this.allCoordinatesLists.get(i).Y0;
                        float f3 = NewTaskforImageDownload.this.allCoordinatesLists.get(i).X1 - f;
                        float f4 = NewTaskforImageDownload.this.allCoordinatesLists.get(i).Y1 - f2;
                        int i2 = (int) f;
                        int i3 = (int) f2;
                        customFrameLayoutFrame.setLayoutPostion(i2, i3, f3, f4, 0.0f);
                        customFrameLayoutFrame.setFrame_id(i);
                        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int) f3, (int) f4);
                        layoutParams2.setMargins(i2, i3, 0, 0);
                        customFrameLayoutFrame.setLayoutParams(layoutParams2);
                        FrameActivity.customFrameLayouts.add(customFrameLayoutFrame);
                        customFrameLayoutFrame.uripath = FrameActivity.imagePath;
                        FrameActivity.this.PhotoLayout.addView(customFrameLayoutFrame);
                        if (i == 0) {
                            FrameActivity.this.setImage();
                        }
                    }
                    return true;
                }
            });
            FrameActivity.this.progressMain.clearAnimation();
            FrameActivity.this.progressMain.setVisibility(View.GONE);
        }
    }


    public void setImage() {
        ArrayList<CustomFrameLayoutFrame> arrayList;
        if (savedBitmap != null && (arrayList = customFrameLayouts) != null) {
            Iterator<CustomFrameLayoutFrame> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                CustomFrameLayoutFrame next = it2.next();
                if (next.getFrame_id() == 0) {
                    next.setBitmapInCollageView1(savedBitmap, next.getLeft(), next.getTop());
                    next.setImageAttach(true);
                    AdapterFilterList adapterFilterList = new AdapterFilterList(getBaseContext());
                    this.adapter = adapterFilterList;
                    this.fltrvw.setAdapter((ListAdapter) adapterFilterList);
                }
            }
            imageCount = 1;
        }
    }

    ArrayList<Coordinates> findDimensionListFrontImage(float f, float f2, String str) {
        ArrayList<Coordinates> arrayList = new ArrayList<>();
        arrayList.clear();
        List<String> asList = Arrays.asList(str.split(","));
        this.list = asList;
        asList.size();
        for (int i = 0; i < this.list.size(); i++) {
            List asList2 = Arrays.asList(this.list.get(i).split(":"));
            Log.i("arrays", this.list.get(i));
            Coordinates coordinates = new Coordinates();
            coordinates.X0 = Float.parseFloat((String) asList2.get(0)) * f;
            coordinates.Y0 = Float.parseFloat((String) asList2.get(1)) * f2;
            coordinates.X1 = Float.parseFloat((String) asList2.get(2)) * f;
            coordinates.Y1 = Float.parseFloat((String) asList2.get(3)) * f2;
            arrayList.add(coordinates);
        }
        return arrayList;
    }

    public void showTopBar(int i) {
        Log.e("abcd", i + "");
        selected_frame_id = i;
    }

    public void showViews(boolean z) {
        Iterator<CustomFrameLayoutFrame> it2 = customFrameLayouts.iterator();
        while (it2.hasNext()) {
            CustomFrameLayoutFrame next = it2.next();
            if (next.getFrame_id() != selected_frame_id) {
                next.isTouch = false;
                next.hideTick();
            }
            this.ll_apply.setVisibility(View.GONE);
            this.sb_color.setVisibility(View.INVISIBLE);
            this.ll_save.setVisibility(View.VISIBLE);
        }
        if (z) {
            this.r_tools.setVisibility(View.VISIBLE);
            this.iv_delete.setVisibility(View.VISIBLE);
            return;
        }
        this.r_tools.setVisibility(View.INVISIBLE);
        this.iv_delete.setVisibility(View.INVISIBLE);
    }

    public void OpenImportDialog(int i) {
        selected_frame_id = i;
        removeToolsLayout();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setFlags(1024, 1024);
        dialog.setContentView(R.layout.import_home);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setLayout(-1, -1);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        window.setAttributes(layoutParams);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i2, KeyEvent keyEvent) {
                if (i2 != 4) {
                    return true;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        FrameActivity.selected_frame_id = -1;
                    }
                }, 500);
                return true;
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ImagePicker.pickCameraImage(FrameActivity.this);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.galley)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ImagePicker.pickGalleryImage(FrameActivity.this, "Select your image:");
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == 111) {
            finish();
        }
        Bitmap imageFromResult = ImagePicker.getImageFromResult(getApplicationContext(), i, i2, intent);
        if (imageFromResult != null) {
            savedBitmap = imageFromResult.copy(Bitmap.Config.ARGB_8888, true);
            imageCount++;
            UserObject.setForgroundBitmap(imageFromResult);
            UserObject.setBackgroundBitmap(imageFromResult);
            new ImageCompressionAsyncTaskNew(false, selected_frame_id).execute("sss");
        }
    }

    void removeToolsLayout() {
        this.r_tools.setVisibility(View.INVISIBLE);
        this.iv_delete.setVisibility(View.INVISIBLE);
        Iterator<CustomFrameLayoutFrame> it2 = customFrameLayouts.iterator();
        while (it2.hasNext()) {
            it2.next().hideTick();
        }
    }



    public static class ImageCompressionAsyncTaskNew extends AsyncTask<String, Void, Bitmap> {
        public ImageCompressionAsyncTaskNew(boolean z, int i) {
        }


        public Bitmap doInBackground(String... strArr) {
            return UserObject.getForgroundBitmap();
        }


        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && FrameActivity.customFrameLayouts != null) {
                Iterator<CustomFrameLayoutFrame> it2 = FrameActivity.customFrameLayouts.iterator();
                while (it2.hasNext()) {
                    CustomFrameLayoutFrame next = it2.next();
                    if (next.getFrame_id() == FrameActivity.selected_frame_id) {
                        next.setBitmapInCollageView1(bitmap, next.getLeft(), next.getTop());
                        next.setImageAttach(true);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_delete) {
            this.sb_color.setVisibility(View.INVISIBLE);
            this.iv_delete.setVisibility(View.INVISIBLE);
            this.fltrvw.setVisibility(View.GONE);
            this.iv_filterClose.setVisibility(View.GONE);
            this.r_tools.setVisibility(View.INVISIBLE);
            Log.e("abcd", "" + selected_frame_id);
            Iterator<CustomFrameLayoutFrame> it2 = customFrameLayouts.iterator();
            while (it2.hasNext()) {
                CustomFrameLayoutFrame next = it2.next();
                if (next.getFrame_id() == selected_frame_id) {
                    next.refreshView();
                }
            }
            selected_frame_id = -1;
            imageCount--;
        } else if (id != R.id.ll_save) {
            switch (id) {
                case R.id.ll_apply:
                    if (this.collage_sticker.getVisibility() == View.VISIBLE) {
                        this.r_framewithstrk.setDrawingCacheEnabled(true);
                        this.r_framewithstrk.setDrawingCacheQuality(1048576);
                        Bitmap copy = this.r_framewithstrk.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
                        this.r_framewithstrk.setDrawingCacheEnabled(false);
                        this.ivFrame.setImageBitmap(copy);
                        this.currentOrgnBmp = copy.copy(Bitmap.Config.ARGB_8888, true);
                        this.collage_sticker.mImages.clear();
                        stickerCaller = false;
                        this.collage_sticker.setVisibility(View.GONE);
                        this.sb_color.setProgress(180);
                    } else if (this.sb_color.getVisibility() == View.VISIBLE) {
                        this.sb_color.setVisibility(View.INVISIBLE);
                        Bitmap bitmap2 = this.currentChangeBmp;
                        if (bitmap2 != null) {
                            this.currentOrgnBmp = bitmap2.copy(Bitmap.Config.ARGB_8888, true);
                        }
                    }
                    this.r_tools.setVisibility(View.VISIBLE);
                    this.iv_delete.setVisibility(View.VISIBLE);
                    this.ll_apply.setVisibility(View.GONE);
                    this.fltrvw.setVisibility(View.GONE);
                    this.iv_filterClose.setVisibility(View.GONE);
                    this.ll_save.setVisibility(View.VISIBLE);
                    return;
                case R.id.ll_back:
                    if (this.collage_sticker.getVisibility() == View.VISIBLE) {
                        this.r_tools.setVisibility(View.VISIBLE);
                        this.iv_delete.setVisibility(View.VISIBLE);
                        this.ll_apply.setVisibility(View.GONE);
                        this.fltrvw.setVisibility(View.GONE);
                        this.iv_filterClose.setVisibility(View.GONE);
                        this.ll_save.setVisibility(View.VISIBLE);
                        this.collage_sticker.mImages.clear();
                        this.collage_sticker.setVisibility(View.GONE);
                    } else {
                        new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.duwte)).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FrameActivity.this.sb_color.setVisibility(View.INVISIBLE);
                                FrameActivity.this.fltrvw.setVisibility(View.INVISIBLE);
                                FrameActivity.this.iv_filterClose.setVisibility(View.GONE);
                                FrameActivity.this.finish();
                            }
                        }).setNegativeButton(getResources().getString(R.string.no), (DialogInterface.OnClickListener) null).show();
                    }
                    frameCaller = false;
                    stickerCaller = false;
                    return;
                default:
                    switch (id) {
                        case R.id.r_anticlock_rotate:
                            this.sb_color.setVisibility(View.INVISIBLE);
                            this.fltrvw.setVisibility(View.GONE);
                            this.iv_filterClose.setVisibility(View.GONE);
                            Iterator<CustomFrameLayoutFrame> it3 = customFrameLayouts.iterator();
                            while (it3.hasNext()) {
                                CustomFrameLayoutFrame next2 = it3.next();
                                if (next2.getFrame_id() == selected_frame_id) {
                                    next2.rotate5LeftImage();
                                }
                            }
                            return;
                        case R.id.r_clock_rotate:
                            this.sb_color.setVisibility(View.INVISIBLE);
                            this.fltrvw.setVisibility(View.GONE);
                            this.iv_filterClose.setVisibility(View.GONE);
                            Iterator<CustomFrameLayoutFrame> it4 = customFrameLayouts.iterator();
                            while (it4.hasNext()) {
                                CustomFrameLayoutFrame next3 = it4.next();
                                if (next3.getFrame_id() == selected_frame_id) {
                                    next3.rotate5RightImage();
                                }
                            }
                            return;
                        case R.id.r_color:
                            this.sb_color.setVisibility(View.VISIBLE);
                            this.r_tools.setVisibility(View.INVISIBLE);
                            this.iv_delete.setVisibility(View.INVISIBLE);
                            this.ll_apply.setVisibility(View.VISIBLE);
                            this.ll_save.setVisibility(View.GONE);
                            this.fltrvw.setVisibility(View.GONE);
                            this.iv_filterClose.setVisibility(View.GONE);
                            return;
                        case R.id.r_edit:
                            this.sb_color.setVisibility(View.INVISIBLE);
                            Iterator<CustomFrameLayoutFrame> it5 = customFrameLayouts.iterator();
                            while (it5.hasNext()) {
                                it5.next().getFrame_id();
                            }
                            return;
                        case R.id.r_flip:
                            this.sb_color.setVisibility(View.INVISIBLE);
                            this.fltrvw.setVisibility(View.GONE);
                            this.iv_filterClose.setVisibility(View.GONE);
                            Iterator<CustomFrameLayoutFrame> it6 = customFrameLayouts.iterator();
                            while (it6.hasNext()) {
                                CustomFrameLayoutFrame next4 = it6.next();
                                if (next4.getFrame_id() == selected_frame_id) {
                                    next4.flipImage();
                                }
                            }
                            return;
                        default:
                            return;
                    }
            }
        } else {
            if ((selected_frame_id >= 0 || savedBitmap != null) && imageCount == customFrameLayouts.size()) {
                if (savedBitmap != null) {
                    selected_frame_id = 0;
                }
                Iterator<CustomFrameLayoutFrame> it7 = customFrameLayouts.iterator();
                while (true) {
                    if (!it7.hasNext()) {
                        break;
                    }
                    CustomFrameLayoutFrame next5 = it7.next();
                    if (next5.getFrame_id() == selected_frame_id) {
                        next5.isTouch = false;
                        next5.hideTick();
                        showViews(next5.isTouch);
                        this.body.setDrawingCacheEnabled(true);
                        this.body.setDrawingCacheQuality(1048576);
                        savepicbmp = this.body.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
                        this.body.setDrawingCacheEnabled(false);
                        startActivityForResult(new Intent(getApplicationContext(), SaveActivity.class), 222);
                        break;
                    }
                }
            } else {
                Toast makeText = Toast.makeText(getApplicationContext(), "Please Insert Image!", Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
            this.fltrvw.setVisibility(View.GONE);
            this.iv_filterClose.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pathSticker != null) {
            Log.e("abcd", "pathStickerOnResume");
            this.dottedBar.setVisibility(View.VISIBLE);
            this.collage_sticker.setVisibility(View.VISIBLE);
            Picasso.get().load(pathSticker).placeholder(R.drawable.icon200).into(this.targetSticker);
            pathSticker = null;
        } else if (pathFrame != null) {
            Log.e("abcd", "pathFrameOnResume");
            this.r_tools.setVisibility(View.INVISIBLE);
            this.iv_delete.setVisibility(View.INVISIBLE);
            new DownloadSticker(link, thumbnailURL).execute(new Void[0]);
            pathFrame = null;
            frameCaller = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        frameCaller = false;
        stickerCaller = false;
        if (this.collage_sticker.getVisibility() == View.VISIBLE) {
            this.r_tools.setVisibility(View.VISIBLE);
            this.iv_delete.setVisibility(View.VISIBLE);
            this.ll_apply.setVisibility(View.GONE);
            this.ll_save.setVisibility(View.VISIBLE);
            this.collage_sticker.mImages.clear();
            this.collage_sticker.setVisibility(View.GONE);
            return;
        }
        new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.duwte)).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FrameActivity.this.sb_color.setVisibility(View.INVISIBLE);
                FrameActivity.this.fltrvw.setVisibility(View.INVISIBLE);
                FrameActivity.this.iv_filterClose.setVisibility(View.GONE);
                FrameActivity.super.onBackPressed();
            }
        }).setNegativeButton(getResources().getString(R.string.no), (DialogInterface.OnClickListener) null).show();
    }

    void setFrameFromSDCard(String str) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Frame");
        if (!file.exists()) {
            Toast.makeText(frameActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
        String str2 = file.getAbsolutePath() + "/Frame_Full" + str + FileUtils.IMAGE_EXTENSION_PNG;
        Log.d("abcd", "" + BitmapFactory.decodeFile(str2));
        Log.d("abcd", "Coordinates Value is(ID):" + currentCoordinate);
        this.body.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.body.getLayoutParams();
        layoutParams.addRule(13);
        layoutParams.addRule(3, R.id.header_layout);
        layoutParams.addRule(2, R.id.lower_layout);
        this.body.invalidate();
        this.ratio = false;
        new NewTaskforImageDownload(BitmapFactory.decodeFile(str2), currentCoordinate).execute(new String[0]);
    }

    void setFrameFromSDCard(Frame_Photo frame_Photo) {
        try {
            String imageId = frame_Photo.getImageId();
            String cordinate2 = frame_Photo.getCordinate();
            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Frame");
            if (!file.exists()) {
                Toast.makeText(frameActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
            Log.d("abcd", "Coordinates Value is(photos)::" + cordinate2);
            new NewTaskforImageDownload(BitmapFactory.decodeFile(file.getAbsolutePath() + "/Frame_Full" + imageId + FileUtils.IMAGE_EXTENSION_PNG), cordinate2).execute(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class DownloadSticker extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;
        Bitmap bitmapThumb;
        String link;
        String linkThumb;

        public DownloadSticker(String str, String str2) {
            this.link = str;
            this.linkThumb = str2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FrameActivity.this.dottedBar.setVisibility(View.VISIBLE);
        }


        public Void doInBackground(Void... voidArr) {
            this.bitmap = FrameActivity.this.getBitmapFromURL(this.link);
            this.bitmapThumb = FrameActivity.this.getBitmapFromURL(this.linkThumb);
            return null;
        }


        public void onPostExecute(Void r4) {
            super.onPostExecute(r4);
            FrameActivity.this.dottedBar.setVisibility(View.INVISIBLE);
            if (this.bitmap != null) {
                new SaveImageSD(this.bitmap, this.bitmapThumb).execute(new Void[0]);
            }
        }
    }

    Bitmap getBitmapFromURL(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return Utils.decodeSampledBitmapFromStream(inputStream, displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public class SaveImageSD extends AsyncTask<Void, Void, Void> {
        Bitmap bmp;
        Bitmap bmpThumb;
        String fileName;
        String fileNameThumb;
        boolean isSaved = false;

        public SaveImageSD(Bitmap bitmap, Bitmap bitmap2) {
            this.bmp = null;
            this.bmpThumb = null;
            this.bmp = bitmap;
            this.bmpThumb = bitmap2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FrameActivity.this.dottedBar.setVisibility(View.VISIBLE);
        }










        public Void doInBackground(Void... voidArr) {
            Exception e;
            FileOutputStream fileOutputStream = null;
            FileOutputStream fileOutputStream2 = null;
            FileOutputStream fileOutputStream3 = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Frame");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(file.getAbsolutePath() + "/" + "Frame_Full" + FrameActivity.ID + FileUtils.IMAGE_EXTENSION_PNG);
                this.fileName = file.getAbsolutePath() + "/" + "Frame_Full" + FrameActivity.ID + FileUtils.IMAGE_EXTENSION_PNG;
                new File(file.getAbsolutePath() + "/Frame_Thumb" + FrameActivity.ID + FileUtils.IMAGE_EXTENSION_PNG);
                this.fileNameThumb = file.getAbsolutePath() + "/Frame_Thumb" + FrameActivity.ID + FileUtils.IMAGE_EXTENSION_PNG;
                Log.d("abcd", file2.getAbsolutePath());
                if (file2.exists()) {
                    FrameActivity.isPicsaved = true;
                    return null;
                }
            } catch (Exception unused) {
            }
            try {
                try {
                    fileOutputStream = new FileOutputStream(this.fileName);
                } catch (Throwable th2) {

                }
            } catch (Exception e2) {
                fileOutputStream2 = null;
            } catch (Throwable th3) {

            }
            try {
                this.isSaved = this.bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream2 = new FileOutputStream(this.fileNameThumb);
                try {
                    this.isSaved = this.bmpThumb.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream2);
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    fileOutputStream2.close();
                }
            } catch (Exception e4) {
                e = e4;
                fileOutputStream2 = fileOutputStream;
            } catch (Throwable th4) {
                fileOutputStream3 = fileOutputStream;
                try {
                    fileOutputStream3.close();
                } catch (Throwable unused2) {
                }
            }
            try {
                fileOutputStream2.close();
            } catch (Throwable unused3) {
                return null;
            }
            return null;
        }


        public void onPostExecute(Void r5) {
            super.onPostExecute(r5);
            Log.e("abcd", "pathFrameOnResume");
            if (FrameActivity.isPicsaved) {
                FrameActivity.this.setFrameFromSDCard(FrameActivity.ID);
                Toast.makeText(FrameActivity.this.getApplicationContext(), "Already Downloaded", Toast.LENGTH_SHORT).show();
                FrameActivity.isPicsaved = false;
                FrameActivity.this.dottedBar.setVisibility(View.INVISIBLE);
                return;
            }
            FrameActivity.this._feed.clear();
            FrameActivity frameActivity = FrameActivity.this;
            frameActivity._feed = frameActivity.parser.getFRAMEThumbnails(FrameActivity.this);
            FrameActivity.this.mHorizontalListAdapter.selected_mark_pos = 0;
            FrameActivity.this.mHorizontalListAdapter.notifyDataSetChanged();
            FrameActivity.this.dottedBar.setVisibility(View.INVISIBLE);
            FrameActivity.this.setFrameFromSDCard(FrameActivity.ID);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Iterator<CustomFrameLayoutFrame> it2 = customFrameLayouts.iterator();
        while (it2.hasNext()) {
            CustomFrameLayoutFrame next = it2.next();
            if (next.getFrame_id() == selected_frame_id) {
                next.imageViewTouch.onTouchEvent(motionEvent);
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean z) {
        this.ivFrame.setColorFilter(ColorMatrixEffects.adjustColor(7, (float) (this.sb_color.getProgress() - 180)));
    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
        Bitmap bitmap2 = ColorMatrixEffects.getBitmap(this.currentOrgnBmp, (float) (this.sb_color.getProgress() - 180), 7);
        this.currentChangeBmp = bitmap2;
        this.ivFrame.setImageBitmap(bitmap2);
        this.ivFrame.invalidate();
    }
}
