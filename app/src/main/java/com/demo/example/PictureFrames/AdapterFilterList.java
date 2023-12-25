package com.demo.example.PictureFrames;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.example.R;


public class AdapterFilterList extends BaseAdapter {
    TypedArray SHAPE_Id;
    Bitmap bitmap;
    Context context;
    LayoutInflater inflater;
    String[] name_array;

    @Override 
    public long getItemId(int i) {
        return (long) i;
    }

    public AdapterFilterList(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.name_array = context.getResources().getStringArray(R.array.filter_name);
        this.SHAPE_Id = context.getResources().obtainTypedArray(R.array.thumb_array_id);
    }

    public AdapterFilterList(Context context, RenderScriptLutColorFilter renderScriptLutColorFilter) {
        this.context = context;
        this.name_array = context.getResources().getStringArray(R.array.filter_name);
        this.inflater = LayoutInflater.from(context);
    }

    @Override 
    public int getCount() {
        return this.SHAPE_Id.length();
    }

    @Override 
    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    @Override 
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.zombi_shape_list, (ViewGroup) null);
        }
        ((TextView) view.findViewById(R.id.name)).setText(this.name_array[i]);
        ((ImageView) view.findViewById(R.id.imageItem)).setImageResource(this.SHAPE_Id.getResourceId(i, R.drawable.icon200));
        return view;
    }
}
