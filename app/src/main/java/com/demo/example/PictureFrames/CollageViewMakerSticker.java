package com.demo.example.PictureFrames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.demo.example.helper.MultiTouchController;

import com.demo.example.helper.HorizontalListView;

import java.util.ArrayList;


public class CollageViewMakerSticker extends View implements MultiTouchController.MultiTouchObjectCanvas<CollageViewMakerSticker.Img> {
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    private static final int UI_MODE_ROTATE = 1;
    public static float curr_X = 100.0f;
    public static float curr_Y = 100.0f;
    Context context;
    private MultiTouchController.PointInfo currTouchPoint;
    HorizontalListView horizontalListView;
    public ArrayList<Img> mImages;
    private boolean mShowDebugInfo;
    private int mUIMode;
    private MultiTouchController<Img> multiTouchController;
    private int position;
    boolean stickerClicked;

    public CollageViewMakerSticker(Context context) {
        this(context, null);
    }

    public CollageViewMakerSticker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CollageViewMakerSticker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.position = -1;
        this.stickerClicked = false;
        this.mImages = new ArrayList<>();
        this.multiTouchController = new MultiTouchController<>(this);
        this.currTouchPoint = new MultiTouchController.PointInfo();
        this.mShowDebugInfo = true;
        this.mUIMode = 1;
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(0);
        this.context = context;
    }

    public int loadImages(Context context, Bitmap bitmap) {
        this.mImages.add(new Img(bitmap));
        int size = this.mImages.size();
        Log.e("LoAD SIZE", "" + size);
        for (int i = 0; i < size; i++) {
            this.mImages.get(size - 1).load(bitmap);
        }
        invalidate();
        return size;
    }

    public int replaceImages(Context context, Bitmap bitmap) {
        ArrayList<Img> arrayList = this.mImages;
        arrayList.set(arrayList.size() - 1, new Img(bitmap));
        int size = this.mImages.size();
        Log.e("LoAD SIZE", "" + size);
        for (int i = 0; i < size; i++) {
            this.mImages.get(size - 1).load(bitmap);
        }
        invalidate();
        return size;
    }

    public int loadImagesForText(Context context, Bitmap bitmap) {
        this.mImages.add(new Img(bitmap));
        int size = this.mImages.size();
        Log.e("LoAD SIZE", "" + size);
        for (int i = 0; i < size; i++) {
            this.mImages.get(size - 1).load2(bitmap);
        }
        invalidate();
        return size;
    }

    public int deleteSelectedImage() {
        try {
            int i = this.position;
            if (i != -1) {
                this.mImages.remove(i);
                this.position = -1;
            }
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Nothing to Delete", Toast.LENGTH_SHORT).show();
        }
        return this.mImages.size();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mImages.size();
        Log.e("SIZE", "" + size);
        for (int i = 0; i < size; i++) {
            this.mImages.get(i).draw(canvas);
        }
        if (this.mShowDebugInfo) {
            drawMultitouchDebugMarks(canvas);
        }
    }

    public void trackballClicked() {
        this.mUIMode = (this.mUIMode + 1) % 3;
        invalidate();
    }

    private void drawMultitouchDebugMarks(Canvas canvas) {
        this.currTouchPoint.isDown();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.multiTouchController.onTouchEvent(motionEvent);
    }

    
    @Override 
    public Img getDraggableObjectAtPoint(MultiTouchController.PointInfo pointInfo) {
        float x = pointInfo.getX();
        float y = pointInfo.getY();
        for (int size = this.mImages.size() - 1; size >= 0; size--) {
            Img img = this.mImages.get(size);
            if (img.containsPoint(x, y)) {
                this.position = size;
                return img;
            }
            this.position = -1;
        }
        return null;
    }

    public void selectObject(Img img, MultiTouchController.PointInfo pointInfo) {
        this.currTouchPoint.set(pointInfo);
        invalidate();
    }

    public void getPositionAndScale(Img img, MultiTouchController.PositionAndScale positionAndScale) {
        positionAndScale.set(img.getCenterX(), img.getCenterY(), (this.mUIMode & 2) == 0, (img.getScaleX() + img.getScaleY()) / 2.0f, (this.mUIMode & 2) != 0, img.getScaleX(), img.getScaleY(), (this.mUIMode & 1) != 0, img.getAngle());
    }

    public boolean setPositionAndScale(Img img, MultiTouchController.PositionAndScale positionAndScale, MultiTouchController.PointInfo pointInfo) {
        this.currTouchPoint.set(pointInfo);
        boolean pos = img.setPos(positionAndScale);
        if (pos) {
            invalidate();
        }
        return pos;
    }

    
    
    public class Img {
        private static final float SCREEN_MARGIN = 10.0f;
        private float angle;
        private Bitmap bitmap;
        private float centerX;
        private float centerY;
        private Drawable drawable;
        private int height;
        private float maxX;
        private float maxY;
        private float minX;
        private float minY;
        private float scaleX;
        private float scaleY;
        private int width;
        private int displayWidth = 800;
        private int displayHeight = 1280;
        private boolean firstLoad = true;

        public Img(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void load(Bitmap bitmap) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(CollageViewMakerSticker.this.getResources(), this.bitmap);
            this.drawable = bitmapDrawable;
            this.width = bitmapDrawable.getIntrinsicWidth();
            this.height = this.drawable.getIntrinsicHeight();
            if (this.firstLoad) {
                Math.max(this.displayWidth, this.displayHeight);
                Math.max(this.width, this.height);
                Math.random();
            } else {
                if (this.maxX >= SCREEN_MARGIN) {
                    int i = (this.minX > (((float) this.displayWidth) - SCREEN_MARGIN) ? 1 : (this.minX == (((float) this.displayWidth) - SCREEN_MARGIN) ? 0 : -1));
                }
                if (this.maxY <= SCREEN_MARGIN) {
                    int i2 = (this.minY > (((float) this.displayHeight) - SCREEN_MARGIN) ? 1 : (this.minY == (((float) this.displayHeight) - SCREEN_MARGIN) ? 0 : -1));
                }
            }
            setPos((float) (this.displayWidth / 2), (float) (this.displayHeight / 4), 1.0f, 1.0f, 0.0f);
        }

        public void load2(Bitmap bitmap) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(CollageViewMakerSticker.this.getResources(), this.bitmap);
            this.drawable = bitmapDrawable;
            this.width = bitmapDrawable.getIntrinsicWidth();
            this.height = this.drawable.getIntrinsicHeight();
            if (this.firstLoad) {
                Math.max(this.displayWidth, this.displayHeight);
                Math.max(this.width, this.height);
                Math.random();
            } else {
                if (this.maxX >= SCREEN_MARGIN) {
                    int i = (this.minX > (((float) this.displayWidth) - SCREEN_MARGIN) ? 1 : (this.minX == (((float) this.displayWidth) - SCREEN_MARGIN) ? 0 : -1));
                }
                if (this.maxY <= SCREEN_MARGIN) {
                    int i2 = (this.minY > (((float) this.displayHeight) - SCREEN_MARGIN) ? 1 : (this.minY == (((float) this.displayHeight) - SCREEN_MARGIN) ? 0 : -1));
                }
            }
            setPos((float) this.width, (float) (this.height * 2), 1.0f, 1.0f, 0.0f);
        }

        public void load() {
            float f;
            float f2;
            BitmapDrawable bitmapDrawable = new BitmapDrawable(CollageViewMakerSticker.this.getResources(), this.bitmap);
            this.drawable = bitmapDrawable;
            this.width = bitmapDrawable.getIntrinsicWidth();
            this.height = this.drawable.getIntrinsicHeight();
            if (this.firstLoad) {
                double max = (double) (((float) Math.max(this.displayWidth, this.displayHeight)) / ((float) Math.max(this.width, this.height)));
                double random = Math.random();
                Double.isNaN(max);
                f2 = (float) ((max * random * 0.3d) + 0.2d);
                f = f2;
            } else {
                f2 = this.scaleX;
                f = this.scaleY;
                if (this.maxX >= SCREEN_MARGIN) {
                    int i = (this.minX > (((float) this.displayWidth) - SCREEN_MARGIN) ? 1 : (this.minX == (((float) this.displayWidth) - SCREEN_MARGIN) ? 0 : -1));
                }
                if (this.maxY <= SCREEN_MARGIN) {
                    int i2 = (this.minY > (((float) this.displayHeight) - SCREEN_MARGIN) ? 1 : (this.minY == (((float) this.displayHeight) - SCREEN_MARGIN) ? 0 : -1));
                }
            }
            setPos(((float) (this.displayWidth / 2)) - (f2 / 2.0f), ((float) (this.displayHeight / 2)) - (f / 2.0f), f2 * 1.5f, f * 1.5f, 0.0f);
        }

        public void unload() {
            this.drawable = null;
        }

        public boolean setPos(MultiTouchController.PositionAndScale positionAndScale) {
            return setPos(positionAndScale.getXOff(), positionAndScale.getYOff(), (CollageViewMakerSticker.this.mUIMode & 2) != 0 ? positionAndScale.getScaleX() : positionAndScale.getScale(), (CollageViewMakerSticker.this.mUIMode & 2) != 0 ? positionAndScale.getScaleY() : positionAndScale.getScale(), positionAndScale.getAngle());
        }

        private boolean setPos(float f, float f2, float f3, float f4, float f5) {
            float f6 = ((float) (this.width / 2)) * f3;
            float f7 = ((float) (this.height / 2)) * f4;
            float f8 = f - f6;
            float f9 = f2 - f7;
            float f10 = f6 + f;
            float f11 = f7 + f2;
            if (f8 > ((float) this.displayWidth) - SCREEN_MARGIN || f10 < SCREEN_MARGIN || f9 > ((float) this.displayHeight) - SCREEN_MARGIN || f11 < SCREEN_MARGIN) {
                return false;
            }
            this.centerX = f;
            this.centerY = f2;
            this.scaleX = f3;
            this.scaleY = f4;
            this.angle = f5;
            this.minX = f8;
            this.minY = f9;
            this.maxX = f10;
            this.maxY = f11;
            return true;
        }

        public boolean containsPoint(float f, float f2) {
            return f >= this.minX && f <= this.maxX && f2 >= this.minY && f2 <= this.maxY;
        }

        public void draw(Canvas canvas) {
            canvas.save();
            float f = this.maxX;
            float f2 = this.minX;
            float f3 = (f + f2) / 2.0f;
            float f4 = this.maxY;
            float f5 = this.minY;
            float f6 = (f4 + f5) / 2.0f;
            this.drawable.setBounds((int) f2, (int) f5, (int) f, (int) f4);
            canvas.translate(f3, f6);
            canvas.rotate((this.angle * 180.0f) / 3.1415927f);
            canvas.translate(-f3, -f6);
            this.drawable.draw(canvas);
            canvas.restore();
        }

        public Drawable getDrawable() {
            return this.drawable;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public float getCenterX() {
            return this.centerX;
        }

        public float getCenterY() {
            return this.centerY;
        }

        public float getScaleX() {
            return this.scaleX;
        }

        public float getScaleY() {
            return this.scaleY;
        }

        public float getAngle() {
            return this.angle;
        }

        public float getMinX() {
            return this.minX;
        }

        public float getMaxX() {
            return this.maxX;
        }

        public float getMinY() {
            return this.minY;
        }

        public float getMaxY() {
            return this.maxY;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }
    }

    public int getSize() {
        return this.mImages.size();
    }
}
