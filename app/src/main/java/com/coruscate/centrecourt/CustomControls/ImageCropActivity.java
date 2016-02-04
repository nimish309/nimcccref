package com.coruscate.centrecourt.CustomControls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.ImageCompression;


public class ImageCropActivity extends AppCompatActivity {

    // Bundle key for Save/Restore state ///////////////////////////////////////////////////////////
    private static final String KEY_IMG_INDEX = "img_index";
    public static Bitmap cropped = null;
    private CropImageView mCropView;
    public static final  String IMAGE_PATH= "image_path";
    private Toolbar toolbar;

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.buttonDone:
                    try {
                        cropped = mCropView.getCroppedBitmap();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Intent data = new Intent();
                    String text = "Result to be returned....";
                    data.setData(Uri.parse(text));
                    setResult(RESULT_OK, data);
                    closeActivity();
                    break;
                case R.id.buttonFitImage:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_FIT_IMAGE);
                    break;
                case R.id.button1_1:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_1_1);
                    break;
                case R.id.button3_4:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    break;
                case R.id.button4_3:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                    break;
                case R.id.button9_16:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    break;
                case R.id.button16_9:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    break;
                case R.id.buttonCustom:
                    mCropView.setCustomRatio(7, 5);
                    break;
                case R.id.buttonFree:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE);
                    break;
                case R.id.buttonCircle:
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                    break;
                case R.id.buttonRotateImage:
                    try {
                        mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    }catch (Exception e){

                    }
                    break;
                case R.id.buttonCancel:
                    closeActivity();
                    break;
            }
        }
    };
    private RelativeLayout mRootLayout;

    // Lifecycle Method ////////////////////////////////////////////////////////////////////////////
    // Image file index(1 ~ 5)
    private int mImageIndex = 1;
    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            getSupportActionBar().setTitle(AppConstant.spanFont("", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        // bind Views
        findViews();
        // apply custom font
        // set bitmap to CropImageView
        String path = getIntent().getStringExtra("image_path");
        String type = getIntent().getStringExtra("type");
        setUpToolbar();
        if (path == null) {
            return;
        }
        if (type.toString().equals("circle")) {
            mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
        }
        Log.d("Gallery Result", "Activity Result");


        cropped = BitmapFactory.decodeFile(ImageCompression.compressImage(path));
        mCropView.setImageBitmap(cropped);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_IMG_INDEX, mImageIndex);
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mImageIndex = savedInstanceState.getInt(KEY_IMG_INDEX);
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("NewApi")
    private void findViews() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
        findViewById(R.id.button1_1).setOnClickListener(btnListener);
        findViewById(R.id.button3_4).setOnClickListener(btnListener);
        findViewById(R.id.button4_3).setOnClickListener(btnListener);
        findViewById(R.id.button9_16).setOnClickListener(btnListener);
        findViewById(R.id.button16_9).setOnClickListener(btnListener);
        findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateImage).setOnClickListener(btnListener);
        findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
        findViewById(R.id.buttonCancel).setOnClickListener(btnListener);
        mRootLayout = (RelativeLayout) findViewById(R.id.layout_root);
        if (AppConstant.isAndroid5()) {
            findViewById(R.id.buttonDone).setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
            findViewById(R.id.buttonRotateImage).setBackground(getDrawable(R.drawable.ripple_accent_round_corner));

        }
    }

    // Switch image files //////////////////////////////////////////////////////////////////////////

    private void incrementImageIndex() {
        mImageIndex++;
        if (mImageIndex > 5) mImageIndex -= 5;
    }

    public Bitmap getImageForIndex(int index) {
        String fileName = "sample" + index;
        int resId = getResources().getIdentifier(fileName, "mipmap", getPackageName());
        return BitmapFactory.decodeResource(getResources(), resId);
    }
}
