package com.demo.example.stickerapihhitter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


class CustomImageView extends ImageView {
    public CustomImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomImageView(Context context) {
        super(context);
    }

    @Override 
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(i, i);
    }
}
