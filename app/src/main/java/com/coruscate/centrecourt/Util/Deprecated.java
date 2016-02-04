package com.coruscate.centrecourt.Util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by CIS on 6/23/2015.
 */
public class Deprecated {

    private Activity activity;

    public Deprecated(Activity activity) {
        this.activity = activity;
    }

    public Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return activity.getDrawable(id);
        else
            //noinspection deprecation
            return activity.getResources().getDrawable(id);
    }

    public int getColor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return activity.getColor(id);
        else
            //noinspection deprecation
            return activity.getResources().getColor(id);
    }



}
