package com.demo.example.helper;

import android.util.Log;
import android.view.MotionEvent;

import java.lang.reflect.Method;


public class MultiTouchController<T> {
    private static int ACTION_POINTER_INDEX_SHIFT = 0;
    private static int ACTION_POINTER_UP = 0;
    public static final boolean DEBUG = false;
    private static final long EVENT_SETTLE_TIME_INTERVAL = 20;
    private static final float MAX_MULTITOUCH_DIM_JUMP_SIZE = 40.0f;
    private static final float MAX_MULTITOUCH_POS_JUMP_SIZE = 30.0f;
    public static final int MAX_TOUCH_POINTS = 20;
    private static final float MIN_MULTITOUCH_SEPARATION = 30.0f;
    private static final int MODE_DRAG = 1;
    private static final int MODE_NOTHING = 0;
    private static final int MODE_PINCH = 2;
    private static Method m_getHistoricalPressure;
    private static Method m_getHistoricalX;
    private static Method m_getHistoricalY;
    private static Method m_getPointerCount;
    private static Method m_getPointerId;
    private static Method m_getPressure;
    private static Method m_getX;
    private static Method m_getY;
    public static final boolean multiTouchSupported;
    private static final int[] pointerIds;
    private static final float[] pressureVals;
    private static final float[] xVals;
    private static final float[] yVals;
    private boolean handleSingleTouchEvents;
    private PointInfo mCurrPt;
    private float mCurrPtAng;
    private float mCurrPtDiam;
    private float mCurrPtHeight;
    private float mCurrPtWidth;
    private float mCurrPtX;
    private float mCurrPtY;
    private PositionAndScale mCurrXform;
    private int mMode;
    private PointInfo mPrevPt;
    private long mSettleEndTime;
    private long mSettleStartTime;
    MultiTouchObjectCanvas<T> objectCanvas;
    private T selectedObject;
    private float startAngleMinusPinchAngle;
    private float startPosX;
    private float startPosY;
    private float startScaleOverPinchDiam;
    private float startScaleXOverPinchWidth;
    private float startScaleYOverPinchHeight;

    
    public interface MultiTouchObjectCanvas<T> {
        T getDraggableObjectAtPoint(PointInfo pointInfo);

        void getPositionAndScale(T t, PositionAndScale positionAndScale);

        void selectObject(T t, PointInfo pointInfo);

        boolean setPositionAndScale(T t, PositionAndScale positionAndScale, PointInfo pointInfo);
    }

    private void extractCurrPtInfo() {
        float f = 0.0f;
        this.mCurrPtX = this.mCurrPt.getX();
        this.mCurrPtY = this.mCurrPt.getY();
        this.mCurrPtDiam = Math.max(21.3f, !this.mCurrXform.updateScale ? 0.0f : this.mCurrPt.getMultiTouchDiameter());
        this.mCurrPtWidth = Math.max(30.0f, !this.mCurrXform.updateScaleXY ? 0.0f : this.mCurrPt.getMultiTouchWidth());
        this.mCurrPtHeight = Math.max(30.0f, !this.mCurrXform.updateScaleXY ? 0.0f : this.mCurrPt.getMultiTouchHeight());
        if (this.mCurrXform.updateAngle) {
            f = this.mCurrPt.getMultiTouchAngle();
        }
        this.mCurrPtAng = f;
    }

