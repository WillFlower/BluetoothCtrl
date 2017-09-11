package com.wgh.bluetoothctrl.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by WGH on 2017/4/15.
 */
public class UUi {
    private static DisplayMetrics metric = new DisplayMetrics();
    private static android.view.WindowManager.LayoutParams mParams;
    private static UUi uUi= null;

    public static UUi getInstance(Context context) {
        Activity activity = (Activity) context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        mParams = activity.getWindow().getAttributes();
        if (uUi == null) {
            uUi = new UUi();
        }
        return uUi;
    }

    public int getWindowWidth() {
        return metric.widthPixels;// The width of the screen.(pixel)
    }

    public int getWindowHeight() {
        return metric.heightPixels;// The height of the screen.(pixel)
    }

    public float getDensity() {
        return metric.density;// The density of the screen.(0.75 / 1.0 / 1.5)
    }

    public int getDensityDpi() {
        return metric.densityDpi;// The screen density DPI.(120 / 160 / 240)
    }

    public void setWindowWidth(int width) {
        mParams.width = width;
    }

    public void setWindowHeight(int height) {
        mParams.height = height;
    }

}
