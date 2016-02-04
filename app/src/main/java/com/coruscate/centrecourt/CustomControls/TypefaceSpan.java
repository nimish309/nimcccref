package com.coruscate.centrecourt.CustomControls;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by CIS on 6/25/2015.
 */
public class TypefaceSpan extends MetricAffectingSpan {
    private static LruCache<String, Typeface> sTypefaceCache =  new LruCache<String, Typeface>(12);

    private Typeface mTypeface;

    public TypefaceSpan(Context context, String typefaceName) {
        mTypeface = sTypefaceCache.get(typefaceName);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), String.format("fonts/AvenirNext-Bold.ttf", typefaceName));
            sTypefaceCache.put(typefaceName, mTypeface);
        }
    }
    public TypefaceSpan(Context context, String typefaceName,String type) {
        mTypeface = sTypefaceCache.get(typefaceName);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), String.format("fonts/AvenirNext-Regular.ttf", typefaceName));
            sTypefaceCache.put(typefaceName, mTypeface);
        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}