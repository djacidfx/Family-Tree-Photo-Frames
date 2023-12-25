package com.demo.example.PictureFrames;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsic3DLUT;
import android.renderscript.Type;
import android.util.Log;


public class RenderScriptLutColorFilter {
    private static RenderScript rs;
    Context context;
    private final int RED_DIM = 64;
    private final int GREEN_DIM = 64;
    private final int BLUE_DIM = 64;

    public RenderScriptLutColorFilter(Context context) {
        this.context = context;
        rs = RenderScript.create(context);
    }

    public Bitmap renderImage(Bitmap bitmap, Bitmap bitmap2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        ScriptIntrinsic3DLUT rsScript = getRsScript(bitmap2);
        Allocation createFromBitmap = Allocation.createFromBitmap(rs, bitmap);
        Allocation createFromBitmap2 = Allocation.createFromBitmap(rs, createBitmap);
        rsScript.forEach(createFromBitmap, createFromBitmap2);
        createFromBitmap2.copyTo(createBitmap);
        return createBitmap;
    }

    private synchronized Allocation getLutCube(RenderScript renderScript, Bitmap bitmap) {
        Allocation createTyped;
        Type.Builder builder = new Type.Builder(renderScript, Element.U8_4(renderScript));
        builder.setX(64);
        builder.setY(64);
        builder.setZ(64);
        createTyped = Allocation.createTyped(renderScript, builder.create());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = width * height;
        int[] iArr = new int[i];
        int[] iArr2 = new int[i];
        bitmap.getPixels(iArr2, 0, width, 0, 0, width, height);
        for (int i2 = 0; i2 < 64; i2++) {
            for (int i3 = 0; i3 < 64; i3++) {
                for (int i4 = 0; i4 < 64; i4++) {
                    try {
                        int i5 = iArr2[((i2 / 8) * width * 64) + ((i2 % 8) * 64) + (i3 * width) + i4];
                        iArr[(i2 * 64 * 64) + (i3 * 64) + i4] = ((i5 & 255) << 16) | -16777216 | ((i5 >> 16) & 255) | (((i5 >> 8) & 255) << 8);
                    } catch (Exception e) {
                        Log.e("Exception", "" + e);
                    }
                }
            }
        }
        try {
            createTyped.copyFromUnchecked(iArr);
        } catch (Exception unused) {
        }
        return createTyped;
    }

    private ScriptIntrinsic3DLUT getRsScript(Bitmap bitmap) {
        RenderScript renderScript = rs;
        ScriptIntrinsic3DLUT create = ScriptIntrinsic3DLUT.create(renderScript, Element.RGBA_8888(renderScript));
        create.setLUT(getLutCube(rs, bitmap));
        return create;
    }
}
