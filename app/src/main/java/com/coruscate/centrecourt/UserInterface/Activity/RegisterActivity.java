package com.coruscate.centrecourt.UserInterface.Activity;

import android.Manifest;
import android.animation.LayoutTransition;
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
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.InternalStorageContentProvider;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressWarnings("ALL")
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {


    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private static final String TAG = "Sign Up";
    private final String PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    boolean isProfileSelected = false;
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
    @InjectView(R.id.btnSignup)
    TypedfacedButton btnSignup;
    @InjectView(R.id.linSignupBottom)
    LinearLayout linSignupBottom;
    @InjectView(R.id.linearLayoutPersionalDetail)
    LinearLayout linearLayoutPersionalDetail;
    @InjectView(R.id.linearLayoutAddressDetail)
    LinearLayout linearLayoutAddressDetail;
    @InjectView(R.id.linearLayoutRegister)
    LinearLayout linearLayoutRegister;
    android.app.AlertDialog alert;
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
    private int selectedPosition = 0;
    private File mFileTemp;
    private Bitmap bitmap;
    private LayoutTransition layoutTransition;
    private boolean openPersionalDetail = true, openAddressDetail = false;
    private String[] cityList;
    private String first_name, last_name, mobile, email, password, cPassword, line1, line2, city, state, country, pincode;
    private AlertDialog builder;
    private PermissionHelper permissionHelper;
    private OnPermissionCallback onPermissionCallbackPic;
    private OnPermissionCallback onPermissionCallbackPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
//        layoutTransition = linearLayoutRegister.getLayoutTransition();
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//        layoutTransition.setDuration(1000);

        try {
            AppConstant.setToolBarColor(RegisterActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (AppConstant.isAndroid5()) {
            btnSignup.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        }

        imgRegisterProfilePic.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        txtPersonalDetail.setOnClickListener(this);
        txtAddressDetail.setOnClickListener(this);
        // etCity.setOnClickListener(this);
        etState.setText("Gujarat");
        etCountry.setText("India");
        etCity.setText("Surat");
        radiobuttonYes.setChecked(true);
        cityList = new String[2];
        cityList[0] = "Surat";
        cityList[1] = "Ahmedabad";
        txtPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        etCity.setText(cityList[which]);
        alert.dismiss();
        selectedPosition = which;
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void showCityDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("City");
        builder.setSingleChoiceItems(cityList, selectedPosition, this);
        alert = builder.create();
        alert.show();
    }

    private void getDataFromUI() {
        first_name = etName.getText().toString().trim();
        last_name = etLastName.getText().toString().trim();
        mobile = etMobileNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString();
        cPassword = etConfirmPassword.getText().toString();
        line1 = etAddress.getText().toString().trim();
        line2 = etAddress2.getText().toString().trim();
        city = etCity.getText().toString().trim();
        state = etState.getText().toString().trim();
        country = etCountry.getText().toString().trim();
        pincode = etPinCode.getText().toString().trim();

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
                            permissionHelper = PermissionHelper.getInstance(RegisterActivity.this, onPermissionCallbackPhoto);
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

                                    String[] neededPermission = PermissionHelper.declinedPermissions(RegisterActivity.this, PERMISSIONS_CAMERA);
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
                            permissionHelper = PermissionHelper.getInstance(RegisterActivity.this, onPermissionCallbackPic);
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
                    AppConstant.saveProfilePic(RegisterActivity.this, inputStream);
                    startCropImage();
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:
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
        Intent intent = new Intent(RegisterActivity.this, ImageCropActivity.class);
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
            case R.id.txtPrivacyPolicy:
                Intent intent = new Intent(this, WebViewPrivacyActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignup:
                register(v);
                break;
            case R.id.txtPersonalDetail:
                if (openPersionalDetail) {
                    linearLayoutAddressDetail.setVisibility(View.VISIBLE);
                    openAddressDetail = true;
                    openPersionalDetail = false;
                    AppConstant.expand(linearLayoutAddressDetail);
                    AppConstant.collapse(linearLayoutPersionalDetail);
                } else {
                    linearLayoutPersionalDetail.setVisibility(View.VISIBLE);
                    openPersionalDetail = true;
                    openAddressDetail = false;
                    AppConstant.expand(linearLayoutPersionalDetail);
                    AppConstant.collapse(linearLayoutAddressDetail);
                }
                break;
            case R.id.txtAddressDetail:
                if (openAddressDetail) {
                    linearLayoutPersionalDetail.setVisibility(View.VISIBLE);
                    openAddressDetail = false;
                    openPersionalDetail = true;
                    AppConstant.expand(linearLayoutPersionalDetail);
                    AppConstant.collapse(linearLayoutAddressDetail);
                } else {
                    linearLayoutAddressDetail.setVisibility(View.VISIBLE);
                    openAddressDetail = true;
                    openPersionalDetail = false;
                    AppConstant.expand(linearLayoutAddressDetail);
                    AppConstant.collapse(linearLayoutPersionalDetail);
                }
                break;
            case R.id.etCity:
                //showCityDialog();
                break;

            default:
                break;
        }
    }

    private void register(View v) {
        getDataFromUI();
        if (first_name.length() > 0) {
            if (mobile.length() > 0) {
                if (mobile.length() == 10) {
                    if (email.length() > 0) {
                        if (AppConstant.isValidEmailAddress(email)) {
                            if (password.length() > 0) {
                                if (password.equals(cPassword)) {
                                    if (city.length() > 0) {
                                        if (state.length() > 0) {
                                            if (country.length() > 0) {
                                                if (checkboxPrivacy.isChecked()) {
                                                    if (AppConstant.isNetworkAvailable(RegisterActivity.this)) {
                                                        new RegisterUser().execute();
                                                    } else {
                                                        AppConstant.showNetworkError(this);
                                                    }
                                                } else {
                                                    AppConstant.displayErroMessage(v, "Accept Privacy Policy.", this);
                                                }
                                            } else {
                                                AppConstant.displayErroMessage(v, "Please enter country", this);
                                            }
                                        } else {
                                            AppConstant.displayErroMessage(v, "Please enter state", this);
                                        }
                                    } else {
                                        AppConstant.displayErroMessage(v, "Please enter city", this);
                                    }
                                } else {
                                    AppConstant.displayErroMessage(v, "Please password not match.", this);
                                }
                            } else {
                                AppConstant.displayErroMessage(v, "Please enter password", this);
                            }
                        } else {
                            AppConstant.displayErroMessage(v, "Please enter Valid email id", this);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Please enter email id", this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, " mobile number must be 10 digit", this);
                }
            } else {
                AppConstant.displayErroMessage(v, "Please enter mobile number", this);
            }
        } else {
            AppConstant.displayErroMessage(v, "Please enter first name", this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }


    private class RegisterUser extends AsyncTask<Void, Void, String> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(RegisterActivity.this);
        InputStream is;
        String sResponse;
        boolean req_as_member;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... unsued) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.api_v1 + Constants.api_register);

                // httpPost.setHeader("Content-Type","undefined");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("mobile", mobile);
                jsonObject.put("line1", line1);
                jsonObject.put("line2", line2);
                jsonObject.put("city", city);
                jsonObject.put("state", state);
                jsonObject.put("country", country);
                jsonObject.put("pincode", pincode);
                JSONArray array = new JSONArray();
                array.put(jsonObject);
                entity.addPart("first_name", new StringBody(first_name));
                entity.addPart("last_name", new StringBody(last_name));
                entity.addPart("email", new StringBody(email));
                entity.addPart("mobile", new StringBody(mobile));
                entity.addPart("password", new StringBody(password));
                entity.addPart("address_book", new StringBody(array.toString()));
                entity.addPart("is_newsletter", new StringBody(radiobuttonYes.isChecked() + ""));
                if (isProfileSelected) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] data = bos.toByteArray();
                    entity.addPart("image", new ByteArrayBody(data,
                            "myImage.jpg"));
                } else {
                    //  entity.addPart("photo", new StringBody("false"));
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

                        Toast.makeText(RegisterActivity.this,
                                "Successfully Registered", Toast.LENGTH_LONG)
                                .show();
                        JSONObject dataObj = jObj.getJSONObject("data");
                        Log.d("data", dataObj.toString());
                        UserDataPreferences.saveToken(RegisterActivity.this, JSONData.getString(dataObj, "token"));
                        UserDataPreferences.saveUserInfo(RegisterActivity.this, dataObj.toString());
                        Intent mainActivity = new Intent(RegisterActivity.this,
                                MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.animation, R.anim.animation2);
                        closeActivity();
                    } else {
                        if (message.equals("")) {
                            AppConstant.showSingleButtonAlertDialog(
                                    RegisterActivity.this, "Error",
                                    "Something went wrong.\nPlease try again");
                        } else {
                            AppConstant.showSingleButtonAlertDialog(
                                    RegisterActivity.this, "Error", message);
                        }
                    }
                }
            } catch (Exception e) {
                AppConstant.showNetworkError(RegisterActivity.this);
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }
}
