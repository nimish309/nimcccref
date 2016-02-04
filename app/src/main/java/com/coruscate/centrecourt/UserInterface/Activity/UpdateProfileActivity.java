package com.coruscate.centrecourt.UserInterface.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.ImageCropActivity;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedCheckBox;
import com.coruscate.centrecourt.CustomControls.TypefacedEditText;
import com.coruscate.centrecourt.CustomControls.TypefacedRadioButton;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.InternalStorageContentProvider;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by CIS-16 on 6/30/2015.
 */

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private static final String TAG = "Sign Up";
    public static boolean isProfileSelected = false;
    public static File mFileTemp;
    private final String PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.imgRegisterProfilePic)
    ImageView imgRegisterProfilePic;
    @InjectView(R.id.txtPersonalDetail)
    TypefacedTextView txtPersonalDetail;
    @InjectView(R.id.etName)
    TypefacedEditText etName;
    @InjectView(R.id.etLastName)
    TypefacedEditText etLastName;
    @InjectView(R.id.etMobileNo)
    TypefacedEditText etMobileNo;
    @InjectView(R.id.etEmail)
    TypefacedEditText etEmail;
    @InjectView(R.id.etPassword)
    TypefacedEditText etPassword;
    @InjectView(R.id.linearLayoutPersionalDetail)
    LinearLayout linearLayoutPersionalDetail;
    @InjectView(R.id.txtAddressDetail)
    TypefacedTextView txtAddressDetail;
    @InjectView(R.id.etAddress)
    TypefacedEditText etAddress;
    @InjectView(R.id.etCity)
    TypefacedEditText etCity;
    @InjectView(R.id.etState)
    TypefacedEditText etState;
    @InjectView(R.id.etCountry)
    TypefacedEditText etCountry;
    @InjectView(R.id.etPinCode)
    TypefacedEditText etPinCode;
    @InjectView(R.id.linearLayoutAddressDetail)
    LinearLayout linearLayoutAddressDetail;
    @InjectView(R.id.btnSignup)
    TypedfacedButton btnSignup;
    @InjectView(R.id.linSignupBottom)
    LinearLayout linSignupBottom;
    @InjectView(R.id.linearLayoutRegister)
    LinearLayout linearLayoutRegister;
    @InjectView(R.id.etConfirmPassword)
    TypefacedEditText etConfirmPassword;
    @InjectView(R.id.etAddress2)
    TypefacedEditText etAddress2;
    @InjectView(R.id.txtSubscribe)
    TypefacedTextView txtSubscribe;
    @InjectView(R.id.radiobuttonYes)
    TypefacedRadioButton radiobuttonYes;
    @InjectView(R.id.radiobuttonNo)
    TypefacedRadioButton radiobuttonNo;
    @InjectView(R.id.radioButtonGroup)
    RadioGroup radioButtonGroup;
    @InjectView(R.id.checkboxPrivacy)
    TypefacedCheckBox checkboxPrivacy;
    @InjectView(R.id.txtPrivacyPolicy)
    TypefacedTextView txtPrivacyPolicy;
    @InjectView(R.id.linPrivacyPolicy)
    LinearLayout linPrivacyPolicy;
    private String strEmail, strMobile, strAddress, strPasssword, strName, strLastName;
    private Bitmap bitmap;
    private String imageUrl;
    private PermissionHelper permissionHelper;
    private AlertDialog builder;
    private OnPermissionCallback onPermissionCallbackPic;
    private OnPermissionCallback onPermissionCallbackPhoto;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avtar)
                .showImageForEmptyUri(R.drawable.avtar)
                .showImageOnFail(R.drawable.avtar).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        linearLayoutAddressDetail.setVisibility(View.GONE);
        linearLayoutPersionalDetail.setVisibility(View.VISIBLE);
        txtAddressDetail.setVisibility(View.GONE);
        txtPersonalDetail.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        txtSubscribe.setVisibility(View.GONE);
        etConfirmPassword.setVisibility(View.GONE);
        linPrivacyPolicy.setVisibility(View.GONE);
        radioButtonGroup.setVisibility(View.GONE);

        if (AppConstant.isAndroid5()) {
            btnSignup.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        try {
            AppConstant.setToolBarColor(UpdateProfileActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpToolbar();
        String state = Environment.getExternalStorageState();
        btnSignup.setText("Update");
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        }
        imgRegisterProfilePic.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        setProfileInfo();
    }


    private void showAlertDialog() {

        String[] array = {"Pick From Gallery", "Take Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (AppConstant.isAndroid6()) {
                            onPermissionCallbackPhoto = new OnPermissionCallback() {
                                @Override
                                public void onPermissionGranted(String[] permissionName) {
                                    openGallery();
                                }

                                @Override
                                public void onPermissionDeclined(String[] permissionName) {

                                }

                                @Override
                                public void onPermissionPreGranted(String permissionsName) {
                                    openGallery();
                                }

                                @Override
                                public void onPermissionNeedExplanation(String permissionName) {
                                    getAlertDialog(permissionName, "Grant Camera Permission", "Camera permission is needed to access device camera.", permissionHelper).show();
                                }

                                @Override
                                public void onPermissionReallyDeclined(String permissionName) {

                                }

                                @Override
                                public void onNoPermissionNeeded() {

                                }
                            };
                            permissionHelper = PermissionHelper.getInstance(UpdateProfileActivity.this, onPermissionCallbackPhoto);
                            permissionHelper.setForceAccepting(false).request(PERMISSION_STORAGE);
                        } else {
                            openGallery();
                        }

                        break;
                    case 1:
                        if (AppConstant.isAndroid6()) {
                            onPermissionCallbackPic = new OnPermissionCallback() {
                                @Override
                                public void onPermissionGranted(String[] permissionName) {
                                    takePicture();
                                }

                                @Override
                                public void onPermissionDeclined(String[] permissionName) {

                                }

                                @Override
                                public void onPermissionPreGranted(String permissionsName) {
                                    takePicture();
                                }

                                @Override
                                public void onPermissionNeedExplanation(String permissionName) {

                                    String[] neededPermission = PermissionHelper.declinedPermissions(UpdateProfileActivity.this, PERMISSIONS_CAMERA);
                                    AlertDialog alert = getAlertDialog(neededPermission, "Grant Permission Camera & Storage", "Camera and Storage Permisssion is needed to take photo", permissionHelper);
                                    if (!alert.isShowing()) {
                                        alert.show();
                                    }
                                }

                                @Override
                                public void onPermissionReallyDeclined(String permissionName) {

                                }

                                @Override
                                public void onNoPermissionNeeded() {

                                }
                            };
                            permissionHelper = PermissionHelper.getInstance(UpdateProfileActivity.this, onPermissionCallbackPic);
                            permissionHelper.setForceAccepting(false).request(PERMISSIONS_CAMERA);
                        } else {
                            takePicture();
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    public AlertDialog getAlertDialog(final String[] permissions, String title, String msg, final PermissionHelper permissionHelper) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle(title).create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permissions);
            }
        });
        builder.setMessage(msg);
        return builder;
    }

    public AlertDialog getAlertDialog(final String permission, String title, String msg, final PermissionHelper permissionHelper) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle(title).create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permission);
            }
        });
        builder.setMessage(msg);
        return builder;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem itemSearch = menu.findItem(R.id.itemSearch);
        final MenuItem itemCart = menu.findItem(R.id.itemMyCart);
        itemSearch.setVisible(false);
        itemCart.setVisible(false);
        return true;
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Edit Profile", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void setProfileInfo() {
        try {
            if (!UserDataPreferences.getUserInfo(UpdateProfileActivity.this).equals("")) {
                JSONObject jsonObject = new JSONObject(UserDataPreferences.getUserInfo(UpdateProfileActivity.this));
                etName.setText(jsonObject.has("first_name") ? (jsonObject.isNull("first_name") ? "" : jsonObject.getString("first_name")) : "");
                etLastName.setText(jsonObject.has("last_name") ? (jsonObject.isNull("last_name") ? "" : jsonObject.getString("last_name")) : "");
                etEmail.setText(jsonObject.has("email") ? (jsonObject.isNull("email") ? "" : jsonObject.getString("email")) : "");
                etMobileNo.setText(jsonObject.has("mobile") ? (jsonObject.isNull("mobile") ? "" : jsonObject.getString("mobile")) : "");
                imageUrl = JSONData.getString(jsonObject, "image");
//                if (imageUrl.length() > 0) {
//                    imageLoader = ImageLoader.getInstance();
//                    imageLoader.init(ImageLoaderConfiguration.createDefault(this));
//                    imageLoader.displayImage(Constants.api_ip + "/" + imageUrl, imgRegisterProfilePic, options);
//                }


                if (imageUrl.trim().length() > 0) {
                    imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(UpdateProfileActivity.this));
                    if (imageUrl.contains("http")) {
                        imageLoader.displayImage(imageUrl, imgRegisterProfilePic, options);
                    } else {
                        imageLoader.displayImage(Constants.api_ip + "/" + imageUrl, imgRegisterProfilePic, options);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(
                            data.getData());
                    AppConstant.saveProfilePic(UpdateProfileActivity.this, inputStream);
                    startCropImage();
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:
                /*String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {
                    return;
                }*/
                Log.d("Gallery Result", "Activity Result");
                try {
                    bitmap = ImageCropActivity.cropped;
                    imgRegisterProfilePic.setImageBitmap(bitmap);
                    isProfileSelected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImage() {
        Intent intent = new Intent(UpdateProfileActivity.this, ImageCropActivity.class);
        intent.putExtra("image_path", mFileTemp.getPath());
        intent.putExtra("type", "circle");
      /*  intent.putExtra(CropImage.SCALE, false);

        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 3);*/
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgRegisterProfilePic:
                showAlertDialog();
                break;
            case R.id.btnSignup:
                register(v);
                break;

            default:
                break;
        }
    }

    private void register(View v) {
        strEmail = etEmail.getText().toString();
        strName = etName.getText().toString();
        strLastName = etLastName.getText().toString();
        strMobile = etMobileNo.getText().toString();
        strAddress = etAddress.getText().toString();
        strPasssword = etPassword.getText().toString();
        if (strName.length() > 0) {
            if (strLastName.length() > 0) {
                if (strEmail.length() > 0) {
                    if (AppConstant.isValidEmailAddress(strEmail)) {
                        if (strMobile.length() > 0) {
                            if (strMobile.length() == 10) {
                                if (AppConstant.isNetworkAvailable(this)) {
                                    new UpdateUserTask().execute();
                                } else {
                                    AppConstant.showNetworkError(this);
                                }
                            } else {
                                AppConstant.displayErroMessage(v, "Mobile No. Must be 10 digit.", this);
                            }
                        } else {
                            AppConstant.displayErroMessage(v, "Please enter mobile no.", this);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Please enter valid email.", this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter email id .", this);
                }
            } else {
                AppConstant.displayErroMessage(v, "Please enter Last Name.", this);
            }
        } else {
            AppConstant.displayErroMessage(v, "Please enter First Name.", this);
        }
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

    private class UpdateUserTask extends AsyncTask<Void, Void, String> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(UpdateProfileActivity.this);
        InputStream is;
        String sResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... unsued) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.api_v1 + Constants.api_update_profile);
                httpPost.setHeader("Authorization", "Bearer " + UserDataPreferences.getToken(UpdateProfileActivity.this));
                // httpPost.setHeader("Content-Type","undefined");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("first_name", new StringBody(strName.trim()));
                entity.addPart("last_name", new StringBody(strLastName.trim()));
                entity.addPart("email", new StringBody(strEmail.trim()));
                entity.addPart("mobile", new StringBody(strMobile.trim()));

                if (isProfileSelected) {
                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] data = bos.toByteArray();
                        entity.addPart("image", new ByteArrayBody(data,
                                "myImage.jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // entity.addPart("image", new StringBody("false"));
                }

                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);

                try {
                    is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    sResponse = sb.toString();

                } catch (Exception e) {
                    Log.e("Buffer Error",
                            "Error converting result " + e.toString());
                }
                return sResponse;
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), "Error",
                // Toast.LENGTH_LONG).show();
                Log.e("Do in back" + e.getClass().getName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String sResponse) {

            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    Log.d("Json", sResponse);
                    JSONObject jObj = new JSONObject(sResponse);

                    boolean flag = false;
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    String message = jObj.has("message") ? jObj
                            .getString("message") : "";
                    if (flag) {

                        Toast.makeText(UpdateProfileActivity.this,
                                "Profile Updated Successfully", Toast.LENGTH_LONG)
                                .show();
                        JSONObject dataObj = jObj.getJSONObject("data");
                        UserDataPreferences.saveUserInfo(UpdateProfileActivity.this, dataObj.toString());
//                        Intent mainActivity = new Intent(UpdateProfileActivity.this,
//                                MainActivity.class);
//                        startActivity(mainActivity);
//                        overridePendingTransition(R.anim.animation, R.anim.animation2);
//                        closeActivity();
                        setResult(4, new Intent());
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    } else {
                        if (message.equals("")) {
                            AppConstant.showSingleButtonAlertDialog(
                                    UpdateProfileActivity.this, "Error",
                                    "Something went wrong.\nPlease try again");
                        } else {
                            AppConstant.showSingleButtonAlertDialog(
                                    UpdateProfileActivity.this, "Error", message);
                        }
                    }
                }
            } catch (Exception e) {
                AppConstant.showNetworkError(UpdateProfileActivity.this);
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }
}
