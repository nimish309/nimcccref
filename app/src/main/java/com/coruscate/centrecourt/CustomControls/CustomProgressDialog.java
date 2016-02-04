package com.coruscate.centrecourt.CustomControls;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.coruscate.centrecourt.R;


/**
 * Created by User on 9/8/2015.
 */
public class CustomProgressDialog extends ProgressDialog {

    static Context context;
    ImageView image1, image2, image3;
    Animation anim1, anim2, anim3;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createProgressBar(Context context) {
        CustomProgressDialog.context = context;
        CustomProgressDialog dialog = new CustomProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        image1 = (ImageView) findViewById(R.id.pbImg1);
        image2 = (ImageView) findViewById(R.id.pbImg2);
        image3 = (ImageView) findViewById(R.id.pbImg3);
        anim1 = AnimationUtils.loadAnimation(context, R.anim.pdanimation1);
        anim2 = AnimationUtils.loadAnimation(context, R.anim.pdanimation1);
        anim3 = AnimationUtils.loadAnimation(context, R.anim.pdanimation1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                image1.clearAnimation();
                image2.setVisibility(View.VISIBLE);
                image2.startAnimation(anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image2.clearAnimation();
                image3.setVisibility(View.VISIBLE);
                image3.startAnimation(anim3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image3.clearAnimation();
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                image1.startAnimation(anim1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void show() {
        super.show();
        image1.startAnimation(anim1);
        //image2.startAnimation(anim1);
        //image3.startAnimation(anim3);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        image1.clearAnimation();
        image2.clearAnimation();
        image3.clearAnimation();
    }

}
