package com.demo.example.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;

import com.demo.example.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HorizontalListView extends AdapterView<ListAdapter> {
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
    private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30.0f;
    private static final float FLING_FRICTION = 0.009f;
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;
    protected ListAdapter mAdapter;
    protected int mCurrentX;
    private int mCurrentlySelectedAdapterIndex;
    private int mDisplayOffset;
    private EdgeEffectCompat mEdgeGlowLeft;
    private EdgeEffectCompat mEdgeGlowRight;
    private GestureDetector mGestureDetector;
    private final GestureListener mGestureListener;
    private int mHeightMeasureSpec;
    private int mLeftViewAdapterIndex;
    protected int mNextX;
    private OnClickListener mOnClickListener;
    private int mRightViewAdapterIndex;
    protected Scroller mFlingTracker = new Scroller(getContext());
    private List<Queue<View>> mRemovedViewsCache = new ArrayList();
    private boolean mDataChanged = false;
    private Rect mRect = new Rect();
    private View mViewBeingTouched = null;
    private int mDividerWidth = 0;
    private Drawable mDivider = null;
    private Integer mRestoreX = null;
    private int mMaxX = Integer.MAX_VALUE;
    private RunningOutOfDataListener mRunningOutOfDataListener = null;
    private int mRunningOutOfDataThreshold = 0;
    private boolean mHasNotifiedRunningLowOnData = false;
    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;
    private OnScrollStateChangedListener.ScrollState mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;
    private boolean mBlockTouchAction = false;
    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;
    private DataSetObserver mAdapterDataObserver = new DataSetObserver() { 
        @Override 
        public void onChanged() {
            HorizontalListView.this.mDataChanged = true;
            HorizontalListView.this.mHasNotifiedRunningLowOnData = false;
            HorizontalListView.this.unpressTouchedChild();
            HorizontalListView.this.invalidate();
            HorizontalListView.this.requestLayout();
        }

        @Override 
        public void onInvalidated() {
            HorizontalListView.this.mHasNotifiedRunningLowOnData = false;
            HorizontalListView.this.unpressTouchedChild();
            HorizontalListView.this.reset();
            HorizontalListView.this.invalidate();
            HorizontalListView.this.requestLayout();
        }
    };
    private Runnable mDelayedLayout = new Runnable() { 
        @Override 
        public void run() {
            HorizontalListView.this.requestLayout();
        }
    };

    
    public interface OnScrollStateChangedListener {

        
        public enum ScrollState {
            SCROLL_STATE_IDLE,
            SCROLL_STATE_TOUCH_SCROLL,
            SCROLL_STATE_FLING
        }

        void onScrollStateChanged(ScrollState scrollState);
    }

    
    public interface RunningOutOfDataListener {
        void onRunningOutOfData();
    }

    @Override 
    protected void dispatchSetPressed(boolean z) {
    }

    public HorizontalListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        GestureListener gestureListener = new GestureListener();
        this.mGestureListener = gestureListener;
        this.mEdgeGlowLeft = new EdgeEffectCompat(context);
        this.mEdgeGlowRight = new EdgeEffectCompat(context);
        this.mGestureDetector = new GestureDetector(context, gestureListener);
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attributeSet);
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= 11) {
            HoneycombPlus.setFriction(this.mFlingTracker, FLING_FRICTION);
        }
    }

    private void bindGestureDetector() {
        setOnTouchListener(new OnTouchListener() { 
            @Override 
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return HorizontalListView.this.mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    
    public void requestParentListViewToNotInterceptTouchEvents(Boolean bool) {
        if (this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != bool.booleanValue()) {
            for (View view = this; view.getParent() instanceof View; view = (View) view.getParent()) {
                if ((view.getParent() instanceof ListView) || (view.getParent() instanceof ScrollView)) {
                    view.getParent().requestDisallowInterceptTouchEvent(bool.booleanValue());
                    this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = bool.booleanValue();
                    return;
                }
            }
        }
    }

    private void retrieveXmlConfiguration(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HorizontalListView);
            Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.HorizontalListView_android_divider);
            if (drawable != null) {
                setDivider(drawable);
            }
            int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.HorizontalListView_dividerWidth, 0);
            if (dimensionPixelSize != 0) {
                setDividerWidth(dimensionPixelSize);
            }
            obtainStyledAttributes.recycle();
        }
    }

    @Override 
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());
        bundle.putInt(BUNDLE_ID_CURRENT_X, this.mCurrentX);
        return bundle;
    }

    @Override 
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.mRestoreX = Integer.valueOf(bundle.getInt(BUNDLE_ID_CURRENT_X));
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    public void setDivider(Drawable drawable) {
        this.mDivider = drawable;
        if (drawable != null) {
            setDividerWidth(drawable.getIntrinsicWidth());
        } else {
            setDividerWidth(0);
        }
    }

    public void setDividerWidth(int i) {
        this.mDividerWidth = i;
        requestLayout();
        invalidate();
    }

    private void initView() {
        this.mLeftViewAdapterIndex = -1;
        this.mRightViewAdapterIndex = -1;
        this.mDisplayOffset = 0;
        this.mCurrentX = 0;
        this.mNextX = 0;
        this.mMaxX = Integer.MAX_VALUE;
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
    }

    
    public void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override 
    public void setSelection(int i) {
        this.mCurrentlySelectedAdapterIndex = i;
    }

    @Override 
    public View getSelectedView() {
        return getChild(this.mCurrentlySelectedAdapterIndex);
    }

    public void setAdapter(ListAdapter listAdapter) {
        ListAdapter listAdapter2 = this.mAdapter;
        if (listAdapter2 != null) {
            listAdapter2.unregisterDataSetObserver(this.mAdapterDataObserver);
        }
        if (listAdapter != null) {
            this.mHasNotifiedRunningLowOnData = false;
            this.mAdapter = listAdapter;
            listAdapter.registerDataSetObserver(this.mAdapterDataObserver);
        }
        initializeRecycledViewCache(this.mAdapter.getViewTypeCount());
        reset();
    }

    @Override 
    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    private void initializeRecycledViewCache(int i) {
        this.mRemovedViewsCache.clear();
        for (int i2 = 0; i2 < i; i2++) {
            this.mRemovedViewsCache.add(new LinkedList());
        }
    }

    private View getRecycledView(int i) {
        int itemViewType = this.mAdapter.getItemViewType(i);
        if (isItemViewTypeValid(itemViewType)) {
            return this.mRemovedViewsCache.get(itemViewType).poll();
        }
        return null;
    }

    private void recycleView(int i, View view) {
        int itemViewType = this.mAdapter.getItemViewType(i);
        if (isItemViewTypeValid(itemViewType)) {
            this.mRemovedViewsCache.get(itemViewType).offer(view);
        }
    }

    private boolean isItemViewTypeValid(int i) {
        return i < this.mRemovedViewsCache.size();
    }

    private void addAndMeasureChild(View view, int i) {
        addViewInLayout(view, i, getLayoutParams(view), true);
        measureChild(view);
    }

    private void measureChild(View view) {
        int i;
        LayoutParams layoutParams = getLayoutParams(view);
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
        if (layoutParams.width > 0) {
            i = MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
        } else {
            i = MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(i, childMeasureSpec);
    }

    private LayoutParams getLayoutParams(View view) {
        LayoutParams layoutParams = view.getLayoutParams();
        return layoutParams == null ? new LayoutParams(-2, -1) : layoutParams;
    }

    @Override 
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mAdapter != null) {
            invalidate();
            if (this.mDataChanged) {
                int i5 = this.mCurrentX;
                initView();
                removeAllViewsInLayout();
                this.mNextX = i5;
                this.mDataChanged = false;
            }
            Integer num = this.mRestoreX;
            if (num != null) {
                this.mNextX = num.intValue();
                this.mRestoreX = null;
            }
            if (this.mFlingTracker.computeScrollOffset()) {
                this.mNextX = this.mFlingTracker.getCurrX();
            }
            int i6 = this.mNextX;
            if (i6 < 0) {
                this.mNextX = 0;
                if (this.mEdgeGlowLeft.isFinished()) {
                    this.mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
                }
                this.mFlingTracker.forceFinished(true);
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            } else {
                int i7 = this.mMaxX;
                if (i6 > i7) {
                    this.mNextX = i7;
                    if (this.mEdgeGlowRight.isFinished()) {
                        this.mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
                    }
                    this.mFlingTracker.forceFinished(true);
                    setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
                }
            }
            int i8 = this.mCurrentX - this.mNextX;
            removeNonVisibleChildren(i8);
            fillList(i8);
            positionChildren(i8);
            this.mCurrentX = this.mNextX;
            if (determineMaxX()) {
                onLayout(z, i, i2, i3, i4);
            } else if (!this.mFlingTracker.isFinished()) {
                ViewCompat.postOnAnimation(this, this.mDelayedLayout);
            } else if (this.mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
        }
    }

    @Override 
    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        int i = this.mCurrentX;
        if (i == 0) {
            return 0.0f;
        }
        if (i < horizontalFadingEdgeLength) {
            return ((float) i) / ((float) horizontalFadingEdgeLength);
        }
        return 1.0f;
    }

    @Override 
    protected float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        int i = this.mCurrentX;
        int i2 = this.mMaxX;
        if (i == i2) {
            return 0.0f;
        }
        if (i2 - i < horizontalFadingEdgeLength) {
            return ((float) (i2 - i)) / ((float) horizontalFadingEdgeLength);
        }
        return 1.0f;
    }

    private float determineFlingAbsorbVelocity() {
        return Build.VERSION.SDK_INT >= 14 ? IceCreamSandwichPlus.getCurrVelocity(this.mFlingTracker) : FLING_DEFAULT_ABSORB_VELOCITY;
    }

    @Override 
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mHeightMeasureSpec = i2;
    }

    private boolean determineMaxX() {
        View rightmostChild;
        if (isLastItemInAdapter(this.mRightViewAdapterIndex) && (rightmostChild = getRightmostChild()) != null) {
            int i = this.mMaxX;
            int right = (this.mCurrentX + (rightmostChild.getRight() - getPaddingLeft())) - getRenderWidth();
            this.mMaxX = right;
            if (right < 0) {
                this.mMaxX = 0;
            }
            if (this.mMaxX != i) {
                return true;
            }
        }
        return false;
    }

    private void fillList(int i) {
        View rightmostChild = getRightmostChild();
        int i2 = 0;
        fillListRight(rightmostChild != null ? rightmostChild.getRight() : 0, i);
        View leftmostChild = getLeftmostChild();
        if (leftmostChild != null) {
            i2 = leftmostChild.getLeft();
        }
        fillListLeft(i2, i);
    }

    private void removeNonVisibleChildren(int i) {
        int i2;
        View leftmostChild = getLeftmostChild();
        while (leftmostChild != null && leftmostChild.getRight() + i <= 0) {
            int i3 = this.mDisplayOffset;
            if (isLastItemInAdapter(this.mLeftViewAdapterIndex)) {
                i2 = leftmostChild.getMeasuredWidth();
            } else {
                i2 = this.mDividerWidth + leftmostChild.getMeasuredWidth();
            }
            this.mDisplayOffset = i3 + i2;
            recycleView(this.mLeftViewAdapterIndex, leftmostChild);
            removeViewInLayout(leftmostChild);
            this.mLeftViewAdapterIndex++;
            leftmostChild = getLeftmostChild();
        }
        View rightmostChild = getRightmostChild();
        while (rightmostChild != null && rightmostChild.getLeft() + i >= getWidth()) {
            recycleView(this.mRightViewAdapterIndex, rightmostChild);
            removeViewInLayout(rightmostChild);
            this.mRightViewAdapterIndex--;
            rightmostChild = getRightmostChild();
        }
    }

    private void fillListRight(int i, int i2) {
        while (i + i2 + this.mDividerWidth < getWidth() && this.mRightViewAdapterIndex + 1 < this.mAdapter.getCount()) {
            int i3 = this.mRightViewAdapterIndex + 1;
            this.mRightViewAdapterIndex = i3;
            if (this.mLeftViewAdapterIndex < 0) {
                this.mLeftViewAdapterIndex = i3;
            }
            View view = this.mAdapter.getView(i3, getRecycledView(i3), this);
            addAndMeasureChild(view, -1);
            i += (this.mRightViewAdapterIndex == 0 ? 0 : this.mDividerWidth) + view.getMeasuredWidth();
            determineIfLowOnData();
        }
    }

    private void fillListLeft(int i, int i2) {
        int i3;
        int i4;
        int i5;
        while ((i + i2) - this.mDividerWidth > 0 && (i3 = this.mLeftViewAdapterIndex) >= 1) {
            int i6 = i3 - 1;
            this.mLeftViewAdapterIndex = i6;
            View view = this.mAdapter.getView(i6, getRecycledView(i6), this);
            addAndMeasureChild(view, 0);
            if (this.mLeftViewAdapterIndex == 0) {
                i4 = view.getMeasuredWidth();
            } else {
                i4 = this.mDividerWidth + view.getMeasuredWidth();
            }
            i -= i4;
            int i7 = this.mDisplayOffset;
            if (i + i2 == 0) {
                i5 = view.getMeasuredWidth();
            } else {
                i5 = view.getMeasuredWidth() + this.mDividerWidth;
            }
            this.mDisplayOffset = i7 - i5;
        }
    }

    private void positionChildren(int i) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i2 = this.mDisplayOffset + i;
            this.mDisplayOffset = i2;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                int paddingLeft = getPaddingLeft() + i2;
                int paddingTop = getPaddingTop();
                childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                i2 += childAt.getMeasuredWidth() + this.mDividerWidth;
            }
        }
    }

    private View getLeftmostChild() {
        return getChildAt(0);
    }

    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChild(int i) {
        int i2 = this.mLeftViewAdapterIndex;
        if (i < i2 || i > this.mRightViewAdapterIndex) {
            return null;
        }
        return getChildAt(i - i2);
    }

    
    public int getChildIndex(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            getChildAt(i3).getHitRect(this.mRect);
            if (this.mRect.contains(i, i2)) {
                return i3;
            }
        }
        return -1;
    }

    private boolean isLastItemInAdapter(int i) {
        return i == this.mAdapter.getCount() - 1;
    }

    private int getRenderHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getRenderWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    public void scrollTo(int i) {
        Scroller scroller = this.mFlingTracker;
        int i2 = this.mNextX;
        scroller.startScroll(i2, 0, i - i2, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    @Override 
    public int getFirstVisiblePosition() {
        return this.mLeftViewAdapterIndex;
    }

    @Override 
    public int getLastVisiblePosition() {
        return this.mRightViewAdapterIndex;
    }

    private void drawEdgeGlow(Canvas canvas) {
        EdgeEffectCompat edgeEffectCompat = this.mEdgeGlowLeft;
        if (edgeEffectCompat == null || edgeEffectCompat.isFinished() || !isEdgeGlowEnabled()) {
            EdgeEffectCompat edgeEffectCompat2 = this.mEdgeGlowRight;
            if (edgeEffectCompat2 != null && !edgeEffectCompat2.isFinished() && isEdgeGlowEnabled()) {
                int save = canvas.save();
                int width = getWidth();
                canvas.rotate(90.0f, 0.0f, 0.0f);
                canvas.translate((float) getPaddingTop(), (float) (-width));
                this.mEdgeGlowRight.setSize(getRenderHeight(), getRenderWidth());
                if (this.mEdgeGlowRight.draw(canvas)) {
                    invalidate();
                }
                canvas.restoreToCount(save);
                return;
            }
            return;
        }
        int save2 = canvas.save();
        int height = getHeight();
        canvas.rotate(-90.0f, 0.0f, 0.0f);
        canvas.translate((float) ((-height) + getPaddingBottom()), 0.0f);
        this.mEdgeGlowLeft.setSize(getRenderHeight(), getRenderWidth());
        if (this.mEdgeGlowLeft.draw(canvas)) {
            invalidate();
        }
        canvas.restoreToCount(save2);
    }

    private void drawDividers(Canvas canvas) {
        int childCount = getChildCount();
        Rect rect = this.mRect;
        rect.top = getPaddingTop();
        Rect rect2 = this.mRect;
        rect2.bottom = rect2.top + getRenderHeight();
        for (int i = 0; i < childCount; i++) {
            if (i != childCount - 1 || !isLastItemInAdapter(this.mRightViewAdapterIndex)) {
                View childAt = getChildAt(i);
                rect.left = childAt.getRight();
                rect.right = childAt.getRight() + this.mDividerWidth;
                if (rect.left < getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                }
                if (rect.right > getWidth() - getPaddingRight()) {
                    rect.right = getWidth() - getPaddingRight();
                }
                drawDivider(canvas, rect);
                if (i == 0 && childAt.getLeft() > getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                    rect.right = childAt.getLeft();
                    drawDivider(canvas, rect);
                }
            }
        }
    }

    private void drawDivider(Canvas canvas, Rect rect) {
        Drawable drawable = this.mDivider;
        if (drawable != null) {
            drawable.setBounds(rect);
            this.mDivider.draw(canvas);
        }
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividers(canvas);
    }

    @Override 
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    protected boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.mFlingTracker.fling(this.mNextX, 0, (int) (-f), 0, 0, this.mMaxX, 0, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent motionEvent) {
        int childIndex;
        this.mBlockTouchAction = !this.mFlingTracker.isFinished();
        this.mFlingTracker.forceFinished(true);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        unpressTouchedChild();
        if (!this.mBlockTouchAction && (childIndex = getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY())) >= 0) {
            View childAt = getChildAt(childIndex);
            this.mViewBeingTouched = childAt;
            if (childAt != null) {
                childAt.setPressed(true);
                refreshDrawableState();
            }
        }
        return true;
    }

    
    public void unpressTouchedChild() {
        View view = this.mViewBeingTouched;
        if (view != null) {
            view.setPressed(false);
            refreshDrawableState();
            this.mViewBeingTouched = null;
        }
    }

    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        @Override
        
        public boolean onDown(MotionEvent motionEvent) {
            return HorizontalListView.this.onDown(motionEvent);
        }

        @Override
        
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return HorizontalListView.this.onFling(motionEvent, motionEvent2, f, f2);
        }

        @Override
        
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            HorizontalListView.this.requestParentListViewToNotInterceptTouchEvents(true);
            HorizontalListView.this.setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_TOUCH_SCROLL);
            HorizontalListView.this.unpressTouchedChild();
            HorizontalListView.this.mNextX += (int) f;
            HorizontalListView.this.updateOverscrollAnimation(Math.round(f));
            HorizontalListView.this.requestLayout();
            return true;
        }

        @Override
        
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            HorizontalListView.this.unpressTouchedChild();
            OnItemClickListener onItemClickListener = HorizontalListView.this.getOnItemClickListener();
            int childIndex = HorizontalListView.this.getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (childIndex >= 0 && !HorizontalListView.this.mBlockTouchAction) {
                View childAt = HorizontalListView.this.getChildAt(childIndex);
                int i = HorizontalListView.this.mLeftViewAdapterIndex + childIndex;
                if (onItemClickListener != null) {
                    HorizontalListView horizontalListView = HorizontalListView.this;
                    onItemClickListener.onItemClick(horizontalListView, childAt, i, horizontalListView.mAdapter.getItemId(i));
                    return true;
                }
            }
            if (HorizontalListView.this.mOnClickListener == null || HorizontalListView.this.mBlockTouchAction) {
                return false;
            }
            HorizontalListView.this.mOnClickListener.onClick(HorizontalListView.this);
            return false;
        }

        @Override
        
        public void onLongPress(MotionEvent motionEvent) {
            HorizontalListView.this.unpressTouchedChild();
            int childIndex = HorizontalListView.this.getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (childIndex >= 0 && !HorizontalListView.this.mBlockTouchAction) {
                View childAt = HorizontalListView.this.getChildAt(childIndex);
                OnItemLongClickListener onItemLongClickListener = HorizontalListView.this.getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int i = HorizontalListView.this.mLeftViewAdapterIndex + childIndex;
                    HorizontalListView horizontalListView = HorizontalListView.this;
                    if (onItemLongClickListener.onItemLongClick(horizontalListView, childAt, i, horizontalListView.mAdapter.getItemId(i))) {
                        HorizontalListView.this.performHapticFeedback(0);
                    }
                }
            }
        }
    }

    @Override 
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            Scroller scroller = this.mFlingTracker;
            if (scroller == null || scroller.isFinished()) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
            requestParentListViewToNotInterceptTouchEvents(false);
            releaseEdgeGlow();
        } else if (motionEvent.getAction() == 3) {
            unpressTouchedChild();
            releaseEdgeGlow();
            requestParentListViewToNotInterceptTouchEvents(false);
        }
        return super.onTouchEvent(motionEvent);
    }

    private void releaseEdgeGlow() {
        EdgeEffectCompat edgeEffectCompat = this.mEdgeGlowLeft;
        if (edgeEffectCompat != null) {
            edgeEffectCompat.onRelease();
        }
        EdgeEffectCompat edgeEffectCompat2 = this.mEdgeGlowRight;
        if (edgeEffectCompat2 != null) {
            edgeEffectCompat2.onRelease();
        }
    }

    public void setRunningOutOfDataListener(RunningOutOfDataListener runningOutOfDataListener, int i) {
        this.mRunningOutOfDataListener = runningOutOfDataListener;
        this.mRunningOutOfDataThreshold = i;
    }

    private void determineIfLowOnData() {
        ListAdapter listAdapter;
        if (this.mRunningOutOfDataListener != null && (listAdapter = this.mAdapter) != null && listAdapter.getCount() - (this.mRightViewAdapterIndex + 1) < this.mRunningOutOfDataThreshold && !this.mHasNotifiedRunningLowOnData) {
            this.mHasNotifiedRunningLowOnData = true;
            this.mRunningOutOfDataListener.onRunningOutOfData();
        }
    }

    @Override 
    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnScrollStateChangedListener(OnScrollStateChangedListener onScrollStateChangedListener) {
        this.mOnScrollStateChangedListener = onScrollStateChangedListener;
    }

    
    public void setCurrentScrollState(OnScrollStateChangedListener.ScrollState scrollState) {
        OnScrollStateChangedListener onScrollStateChangedListener;
        if (!(this.mCurrentScrollState == scrollState || (onScrollStateChangedListener = this.mOnScrollStateChangedListener) == null)) {
            onScrollStateChangedListener.onScrollStateChanged(scrollState);
        }
        this.mCurrentScrollState = scrollState;
    }

    
    public void updateOverscrollAnimation(int i) {
        if (this.mEdgeGlowLeft != null && this.mEdgeGlowRight != null) {
            int i2 = this.mCurrentX + i;
            Scroller scroller = this.mFlingTracker;
            if (scroller != null && !scroller.isFinished()) {
                return;
            }
            if (i2 < 0) {
                this.mEdgeGlowLeft.onPull(((float) Math.abs(i)) / ((float) getRenderWidth()));
                if (!this.mEdgeGlowRight.isFinished()) {
                    this.mEdgeGlowRight.onRelease();
                }
            } else if (i2 > this.mMaxX) {
                this.mEdgeGlowRight.onPull(((float) Math.abs(i)) / ((float) getRenderWidth()));
                if (!this.mEdgeGlowLeft.isFinished()) {
                    this.mEdgeGlowLeft.onRelease();
                }
            }
        }
    }

    private boolean isEdgeGlowEnabled() {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null || listAdapter.isEmpty() || this.mMaxX <= 0) {
            return false;
        }
        return true;
    }

    
    private static final class HoneycombPlus {
        private HoneycombPlus() {
        }

        static {
            if (Build.VERSION.SDK_INT < 11) {
                throw new RuntimeException("Should not get to HoneycombPlus class unless sdk is >= 11!");
            }
        }

        public static void setFriction(Scroller scroller, float f) {
            if (scroller != null) {
                scroller.setFriction(f);
            }
        }
    }

    
    
    public static final class IceCreamSandwichPlus {
        private IceCreamSandwichPlus() {
        }

        static {
            if (Build.VERSION.SDK_INT < 14) {
                throw new RuntimeException("Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
            }
        }

        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }
}