    public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas) {
        this(objectCanvas, true);
    }

    public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas, boolean handleSingleTouchEvents) {
        this.selectedObject = null;
        this.mCurrXform = new PositionAndScale();
        this.mMode = 0;
        this.mCurrPt = new PointInfo();
        this.mPrevPt = new PointInfo();
        this.handleSingleTouchEvents = handleSingleTouchEvents;
        this.objectCanvas = objectCanvas;
    }

    protected void setHandleSingleTouchEvents(boolean handleSingleTouchEvents) {
        this.handleSingleTouchEvents = handleSingleTouchEvents;
    }

    protected boolean getHandleSingleTouchEvents() {
        return this.handleSingleTouchEvents;
    }

    static {
        ACTION_POINTER_UP = 6;
        ACTION_POINTER_INDEX_SHIFT = 8;
        boolean succeeded = false;
        try {
            m_getPointerCount = MotionEvent.class.getMethod("getPointerCount", new Class[0]);
            m_getPointerId = MotionEvent.class.getMethod("getPointerId", Integer.TYPE);
            m_getPressure = MotionEvent.class.getMethod("getPressure", Integer.TYPE);
            m_getHistoricalX = MotionEvent.class.getMethod("getHistoricalX", Integer.TYPE, Integer.TYPE);
            m_getHistoricalY = MotionEvent.class.getMethod("getHistoricalY", Integer.TYPE, Integer.TYPE);
            m_getHistoricalPressure = MotionEvent.class.getMethod("getHistoricalPressure", Integer.TYPE, Integer.TYPE);
            m_getX = MotionEvent.class.getMethod("getX", Integer.TYPE);
            m_getY = MotionEvent.class.getMethod("getY", Integer.TYPE);
            succeeded = true;
        } catch (Exception e) {
            Log.e("MultiTouchController", "static initializer failed", e);
        }
        multiTouchSupported = succeeded;
        if (multiTouchSupported) {
            try {
                ACTION_POINTER_UP = MotionEvent.class.getField("ACTION_POINTER_UP").getInt(null);
                ACTION_POINTER_INDEX_SHIFT = MotionEvent.class.getField("ACTION_POINTER_INDEX_SHIFT").getInt(null);
            } catch (Exception e2) {
            }
        }
        xVals = new float[20];
        yVals = new float[20];
        pressureVals = new float[20];
        pointerIds = new int[20];
    }

    public boolean onTouchEvent(MotionEvent event) {
        int i;
        long eventTime;
        float pressure;
        Object invoke;
        try {
            int pointerCount = multiTouchSupported ? ((Integer) m_getPointerCount.invoke(event, new Object[0])).intValue() : 1;
            if (this.mMode == 0 && !this.handleSingleTouchEvents && pointerCount == 1) {
                return false;
            }
            int action = event.getAction();
            int histLen = event.getHistorySize() / pointerCount;
            int histIdx = 0;
            while (histIdx <= histLen) {
                boolean processingHist = histIdx < histLen;
                if (!multiTouchSupported || pointerCount == 1) {
                    xVals[0] = processingHist ? event.getHistoricalX(histIdx) : event.getX();
                    yVals[0] = processingHist ? event.getHistoricalY(histIdx) : event.getY();
                    float[] fArr = pressureVals;
                    if (processingHist) {
                        pressure = event.getHistoricalPressure(histIdx);
                    } else {
                        pressure = event.getPressure();
                    }
                    fArr[0] = pressure;
                } else {
                    int numPointers = Math.min(pointerCount, 20);
                    for (int ptrIdx = 0; ptrIdx < numPointers; ptrIdx++) {
                        pointerIds[ptrIdx] = ((Integer) m_getPointerId.invoke(event, Integer.valueOf(ptrIdx))).intValue();
                        xVals[ptrIdx] = ((Float) (processingHist ? m_getHistoricalX.invoke(event, Integer.valueOf(ptrIdx), Integer.valueOf(histIdx)) : m_getX.invoke(event, Integer.valueOf(ptrIdx)))).floatValue();
                        yVals[ptrIdx] = ((Float) (processingHist ? m_getHistoricalY.invoke(event, Integer.valueOf(ptrIdx), Integer.valueOf(histIdx)) : m_getY.invoke(event, Integer.valueOf(ptrIdx)))).floatValue();
                        float[] fArr2 = pressureVals;
                        if (processingHist) {
                            invoke = m_getHistoricalPressure.invoke(event, Integer.valueOf(ptrIdx), Integer.valueOf(histIdx));
                        } else {
                            invoke = m_getPressure.invoke(event, Integer.valueOf(ptrIdx));
                        }
                        fArr2[ptrIdx] = ((Float) invoke).floatValue();
                    }
                }
                float[] fArr3 = xVals;
                float[] fArr4 = yVals;
                float[] fArr5 = pressureVals;
                int[] iArr = pointerIds;
                if (processingHist) {
                    i = 2;
                } else {
                    i = action;
                }
                boolean z = processingHist ? true : (action == 1 || (((1 << ACTION_POINTER_INDEX_SHIFT) + -1) & action) == ACTION_POINTER_UP || action == 3) ? false : true;
                if (processingHist) {
                    eventTime = event.getHistoricalEventTime(histIdx);
                } else {
                    eventTime = event.getEventTime();
                }
                decodeTouchEvent(pointerCount, fArr3, fArr4, fArr5, iArr, i, z, eventTime);
                histIdx++;
            }
            return true;
        } catch (Exception e) {
            Log.e("MultiTouchController", "onTouchEvent() failed", e);
            return false;
        }
    }

    private void decodeTouchEvent(int pointerCount, float[] x, float[] y, float[] pressure, int[] pointerIds2, int action, boolean down, long eventTime) {
        PointInfo tmp = this.mPrevPt;
        this.mPrevPt = this.mCurrPt;
        this.mCurrPt = tmp;
        this.mCurrPt.set(pointerCount, x, y, pressure, pointerIds2, action, down, eventTime);
        multiTouchController();
    }

    private void anchorAtThisPositionAndScale() {
        float f;
        if (this.selectedObject != null) {
            this.objectCanvas.getPositionAndScale(this.selectedObject, this.mCurrXform);
            if (!this.mCurrXform.updateScale) {
                f = 1.0f;
            } else {
                f = this.mCurrXform.scale == 0.0f ? 1.0f : this.mCurrXform.scale;
            }
            float currScaleInv = 1.0f / f;
            extractCurrPtInfo();
            this.startPosX = (this.mCurrPtX - this.mCurrXform.xOff) * currScaleInv;
            this.startPosY = (this.mCurrPtY - this.mCurrXform.yOff) * currScaleInv;
            this.startScaleOverPinchDiam = this.mCurrXform.scale / this.mCurrPtDiam;
            this.startScaleXOverPinchWidth = this.mCurrXform.scaleX / this.mCurrPtWidth;
            this.startScaleYOverPinchHeight = this.mCurrXform.scaleY / this.mCurrPtHeight;
            this.startAngleMinusPinchAngle = this.mCurrXform.angle - this.mCurrPtAng;
        }
    }

    private void performDragOrPinch() {
        float currScale = 1.0f;
        if (this.selectedObject != null) {
            if (this.mCurrXform.updateScale && this.mCurrXform.scale != 0.0f) {
                currScale = this.mCurrXform.scale;
            }
            extractCurrPtInfo();
            this.mCurrXform.set(this.mCurrPtX - (this.startPosX * currScale), this.mCurrPtY - (this.startPosY * currScale), this.startScaleOverPinchDiam * this.mCurrPtDiam, this.startScaleXOverPinchWidth * this.mCurrPtWidth, this.startScaleYOverPinchHeight * this.mCurrPtHeight, this.startAngleMinusPinchAngle + this.mCurrPtAng);
            if (!this.objectCanvas.setPositionAndScale(this.selectedObject, this.mCurrXform, this.mCurrPt)) {
            }
        }
    }

    private void multiTouchController() {
        switch (this.mMode) {
            case 0:
                if (this.mCurrPt.isDown()) {
                    this.selectedObject = this.objectCanvas.getDraggableObjectAtPoint(this.mCurrPt);
                    if (this.selectedObject != null) {
                        this.mMode = 1;
                        this.objectCanvas.selectObject(this.selectedObject, this.mCurrPt);
                        anchorAtThisPositionAndScale();
                        long eventTime = this.mCurrPt.getEventTime();
                        this.mSettleEndTime = eventTime;
                        this.mSettleStartTime = eventTime;
                        return;
                    }
                    return;
                }
                return;
            case 1:
                if (!this.mCurrPt.isDown()) {
                    this.mMode = 0;
                    MultiTouchObjectCanvas<T> multiTouchObjectCanvas = this.objectCanvas;
                    this.selectedObject = null;
                    multiTouchObjectCanvas.selectObject(null, this.mCurrPt);
                    return;
                } else if (this.mCurrPt.isMultiTouch()) {
                    this.mMode = 2;
                    anchorAtThisPositionAndScale();
                    this.mSettleStartTime = this.mCurrPt.getEventTime();
                    this.mSettleEndTime = this.mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
                    return;
                } else if (this.mCurrPt.getEventTime() < this.mSettleEndTime) {
                    anchorAtThisPositionAndScale();
                    return;
                } else {
                    performDragOrPinch();
                    return;
                }
            case 2:
                if (!this.mCurrPt.isMultiTouch() || !this.mCurrPt.isDown()) {
                    if (!this.mCurrPt.isDown()) {
                        this.mMode = 0;
                        MultiTouchObjectCanvas<T> multiTouchObjectCanvas2 = this.objectCanvas;
                        this.selectedObject = null;
                        multiTouchObjectCanvas2.selectObject(null, this.mCurrPt);
                        return;
                    }
                    this.mMode = 1;
                    anchorAtThisPositionAndScale();
                    this.mSettleStartTime = this.mCurrPt.getEventTime();
                    this.mSettleEndTime = this.mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
                    return;
                } else if (Math.abs(this.mCurrPt.getX() - this.mPrevPt.getX()) > 30.0f || Math.abs(this.mCurrPt.getY() - this.mPrevPt.getY()) > 30.0f || Math.abs(this.mCurrPt.getMultiTouchWidth() - this.mPrevPt.getMultiTouchWidth()) * 0.5f > MAX_MULTITOUCH_DIM_JUMP_SIZE || Math.abs(this.mCurrPt.getMultiTouchHeight() - this.mPrevPt.getMultiTouchHeight()) * 0.5f > MAX_MULTITOUCH_DIM_JUMP_SIZE) {
                    anchorAtThisPositionAndScale();
                    this.mSettleStartTime = this.mCurrPt.getEventTime();
                    this.mSettleEndTime = this.mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
                    return;
                } else if (this.mCurrPt.eventTime < this.mSettleEndTime) {
                    anchorAtThisPositionAndScale();
                    return;
                } else {
                    performDragOrPinch();
                    return;
                }
            default:
                return;
        }
    }

    public int getMode() {
        return this.mMode;
    }

    
    public static class PointInfo {
        private int action;
        private float angle;
        private boolean angleIsCalculated;
        private float diameter;
        private boolean diameterIsCalculated;
        private float diameterSq;
        private boolean diameterSqIsCalculated;
        private float dx;
        private float dy;
        private long eventTime;
        private boolean isDown;
        private boolean isMultiTouch;
        private int numPoints;
        private float pressureMid;
        private float xMid;
        private float yMid;
        private float[] xs = new float[20];
        private float[] ys = new float[20];
        private float[] pressures = new float[20];
        private int[] pointerIds = new int[20];

        
        public void set(int numPoints, float[] x, float[] y, float[] pressure, int[] pointerIds, int action, boolean isDown, long eventTime) {
            this.eventTime = eventTime;
            this.action = action;
            this.numPoints = numPoints;
            for (int i = 0; i < numPoints; i++) {
                this.xs[i] = x[i];
                this.ys[i] = y[i];
                this.pressures[i] = pressure[i];
                this.pointerIds[i] = pointerIds[i];
            }
            this.isDown = isDown;
            this.isMultiTouch = numPoints >= 2;
            if (this.isMultiTouch) {
                this.xMid = (x[0] + x[1]) * 0.5f;
                this.yMid = (y[0] + y[1]) * 0.5f;
                this.pressureMid = (pressure[0] + pressure[1]) * 0.5f;
                this.dx = Math.abs(x[1] - x[0]);
                this.dy = Math.abs(y[1] - y[0]);
            } else {
                this.xMid = x[0];
                this.yMid = y[0];
                this.pressureMid = pressure[0];
                this.dy = 0.0f;
                this.dx = 0.0f;
            }
            this.angleIsCalculated = false;
            this.diameterIsCalculated = false;
            this.diameterSqIsCalculated = false;
        }

        public void set(PointInfo other) {
            this.numPoints = other.numPoints;
            for (int i = 0; i < this.numPoints; i++) {
                this.xs[i] = other.xs[i];
                this.ys[i] = other.ys[i];
                this.pressures[i] = other.pressures[i];
                this.pointerIds[i] = other.pointerIds[i];
            }
            this.xMid = other.xMid;
            this.yMid = other.yMid;
            this.pressureMid = other.pressureMid;
            this.dx = other.dx;
            this.dy = other.dy;
            this.diameter = other.diameter;
            this.diameterSq = other.diameterSq;
            this.angle = other.angle;
            this.isDown = other.isDown;
            this.action = other.action;
            this.isMultiTouch = other.isMultiTouch;
            this.diameterIsCalculated = other.diameterIsCalculated;
            this.diameterSqIsCalculated = other.diameterSqIsCalculated;
            this.angleIsCalculated = other.angleIsCalculated;
            this.eventTime = other.eventTime;
        }

        public boolean isMultiTouch() {
            return this.isMultiTouch;
        }

        public float getMultiTouchWidth() {
            if (this.isMultiTouch) {
                return this.dx;
            }
            return 0.0f;
        }

        public float getMultiTouchHeight() {
            if (this.isMultiTouch) {
                return this.dy;
            }
            return 0.0f;
        }

        private int julery_isqrt(int val) {
            int g = 0;
            int b = 32768;
            int bshft = 15;
            while (true) {
                bshft--;
                int temp = ((g << 1) + b) << bshft;
                if (val >= temp) {
                    g += b;
                    val -= temp;
                }
                b >>= 1;
                if (b <= 0) {
                    return g;
                }
            }
        }

        public float getMultiTouchDiameterSq() {
            if (!this.diameterSqIsCalculated) {
                this.diameterSq = this.isMultiTouch ? (this.dx * this.dx) + (this.dy * this.dy) : 0.0f;
                this.diameterSqIsCalculated = true;
            }
            return this.diameterSq;
        }

        public float getMultiTouchDiameter() {
            float f = 0.0f;
            if (!this.diameterIsCalculated) {
                if (!this.isMultiTouch) {
                    this.diameter = 0.0f;
                } else {
                    float diamSq = getMultiTouchDiameterSq();
                    if (diamSq != 0.0f) {
                        f = ((float) julery_isqrt((int) (256.0f * diamSq))) / 16.0f;
                    }
                    this.diameter = f;
                    if (this.diameter < this.dx) {
                        this.diameter = this.dx;
                    }
                    if (this.diameter < this.dy) {
                        this.diameter = this.dy;
                    }
                }
                this.diameterIsCalculated = true;
            }
            return this.diameter;
        }

        public float getMultiTouchAngle() {
            if (!this.angleIsCalculated) {
                if (!this.isMultiTouch) {
                    this.angle = 0.0f;
                } else {
                    this.angle = (float) Math.atan2((double) (this.ys[1] - this.ys[0]), (double) (this.xs[1] - this.xs[0]));
                }
                this.angleIsCalculated = true;
            }
            return this.angle;
        }

        public int getNumTouchPoints() {
            return this.numPoints;
        }

        public float getX() {
            return this.xMid;
        }

        public float[] getXs() {
            return this.xs;
        }

        public float getY() {
            return this.yMid;
        }

        public float[] getYs() {
            return this.ys;
        }

        public int[] getPointerIds() {
            return this.pointerIds;
        }

        public float getPressure() {
            return this.pressureMid;
        }

        public float[] getPressures() {
            return this.pressures;
        }

        public boolean isDown() {
            return this.isDown;
        }

        public int getAction() {
            return this.action;
        }

        public long getEventTime() {
            return this.eventTime;
        }
    }

    
    public static class PositionAndScale {
        private float angle;
        private float scale;
        private float scaleX;
        private float scaleY;
        private boolean updateAngle;
        private boolean updateScale;
        private boolean updateScaleXY;
        private float xOff;
        private float yOff;

        public void set(float xOff, float yOff, boolean updateScale, float scale, boolean updateScaleXY, float scaleX, float scaleY, boolean updateAngle, float angle) {
            float f = 1.0f;
            this.xOff = xOff;
            this.yOff = yOff;
            this.updateScale = updateScale;
            if (scale == 0.0f) {
                scale = 1.0f;
            }
            this.scale = scale;
            this.updateScaleXY = updateScaleXY;
            if (scaleX == 0.0f) {
                scaleX = 1.0f;
            }
            this.scaleX = scaleX;
            if (scaleY != 0.0f) {
                f = scaleY;
            }
            this.scaleY = f;
            this.updateAngle = updateAngle;
            this.angle = angle;
        }

        protected void set(float xOff, float yOff, float scale, float scaleX, float scaleY, float angle) {
            float f = 1.0f;
            this.xOff = xOff;
            this.yOff = yOff;
            if (scale == 0.0f) {
                scale = 1.0f;
            }
            this.scale = scale;
            if (scaleX == 0.0f) {
                scaleX = 1.0f;
            }
            this.scaleX = scaleX;
            if (scaleY != 0.0f) {
                f = scaleY;
            }
            this.scaleY = f;
            this.angle = angle;
        }

        public float getXOff() {
            return this.xOff;
        }

        public float getYOff() {
            return this.yOff;
        }

        public float getScale() {
            if (!this.updateScale) {
                return 1.0f;
            }
            return this.scale;
        }

        public float getScaleX() {
            if (!this.updateScaleXY) {
                return 1.0f;
            }
            return this.scaleX;
        }

        public float getScaleY() {
            if (!this.updateScaleXY) {
                return 1.0f;
            }
            return this.scaleY;
        }

        public float getAngle() {
            if (!this.updateAngle) {
                return 0.0f;
            }
            return this.angle;
        }
    }
}
