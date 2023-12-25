package com.demo.example.PictureFrames;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.demo.example.R;

import com.demo.example.helper.UserObject;
import com.demo.example.helper.Util;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

import java.io.File;


public class CustomFrameLayoutFrame extends FrameLayout {
    public float Ax;
    public float Ay;
    public float Bx;
    public float By;
    public float Cx;
    public float Cy;
    public float Dx;
    public float Dy;
    private boolean ImageAttach;
    int REQUESTED_CODE_AVIARY;
    Activity activity;
    FrameLayout add_photo_layout;
    public float angle;
    private Bitmap bbb;
    Context context;
    ImageView copyImage;
    int firstHeight;
    int firstWidth;
    private int frame_id;
    private float height;
    int height1;
    public ImageView imageView;
    ImageViewTouch imageViewTouch;
    private String image_path;
    boolean isTouch;
    ImageView iv_tick;
    private int rotation;
    float scaleX;
    float scaleY;
    private Bitmap scaled_bitmap;
    String temporaryImageUri;
    public ImageView temporaryimage;
    String uripath;
    private float width;

    public CustomFrameLayoutFrame(Context context, final FrameActivity frameActivity) {
        super(context);
        this.firstWidth = -1;
        this.firstHeight = -1;
        this.isTouch = false;
        this.rotation = 0;
        this.ImageAttach = false;
        this.REQUESTED_CODE_AVIARY = 10005;
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.collage_custom_framelayoutframe, (ViewGroup) this, true);
        this.context = context;
        this.activity = frameActivity;
        this.temporaryimage = (ImageView) findViewById(R.id.temporaryimage);
        this.add_photo_layout = (FrameLayout) findViewById(R.id.add_photo_layout);
        this.imageView = (ImageView) findViewById(R.id.image_add_photo);
        this.iv_tick = (ImageView) findViewById(R.id.iv_tick);
        this.copyImage = (ImageView) findViewById(R.id.copyimage);
        ImageViewTouch imageViewTouch = (ImageViewTouch) findViewById(R.id.image);
        this.imageViewTouch = imageViewTouch;
        imageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.NONE);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.add_photo_layout.setOnClickListener(new OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (!CustomFrameLayoutFrame.this.isImageAttach()) {
                    frameActivity.OpenImportDialog(CustomFrameLayoutFrame.this.frame_id);
                }
            }
        });
        this.imageViewTouch.setOnTouchListener(new OnTouchListener() { 
            @Override 
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    if (!CustomFrameLayoutFrame.this.isTouch) {
                        frameActivity.showTopBar(CustomFrameLayoutFrame.this.frame_id);
                        CustomFrameLayoutFrame.this.showTick();
                        CustomFrameLayoutFrame.this.isTouch = true;
                    } else {
                        CustomFrameLayoutFrame.this.isTouch = false;
                        CustomFrameLayoutFrame.this.hideTick();
                    }
                    frameActivity.showViews(CustomFrameLayoutFrame.this.isTouch);
                }
                return false;
            }
        });
        this.imageViewTouch.setOnLongClickListener(new OnLongClickListener() { 
            @Override 
            public boolean onLongClick(View view) {
                FrameImageDetails.currentImageView = CustomFrameLayoutFrame.this.imageViewTouch;
                FrameImageDetails.currentDrawable = CustomFrameLayoutFrame.this.imageViewTouch.getDrawable();
                FrameImageDetails.copyImageView = CustomFrameLayoutFrame.this.copyImage;
                FrameImageDetails.copyBitmap = ((BitmapDrawable) CustomFrameLayoutFrame.this.copyImage.getDrawable()).getBitmap();
                FrameImageDetails.temporaryimage = CustomFrameLayoutFrame.this.temporaryimage;
                FrameImageDetails.temporaryimagebitmap = ((BitmapDrawable) CustomFrameLayoutFrame.this.temporaryimage.getDrawable()).getBitmap();
                FrameImageDetails.customFrameLayoutFrame = CustomFrameLayoutFrame.this;
                view.startDrag(null, new DragShadowBuilder(CustomFrameLayoutFrame.this.imageViewTouch), view, 0);
                return false;
            }
        });
        this.imageViewTouch.setOnDragListener(new OnDragListener() { 
            @Override 
            public boolean onDrag(View view, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                if (action != 1) {
                    if (action == 3) {
                        Log.d("abcd", "Dropped");
                        FrameImageDetails.currentImageView.setImageDrawable(CustomFrameLayoutFrame.this.imageViewTouch.getDrawable());
                        CustomFrameLayoutFrame.this.imageViewTouch.setImageDrawable(FrameImageDetails.currentDrawable);
                        FrameImageDetails.copyImageView.setImageBitmap(((BitmapDrawable) CustomFrameLayoutFrame.this.copyImage.getDrawable()).getBitmap());
                        FrameImageDetails.temporaryimage.setImageBitmap(((BitmapDrawable) CustomFrameLayoutFrame.this.temporaryimage.getDrawable()).getBitmap());
                        CustomFrameLayoutFrame.this.copyImage.setImageBitmap(FrameImageDetails.copyBitmap);
                        CustomFrameLayoutFrame.this.temporaryimage.setImageBitmap(FrameImageDetails.temporaryimagebitmap);
                        CustomFrameLayoutFrame customFrameLayoutFrame = CustomFrameLayoutFrame.this;
                        customFrameLayoutFrame.temporaryImageUri = customFrameLayoutFrame.uripath;
                        CustomFrameLayoutFrame.this.uripath = FrameImageDetails.customFrameLayoutFrame.uripath;
                        FrameImageDetails.customFrameLayoutFrame.uripath = CustomFrameLayoutFrame.this.temporaryImageUri;
                    } else if (action != 4) {
                        if (action == 5) {
                            Log.d("abcd", "Drag event entered into " + view.toString());
                        } else if (action == 6) {
                            Log.d("abcd", "Drag event exited from " + view.toString());
                        }
                    }
                    Log.d("abcd", "Drag ended");
                } else {
                    Log.d("abcd", "Drag event started");
                }
                return true;
            }
        });
    }

    public void hideTick() {
        this.iv_tick.setVisibility(View.GONE);
    }

    public void showTick() {
        this.iv_tick.setVisibility(View.VISIBLE);
    }

    public CustomFrameLayoutFrame(Context context) {
        super(context);
        this.firstWidth = -1;
        this.firstHeight = -1;
        this.isTouch = false;
        this.rotation = 0;
        this.ImageAttach = false;
        this.REQUESTED_CODE_AVIARY = 10005;
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.collage_custom_framelayoutframe, (ViewGroup) this, true);
        this.add_photo_layout = (FrameLayout) findViewById(R.id.add_photo_layout);
        this.imageView = (ImageView) findViewById(R.id.image_add_photo);
        this.iv_tick = (ImageView) findViewById(R.id.iv_tick);
        this.imageViewTouch = (ImageViewTouch) findViewById(R.id.image);
        this.copyImage = (ImageView) findViewById(R.id.copyimage);
        this.temporaryimage = (ImageView) findViewById(R.id.temporaryimage);
    }

    public boolean isImageAttach() {
        return this.ImageAttach;
    }

    public void setImageAttach(boolean z) {
        this.ImageAttach = z;
        invalidate();
    }

    public int getFrame_id() {
        return this.frame_id;
    }

    public void setFrame_id(int i) {
        this.frame_id = i;
    }

    public void setLayoutPostion(int i, int i2, float f, float f2, float f3) {
        this.width = f;
        this.height = f2;
        this.angle = f3;
    }

    public void setPoligonPostion(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        this.Ax = f;
        this.Ay = f2;
        this.Bx = f3;
        this.By = f4;
        this.Cx = f5;
        this.Cy = f6;
        this.Dx = f7;
        this.Dy = f8;
    }

    public void setBitmapInCollageView1(Bitmap bitmap, int i, int i2) {
        getBitmapOffset(this, true);
        this.imageView.setVisibility(View.GONE);
        this.imageViewTouch.setVisibility(View.VISIBLE);
        this.scaleX = this.width / ((float) bitmap.getWidth());
        this.scaleY = this.height / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        float f = this.scaleX;
        float f2 = this.scaleY;
        if (f > f2) {
            matrix.setScale(f, f);
        } else {
            matrix.setScale(f2, f2);
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.scaled_bitmap = createBitmap;
        this.imageViewTouch.setImageBitmap(createBitmap);
        this.copyImage.setImageBitmap(this.scaled_bitmap);
        this.temporaryimage.setImageBitmap(this.scaled_bitmap);
    }

    public void suffle_BitmapInCollageView(Bitmap bitmap, int i, int i2) {
        getBitmapOffset(this, true);
        this.imageView.setVisibility(View.GONE);
        this.imageViewTouch.setVisibility(View.VISIBLE);
        this.scaleX = this.width / ((float) bitmap.getWidth());
        this.scaleY = this.height / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        float f = this.scaleX;
        float f2 = this.scaleY;
        if (f > f2) {
            matrix.setScale(f, f);
        } else {
            matrix.setScale(f2, f2);
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.scaled_bitmap = createBitmap;
        this.imageViewTouch.setImageBitmap(createBitmap);
    }

    public static int[] getBitmapOffset(View view, Boolean bool) {
        int[] iArr = new int[2];
        float[] fArr = new float[9];
        iArr[0] = (int) fArr[5];
        iArr[1] = (int) fArr[2];
        if (bool.booleanValue()) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
            int paddingTop = view.getPaddingTop();
            int paddingLeft = view.getPaddingLeft();
            iArr[0] = iArr[0] + paddingTop + marginLayoutParams.topMargin;
            iArr[1] = iArr[1] + paddingLeft + marginLayoutParams.leftMargin;
        }
        return iArr;
    }

    public void refreshView() {
        this.imageViewTouch.setVisibility(View.GONE);
        this.imageView.setVisibility(View.VISIBLE);
        hideTick();
        this.isTouch = false;
        this.ImageAttach = false;
        invalidate();
    }

    public void flipImage() {
        this.scaled_bitmap = ((BitmapDrawable) this.copyImage.getDrawable()).getBitmap();
        this.bbb = ((BitmapDrawable) this.temporaryimage.getDrawable()).getBitmap();
        float width = this.width / ((float) this.scaled_bitmap.getWidth());
        float height = this.height / ((float) this.scaled_bitmap.getHeight());
        float width2 = this.width / ((float) this.bbb.getWidth());
        float height2 = this.height / ((float) this.bbb.getHeight());
        Matrix matrix = new Matrix();
        if (width > height) {
            matrix.setScale(width, width);
        } else {
            matrix.setScale(height, height);
        }
        Bitmap flip = Util.flip(this.scaled_bitmap, 2);
        this.scaled_bitmap = flip;
        this.scaled_bitmap = Bitmap.createBitmap(flip, 0, 0, flip.getWidth(), this.scaled_bitmap.getHeight(), matrix, true);
        if (width2 > height2) {
            matrix.setScale(width2, width2);
        } else {
            matrix.setScale(height2, height2);
        }
        Bitmap flip2 = Util.flip(this.bbb, 2);
        this.bbb = flip2;
        this.bbb = Bitmap.createBitmap(flip2, 0, 0, flip2.getWidth(), this.bbb.getHeight(), matrix, true);
        this.imageViewTouch.setImageBitmap(this.scaled_bitmap);
        this.copyImage.setImageBitmap(this.scaled_bitmap);
        this.temporaryimage.setImageBitmap(this.bbb);
        this.imageViewTouch.invalidate();
    }

    public void rotateImage() {
        if (this.rotation == 360) {
            this.rotation = 0;
        }
        this.rotation += 90;
        float width = this.width / ((float) this.scaled_bitmap.getWidth());
        float height = this.height / ((float) this.scaled_bitmap.getHeight());
        Matrix matrix = new Matrix();
        if (width > height) {
            matrix.setScale(width, width);
        } else {
            matrix.setScale(height, height);
        }
        matrix.setRotate(90.0f);
        Bitmap bitmap = this.scaled_bitmap;
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), this.scaled_bitmap.getHeight(), matrix, true);
        this.scaled_bitmap = createBitmap;
        this.imageViewTouch.setImageBitmap(createBitmap);
        this.copyImage.setImageBitmap(this.scaled_bitmap);
        this.temporaryimage.setImageBitmap(this.scaled_bitmap);
    }

    public void rotate5RightImage() {
        float f = this.angle + 0.3f;
        this.angle = f;
        this.imageViewTouch.setRotation(f);
        this.copyImage.setRotation(this.angle);
    }

    public void rotate5LeftImage() {
        float f = this.angle - 0.3f;
        this.angle = f;
        this.imageViewTouch.setRotation(f);
        this.copyImage.setRotation(this.angle);
    }

    public void color(int i) {
        float f = (float) (i - 180);
        this.imageViewTouch.setColorFilter(ColorMatrixEffects.adjustColor(7, f));
        this.copyImage.setColorFilter(ColorMatrixEffects.adjustColor(7, f));
    }

    public void setBitmapFromAviaryPath() {
        new ImageCompressionAsyncTask().execute(new String[0]);
    }

    
    class ImageCompressionAsyncTask extends AsyncTask<String, Void, Bitmap> {
        ImageCompressionAsyncTask() {
        }

        
        public Bitmap doInBackground(String... strArr) {
            Log.i("I am", "true" + CustomFrameLayoutFrame.this.image_path);
            CustomFrameLayoutFrame.this.image_path = UserObject.getFileLocation();
            CustomFrameLayoutFrame customFrameLayoutFrame = CustomFrameLayoutFrame.this;
            return customFrameLayoutFrame.compressImage(customFrameLayoutFrame.image_path);
        }

        
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                CustomFrameLayoutFrame.this.scaled_bitmap = bitmap;
                float width = CustomFrameLayoutFrame.this.width / ((float) CustomFrameLayoutFrame.this.scaled_bitmap.getWidth());
                float height = CustomFrameLayoutFrame.this.height / ((float) CustomFrameLayoutFrame.this.scaled_bitmap.getHeight());
                Matrix matrix = new Matrix();
                if (width > height) {
                    matrix.setScale(width, width);
                } else {
                    matrix.setScale(height, height);
                }
                CustomFrameLayoutFrame customFrameLayoutFrame = CustomFrameLayoutFrame.this;
                customFrameLayoutFrame.scaled_bitmap = Bitmap.createBitmap(customFrameLayoutFrame.scaled_bitmap, 0, 0, CustomFrameLayoutFrame.this.scaled_bitmap.getWidth(), CustomFrameLayoutFrame.this.scaled_bitmap.getHeight(), matrix, true);
                CustomFrameLayoutFrame.this.imageViewTouch.setImageBitmap(CustomFrameLayoutFrame.this.scaled_bitmap);
                CustomFrameLayoutFrame.this.copyImage.setImageBitmap(CustomFrameLayoutFrame.this.scaled_bitmap);
                CustomFrameLayoutFrame.this.temporaryimage.setImageBitmap(CustomFrameLayoutFrame.this.scaled_bitmap);
                CustomFrameLayoutFrame customFrameLayoutFrame2 = CustomFrameLayoutFrame.this;
                customFrameLayoutFrame2.deleteFileNoThrow(customFrameLayoutFrame2.image_path);
            }
        }
    }

    
    public boolean deleteFileNoThrow(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                return file.delete();
            }
        } catch (NullPointerException unused) {
        }
        return false;
    }

    public Bitmap compressImage(String str) {
        String realPathFromURI = getRealPathFromURI(str);
        if (realPathFromURI != null) {
            str = realPathFromURI;
        }
        new BitmapFactory.Options().inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(str);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        try {
            new Matrix().setScale(this.scaleX, this.scaleY);
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
        }
        return bitmap;
    }

    private String getRealPathFromURI(String str) {
        Uri parse = Uri.parse(str);
        Cursor query = getContext().getContentResolver().query(parse, null, null, null, null);
        if (query == null) {
            return parse.getPath();
        }
        query.moveToFirst();
        return query.getString(query.getColumnIndex("_data"));
    }

    public Bitmap getselBitmap() {
        return ((BitmapDrawable) this.temporaryimage.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
    }

    public void changeSelectedImageWithEffect(Uri uri) {
        this.imageViewTouch.setImageURI(uri);
        this.copyImage.setImageURI(uri);
    }

    @Override 
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.firstWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        this.firstHeight = measuredHeight;
        setMeasuredDimension(this.firstWidth, measuredHeight);
    }
}
