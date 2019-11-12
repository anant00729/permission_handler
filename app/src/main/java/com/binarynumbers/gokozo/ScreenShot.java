package com.binarynumbers.gokozo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ScreenShot {

    public static Bitmap takescreenshot(View v, int w, int h) {

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        v.draw(canvas);
        return bitmap;




    }

    public static Bitmap takescreenshotOfRootView(View v, int w, int h) {
        return takescreenshot(v, w, h);
    }
}
