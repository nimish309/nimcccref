package com.coruscate.centrecourt.UserInterface.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.progresspageindicator.ProgressPageIndicator;
import com.coruscate.centrecourt.AsynkTask.AddToWishListTask;
import com.coruscate.centrecourt.AsynkTask.SettingTask;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.ImageCropActivity;
import com.coruscate.centrecourt.CustomControls.TouchImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressWarnings("ALL")
public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {


    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    public static final long INTERVAL = 1000 * 60 * 60 * 24;
    private static final String TAG = "Item Detail";
    public static boolean isInWishList, isInCart;
    private final String PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public ImageView imgCart;
    CollapsingToolbarLayout collapsingToolbar;
    AlertDialog alert;
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    @InjectView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @InjectView(R.id.customerRattingBar)
    RatingBar customerRattingBar;
    @InjectView(R.id.txtDays)
    TextView txtDays;
    @InjectView(R.id.txtDescription)
    TextView txtDescription;
    @InjectView(R.id.linearLayoutReview)
    LinearLayout linearLayoutReview;
    double calWeight, calWeightPrice, designPrice, totalPrice = 0, calFlvAmt, calDefFlvPrice, calEggPrice;
    String calFlvSign = "+", calFlvId = "", calEggSign = "+";
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
    private ArrayList<String> itemList;
    private LayoutInflater dataInflater;
    private String[] listItems;
    private String[] weightList;
    private String[] QuantityList;
    private String[] flavoursList;
    private String[] flavoursListDesc;
    private int Qty;
    private JSONObject jObjAddCart = new JSONObject();
    private TextView txtInStock, txtViewMore, txtCart;
    private EditText editTextWeight, editTextQuantity, editTextflavours, editTextDeliveryDate, editTextDeliveryTime, edtxtMessageOnCakes, edtxtSpecialInstruction;
    private ArrayList<String> detailList;
    private int position;
    private Toolbar mToolbar;
    private TextView txtName, txtDesc, txtPrice, txtPriceDetail, txtAvailable, txtDeliveryInfo, txtAvailabilityInfo, txtPricePerUnit;
    private ImageView ImgUploadImage, ImgWishList, imgIconEggLess, imgIconEgg;
    private FrameLayout imgFrameLayout;
    private JSONArray jsonArray;
    private ArrayList<String> itemData;
    private FloatingActionButton floatingButton, floatingButtonAddtocart;
    private RadioGroup radioButtonGroup;
    //upload image
    private Bitmap bitmap;
    private File mFileTemp;
    private String clicableTextView;
    private Calendar myCalendar;
    private boolean isProfileSelected = false;
    private RadioButton radiobuttonWithegg, radiobuttonWithoutegg;
    private String selectedRadioButton, id;
    private String price, kg, sign = "+", amount = "0";
    private float finalAmount;
    private DatePickerDialog dialog;
    private TimePickerDialog picker;
    private AppBarLayout appbar;
    private CoordinatorLayout.LayoutParams param;
    private String type, flavourMinPriceId;
    private RelativeLayout relativeLayoutCart;
    private RatingBar rattingBar;
    private LinearLayout layoutRatingBar, linearLayoutItemAvailLable;
    private int itemAvailableTime = 0;
    private boolean isAvailable = false, isAllowFlavourPrice = false, pricePerPiece;
    private ViewPager viewPager;
    private CheckBox checkboxnoMsg;
    private ArrayList<String> imageArrayList;
    private PermissionHelper permissionHelper;
    private OnPermissionCallback onPermissionCallbackPic;
    private OnPermissionCallback onPermissionCallbackPhoto;
    private android.support.v7.app.AlertDialog builder;
    private int selectedPositionWeight = 0, selectedPositionFlavour = 0, selectedPositionQty = 0, selectItem, itemAvailable = 0;
    private TextInputLayout txtInputFlavour, txtInputWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);
        ButterKnife.inject(this);
   /*     try {
            AppConstant.setToolBarColor(ItemDetailActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        position = getIntent().getIntExtra("position", 0);
        type = getIntent().getStringExtra("type");
        //jsonObject=new JSONObject(getIntent().getStringExtra("detail"));

        initializeView();
        setInformation();
        setViewPagerAdapter();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    AppConstant.UPLOAD_ITEM_PIC_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(),
                    AppConstant.UPLOAD_ITEM_PIC_FILE_NAME);
        }
        editTextWeight.setOnClickListener(this);
        editTextQuantity.setOnClickListener(this);
        editTextflavours.setOnClickListener(this);
        editTextDeliveryDate.setOnClickListener(this);
        editTextDeliveryTime.setOnClickListener(this);
        floatingButton.setOnClickListener(this);
        txtViewMore.setOnClickListener(this);
        txtDeliveryInfo.setOnClickListener(this);
        txtAvailabilityInfo.setOnClickListener(this);
        editTextWeight.setOnFocusChangeListener(this);
        editTextQuantity.setOnFocusChangeListener(this);
        editTextflavours.setOnFocusChangeListener(this);
        editTextDeliveryDate.setOnFocusChangeListener(this);
        editTextDeliveryTime.setOnFocusChangeListener(this);
        floatingButtonAddtocart.setOnClickListener(this);
        ImgWishList.setOnClickListener(this);
        layoutRatingBar.setOnClickListener(this);
        editTextWeight.setFocusable(true);
        editTextQuantity.setFocusable(true);
        editTextflavours.setFocusable(true);
        editTextDeliveryDate.setFocusable(true);
        editTextDeliveryTime.setOnClickListener(this);
        // btnAddToCart.setOnClickListener(this);

        checkboxnoMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtxtMessageOnCakes.setText("");
                    edtxtMessageOnCakes.setEnabled(false);
                    edtxtMessageOnCakes.setBackground(null);
                } else {
                    edtxtMessageOnCakes.setEnabled(true);
                    edtxtMessageOnCakes.setBackground(getDrawable(R.drawable.edittext_selecter));
                }
            }
        });
        new SettingTask(this, true).execute();
    }

    private void setViewPagerAdapter() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(imageArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(imageArrayList.size());
//        viewPager.setPageTransformer(true, new FadePageTransformer());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.itemSearch).setVisible(false);
        final MenuItem itemCart = menu.findItem(R.id.itemMyCart);
        itemCart.setVisible(true);
        relativeLayoutCart = (RelativeLayout) MenuItemCompat.getActionView(itemCart);
        final View viewCart = menu.findItem(R.id.itemMyCart).getActionView();
        imgCart = (ImageView) viewCart.findViewById(R.id.item_cart);
        txtCart = (TextView) viewCart.findViewById(R.id.txtCartItemCount);
        imgCart.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        setCartCount();
        relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this, MyCart.class);
                startActivityForResult(intent, 7);
                overridePendingTransition(R.anim.animation, R.anim.animation2);
            }
        });
        return true;
    }

    private void setCartCount() {
        try {
            int count = UserDataPreferences.getCartCount(this);
            if (count > 0) {
                txtCart.setText(count + "");
                txtCart.setVisibility(View.VISIBLE);
            } else {
                txtCart.setText("");
                txtCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImgUploadImage:
                openImageEditPopup();
                break;
            case R.id.txtWeight:
                onSaveMain(weightList, "Weight", editTextWeight);
                break;
            case R.id.txtQuantity:
                onSaveMain(QuantityList, "Quantity", editTextQuantity);
                break;
            case R.id.txtflavours:
                onSaveMain(flavoursList, "Flavours", editTextflavours);
                break;
            case R.id.txtDeliveryDate:
                dialog.show();
                break;
            case R.id.txtDeliveryTime:
                picker.show();
                break;
            case R.id.floatingButton:
                showAlertDialog();
                break;
//            case R.id.txtViewMore:
//                if (AppConstant.isNetworkAvailable(this)) {
//                    new GetReviewListTask().execute();
//                } else {
//                    AppConstant.showNetworkError(this);
//                }
//                break;
            case R.id.floatingButtonAddtocart:
                if (AppConstant.isNetworkAvailable(ItemDetailActivity.this)) {
//                    new AddToCartTask(false).execute();
                    if (itemAvailable != 2) {
                        checkItemAvalibility(true, true);
                    } else {
                        AppConstant.displayErroMessage(v, "Item out of stock", ItemDetailActivity.this);
                    }
                } else {
                    AppConstant.showNetworkError(ItemDetailActivity.this);
                }
                break;
            case R.id.ImgWishList:
                if (AppConstant.isNetworkAvailable(ItemDetailActivity.this)) {
                    ImgWishList.setClickable(false);
                    if (!isInWishList) {
                        ImgWishList.setImageResource(R.drawable.icon_heart_2);
                    } else {
                        ImgWishList.setImageResource(R.drawable.icon_heart_1);
                    }
                    AppConstant.scaleAnimation(ImgWishList, ItemDetailActivity.this);
                    new AddToWishListTask(ImgWishList, id, ItemDetailActivity.this, isInWishList, type, position, null).execute();
                } else {
                    AppConstant.showNetworkError(ItemDetailActivity.this);
                }
                break;
//            case R.id.layoutRatingBar:
//                if (AppConstant.isNetworkAvailable(this)) {
//                    new GetReviewListTask().execute();
//                } else {
//                    AppConstant.showNetworkError(this);
//                }
//                break;
            case R.id.txtDeliveryInfo:
                showDeliveryInfo();

                break;
            case R.id.txtAvailabilityInfo:
                if (itemAvailable != 2) {
                    checkItemAvalibility(true, false);

                } else {
                    AppConstant.displayErroMessage(v, "Item out of stock", ItemDetailActivity.this);
                }
                break;
            default:
                return;
        }

    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @SuppressLint("NewApi")
    private void initializeView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(AppConstant.spanFont("", this));
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        imageArrayList = new ArrayList();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ImgWishList = (ImageView) findViewById(R.id.ImgWishList);
        imgIconEggLess = (ImageView) findViewById(R.id.imgIconEggLess);
        imgIconEgg = (ImageView) findViewById(R.id.imgIconEgg);
        txtName = (TextView) findViewById(R.id.txtName);
        txtDesc = (TextView) findViewById(R.id.txtNewSession);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtPriceDetail = (TextView) findViewById(R.id.txtPriceDetail);
        txtPricePerUnit = (TextView) findViewById(R.id.txtPricePerUnit);
        floatingButtonAddtocart = (FloatingActionButton) findViewById(R.id.floatingButtonAddtocart);
        itemData = new ArrayList<>();
        ImgUploadImage = (ImageView) findViewById(R.id.ImgUploadImage);
        imgFrameLayout = (FrameLayout) findViewById(R.id.imgFrameLayout);
        ImgUploadImage.setOnClickListener(this);
        radioButtonGroup = (RadioGroup) findViewById(R.id.radioButtonGroup);
        radiobuttonWithoutegg = (RadioButton) findViewById(R.id.radiobuttonWithoutegg);
        radiobuttonWithegg = (RadioButton) findViewById(R.id.radiobuttonWithegg);
        radiobuttonWithegg.setText(AppConstant.spanFontRegular("With egg", this));
        radiobuttonWithoutegg.setText(AppConstant.spanFontRegular("Without egg", this));
        txtAvailable = (TextView) findViewById(R.id.txtAvailable);
        rattingBar = (RatingBar) findViewById(R.id.rattingBar);
        layoutRatingBar = (LinearLayout) findViewById(R.id.layoutRatingBar);
        txtInputWeight = (TextInputLayout) findViewById(R.id.txtInputWeight);
        txtInputFlavour = (TextInputLayout) findViewById(R.id.txtInputFlavour);
        DrawableCompat.setTint(DrawableCompat.wrap(((LayerDrawable) rattingBar.getProgressDrawable()).getDrawable(2)), Color.YELLOW);
        if (AppConstant.isAndroid5()) {
            floatingButtonAddtocart.setBackground(getDrawable(R.drawable.ripple_fab));
        }
        linearLayoutItemAvailLable = (LinearLayout) findViewById(R.id.linearLayoutItemAvailLable);
        checkboxnoMsg = (CheckBox) findViewById(R.id.checkboxnoMsg);
        editTextWeight = (EditText) findViewById(R.id.txtWeight);
        editTextQuantity = (EditText) findViewById(R.id.txtQuantity);
        editTextflavours = (EditText) findViewById(R.id.txtflavours);
        editTextDeliveryDate = (EditText) findViewById(R.id.txtDeliveryDate);
        editTextDeliveryTime = (EditText) findViewById(R.id.txtDeliveryTime);
        edtxtMessageOnCakes = (EditText) findViewById(R.id.edtxtMessageOnCakes);
        edtxtSpecialInstruction = (EditText) findViewById(R.id.edtxtSpecialInstruction);
        txtDeliveryInfo = (TextView) findViewById(R.id.txtDeliveryInfo);
        txtAvailabilityInfo = (TextView) findViewById(R.id.txtAvailabilityInfo);
        floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        txtViewMore = (TextView) findViewById(R.id.txtViewMore);
        txtInStock = (TextView) findViewById(R.id.txtInStock);
        DrawableCompat.setTint(DrawableCompat.wrap(((LayerDrawable) customerRattingBar.getProgressDrawable()).getDrawable(2)), Color.YELLOW);
//        if (AppConstant.isAndroid5()) {
//            floatingButton.setBackground(getDrawable(R.drawable.ripple_fab));
//        }
        radioButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobuttonWithegg:
                        setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), "eggwith");
                        break;
                    case R.id.radiobuttonWithoutegg:
                        setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), "eggless");
                        break;
                }
            }
        });
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        myCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(ItemDetailActivity.this, this, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
        picker = new TimePickerDialog(ItemDetailActivity.this, this, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setCartCount();
    }

    private void setInformation() {
        try {
            JSONObject jObject;
            String imageUrl;
            JSONArray jArray;
           /* jsonArray = new JSONArray(UserDataPreferences.getItemCategory(ItemDetailActivity.this));
            AppConstant.copyJSONArray(jsonArray, itemData);*/
          /*  itemData = UserDataPreferences.getCategoryItemList(ItemDetailActivity.this);
            jsonObject = new JSONObject(itemData.get(position).toString());
            Log.d("item_data", itemData.get(position).toString());
            */

            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("detail"));

            try {
                JSONArray imageArray = jsonObject.getJSONArray("images");
                JSONObject imageobject;
                String url;
                for (int i = 0; i < imageArray.length(); i++) {
                    imageobject = imageArray.getJSONObject(i);
                    imageArrayList.add(JSONData.getString(imageobject, "large"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONObject object = jsonObject.getJSONObject("minimum_price");
                double minPrice = JSONData.getDouble(object, "base_price");
                double minWeight = JSONData.getDouble(object, "base_weight");
                flavourMinPriceId = JSONData.getString(object, "flavour_id");
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("price_per_kg");
                    pricePerPiece = false;
                    if (jsonArray != null && jsonArray.length() > 0) {
//                        if (minWeight == 1)
                        txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + "( " + minWeight + " KG )");
//                        else if (minWeight < 1) {
//                            txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + " ( " + minWeight + "KG)");
//                        } else {
//                            txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + " ( " + minWeight + "KG )");
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jObj = jsonObject.getJSONObject("price_per_piece");
                    pricePerPiece = true;
                    checkboxnoMsg.setChecked(true);
                    checkboxnoMsg.setVisibility(View.GONE);
                    edtxtMessageOnCakes.setVisibility(View.GONE);
                    if (jObj != null) {
                        if (minWeight <= 1) {
                            txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice);
                        } else {
                            txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + "/" + minWeight + "pcs");
                        }

                    }

                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            id = JSONData.getString(jsonObject, "_id");
            txtName.setText((jsonObject.has("name") ? jsonObject.getString("name") : ""));
            //final int min_order = JSONData.getInt(jsonObject, "min_ordered_quantity");
            final int min_order = jsonObject.has("min_ordered_quantity") ? (jsonObject.isNull("min_ordered_quantity") ? 1 : jsonObject.getInt("min_ordered_quantity")) : 1;
            // final int max_order = JSONData.getInt(jsonObject, "max_ordered_quantity");
            final int max_order = jsonObject.has("max_ordered_quantity") ? (jsonObject.isNull("max_ordered_quantity") ? 10 : jsonObject.getInt("max_ordered_quantity")) : 10;


            if (min_order > 0) {
                editTextQuantity.setText(min_order + "");
                QuantityList = new String[(max_order - min_order) + 1];
                for (int i = min_order; i <= max_order; i++) {
                    QuantityList[i - min_order] = i + "";
                }
            } else {
                QuantityList = new String[0];
            }
//            try {
//                final String desc = jsonObject.has("desc") ? (jsonObject.isNull("desc") ? "" : jsonObject.getString("desc")) : "";
//                if (desc.toString().equals("")) {
//                    txtDesc.setVisibility(View.GONE);
//                } else {
//                    txtDesc.setVisibility(View.VISIBLE);
//                    txtDesc.setText(desc);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                final boolean allow_user_image = jsonObject.has("is_allow_user_image") ? jsonObject.getBoolean("is_allow_user_image") : false;
                if (allow_user_image) {
                    param = (CoordinatorLayout.LayoutParams) floatingButton.getLayoutParams();
                    param.setAnchorId(R.id.appbar);
                    floatingButton.setLayoutParams(param);
                    floatingButton.setVisibility(View.VISIBLE);
                } else {
                    floatingButton.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                itemAvailable = JSONData.getInt(jsonObject, "stock_status");
                if (itemAvailable == 0) {
                    linearLayoutItemAvailLable.setVisibility(View.GONE);
                } else if (itemAvailable == 1) {
                    txtInStock.setText("In stock ");
                    txtInStock.setTextColor(getResources().getColor(R.color.labelGreen));
                } else if (itemAvailable == 2) {
                    txtInStock.setText("Out of stock ");
                    txtInStock.setTextColor(getResources().getColor(R.color.labelRed));
                } else if (itemAvailable == 3) {
                    txtInStock.setText("Pre order");
                    txtInStock.setTextColor(getResources().getColor(R.color.labelGreen));
                } else if (itemAvailable == 4) {
                    linearLayoutItemAvailLable.setVisibility(View.GONE);
                }

                itemAvailableTime = JSONData.getInt(jsonObject, "product_available_in");

                if (itemAvailableTime != 0) {
                    txtAvailable.setVisibility(View.VISIBLE);
                    txtAvailable.setText("Minimum " + itemAvailableTime + " Hour Advance");
                } else {
                    txtAvailable.setVisibility(View.GONE);
                }

            } catch (Exception q) {
                q.printStackTrace();
            }

            try {
                jArray = jsonObject.getJSONArray("price_per_kg");
                if (jArray.length() > 0 && jArray != null) {
                    weightList = new String[jArray.length()];
                    for (int i = 0; i < jArray.length(); i++) {
                        jObject = jArray.getJSONObject(i);
                        final String price = jObject.has("price") ? (jObject.isNull("price") ? "" : jObject.getString("price")) : "";
                        final String kg = jObject.has("weight") ? (jObject.isNull("weight") ? "" : jObject.getString("weight")) : "";

                        weightList[i] = kg + "";
                    }
                    weightList = sortWeightArray(weightList);
                    if (weightList.length > 0) {
                        editTextWeight.setText(weightList[0] + "");
                    }
                }

            } catch (Exception e) {
            }
            try {
                jObject = jsonObject.getJSONObject("price_per_piece");
                weightList = new String[1];
                final String price = jObject.has("price") ? (jObject.isNull("price") ? "" : jObject.getString("price")) : "";
                final String kg = jObject.has("weight") ? (jObject.isNull("weight") ? "0" : jObject.getString("weight")) : "0";
                weightList[0] = kg + "";
                editTextWeight.setText(weightList[0] + "");
                if (weightList[0].toString().equals("0")) {
                    txtInputWeight.setVisibility(View.GONE);
                }

            } catch (Exception j) {
                j.printStackTrace();
            }
            try {
                jArray = jsonObject.getJSONArray("flavours");
                if (jArray.length() > 0 && jArray != null) {
                    flavoursList = new String[jArray.length()];
                    flavoursListDesc = new String[jArray.length()];
                    String strDesc = "";
                    for (int i = 0; i < jArray.length(); i++) {
                        jObject = jArray.getJSONObject(i);
                        try {
                            flavoursList[i] = JSONData.getString(jObject, "name");
                            flavoursListDesc[i] = JSONData.getString(jObject, "desc");
                            if (flavourMinPriceId.toString().equals(JSONData.getString(jObject, "id"))) {
                                selectedPositionFlavour = i;
                                calFlvId = JSONData.getString(jObject, "id");
                                editTextflavours.setText(JSONData.getString(jObject, "name"));
                                strDesc = JSONData.getString(jObject, "desc");
                                txtDesc.setText(JSONData.getString(jObject, "desc"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (strDesc.length() == 0) {
                        txtDesc.setVisibility(View.GONE);
                    }
                }
            } catch (Exception t) {
                if (flavoursList == null) {
                    txtDesc.setVisibility(View.GONE);
                    txtInputFlavour.setVisibility(View.GONE);
                }
                t.printStackTrace();
            }

            jObject = JSONData.getJSONObjectDefNull(jsonObject, "ratings");
            if (null != jObject) {
                rattingBar.setRating(Float.parseFloat(String.valueOf(JSONData.getDouble(jObject, "average"))));
            } else {
                rattingBar.setRating(0);
            }

            editTextDeliveryDate.setText(AppConstant.sdf.format(myCalendar.getTime()));
            editTextDeliveryTime.setText(AppConstant.stf.format(myCalendar.getTime()));
            jObject = jsonObject.getJSONObject("main_image");
            imageUrl = jObject.has("medium") ? (jObject.isNull("medium") ? "" : jObject.getString("medium")) : "";

            imageArrayList.add(0, imageUrl);

            isInWishList = jsonObject.has("is_in_wishlist") ? jsonObject.getBoolean("is_in_wishlist") : false;
            isInCart = jsonObject.has("is_in_cart") ? jsonObject.getBoolean("is_in_cart") : false;
            if (isInWishList) {
                ImgWishList.setImageResource(R.drawable.icon_heart_2);
            } else {
                ImgWishList.setImageResource(R.drawable.icon_heart_1);
            }
//            setImage(imageUrl, ImgDetailInformationImage);
            if (JSONData.getBoolean(jsonObject, "eggless")) {
                radiobuttonWithoutegg.setVisibility(View.VISIBLE);
                imgIconEggLess.setVisibility(View.VISIBLE);
            } else {
                radiobuttonWithoutegg.setVisibility(View.GONE);
                imgIconEggLess.setVisibility(View.GONE);
            }
            if (JSONData.getBoolean(jsonObject, "eggwith")) {
                radiobuttonWithegg.setVisibility(View.VISIBLE);
                imgIconEgg.setVisibility(View.VISIBLE);
            } else {
                radiobuttonWithegg.setVisibility(View.GONE);
                imgIconEggLess.setVisibility(View.GONE);
            }
            if (JSONData.getBoolean(jsonObject, "eggwith")) {
                radiobuttonWithegg.setChecked(true);
                setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), "eggwith");
            } else if (JSONData.getBoolean(jsonObject, "eggless")) {
                radiobuttonWithoutegg.setChecked(true);
                setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), "eggless");
            } else {
                setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), "");
            }
            try {
//                jObject = JSONData.getJSONObjectDefNull(jsonObject, "rating_review");
//                if (jObject != null) {
//                    linearLayoutReview.setVisibility(View.VISIBLE);
//                    txtViewMore.setText("View all reviews");
//                    JSONObject user = JSONData.getJSONObjectDefNull(jObject, "user");
//                    if (user != null) {
//                        txtCustomerName.setText((JSONData.getString(user, "first_name") + " " + JSONData.getString(user, "last_name")).trim());
//                    }
//                    customerRattingBar.setRating(Float.parseFloat(String.valueOf(JSONData.getInt(jObject, "rating"))));
//                    txtDays.setText(AppConstant.getTimeDiff(JSONData.getString(jObject, "created_at")));
//                    txtDescription.setText(JSONData.getString(jObject, "review"));
//                } else {
                linearLayoutReview.setVisibility(View.GONE);
                txtViewMore.setText("Customer Reviews");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String[] sortWeightArray(String[] array) {
        double[] sortArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            sortArray[i] = Double.parseDouble(array[i]);
        }
        Arrays.sort(sortArray);
        String[] weightArr = new String[sortArray.length];
        for (int i = 0; i < sortArray.length; i++) {
            weightArr[i] = sortArray[i] + "";
        }
        return weightArr;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (radiobuttonWithegg.getId() == radioButtonGroup.getCheckedRadioButtonId()) {
            selectedRadioButton = "eggwith";
        } else if (radiobuttonWithoutegg.getId() == radioButtonGroup.getCheckedRadioButtonId()) {
            selectedRadioButton = "eggless";
        } else {
            selectedRadioButton = "";
        }
        if (clicableTextView.equals("Weight")) {
            editTextWeight.setText(weightList[which]);
            selectedPositionWeight = which;
            setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), selectedRadioButton);
        } else if (clicableTextView.equals("Quantity")) {
            editTextQuantity.setText(QuantityList[which]);
            selectedPositionQty = which;
            setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), selectedRadioButton);
        } else if (clicableTextView.equals("Flavours")) {
            editTextflavours.setText(flavoursList[which]);
            txtDesc.setText(flavoursListDesc[which]);
            selectedPositionFlavour = which;
            setPrice(editTextWeight.getText().toString(), editTextQuantity.getText().toString(), editTextflavours.getText().toString(), selectedRadioButton);
        }
        alert.dismiss();
    }

    public void onSaveMain(String[] listItem, String title, EditText textView) {
        if (textView.getId() == editTextWeight.getId()) {
            selectItem = selectedPositionWeight;
            clicableTextView = "Weight";
        } else if (textView.getId() == editTextQuantity.getId()) {
            selectItem = selectedPositionQty;
            clicableTextView = "Quantity";
        } else if (textView.getId() == editTextflavours.getId()) {
            selectItem = selectedPositionFlavour;
            clicableTextView = "Flavours";
        }
        listItems = listItem;
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(listItems, selectItem, this);
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Log.d("Date", myCalendar.getTimeInMillis() + "");
        editTextDeliveryDate.setText(AppConstant.sdf.format(myCalendar.getTime()));
        // checkTime(false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        myCalendar.set(Calendar.MINUTE, minute);
        Log.d("time", myCalendar.getTimeInMillis() + "");
        editTextDeliveryTime.setText(AppConstant.stf.format(myCalendar.getTime()));
        // checkTime(false);
    }

    private void setImage(String imageUrl, ImageView imageViewIcon) {
        if (!imageUrl.equals("")) {
            Glide.with(ItemDetailActivity.this)
                    .load(Constants.api_ip + imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .crossFade()
                    .into(imageViewIcon);
            // AppConstant.translateAnimation(imageViewIcon, ItemDetailActivity.this);

        }
    }

    private void showAlertDialog() {

        String[] array = {"Pick From Gallery", "Take Photo"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
                            permissionHelper = PermissionHelper.getInstance(ItemDetailActivity.this, onPermissionCallbackPhoto);
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
                                    String[] neededPermission = PermissionHelper.declinedPermissions(ItemDetailActivity.this, PERMISSIONS_CAMERA);
                                    android.support.v7.app.AlertDialog alert = getAlertDialog(neededPermission, "Grant Permission Camera & Storage", "Camera and Storage Permisssion is needed to take photo", permissionHelper);
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
                            permissionHelper = PermissionHelper.getInstance(ItemDetailActivity.this, onPermissionCallbackPic);
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

    public android.support.v7.app.AlertDialog getAlertDialog(final String[] permissions, String title, String msg, final PermissionHelper permissionHelper) {
        if (builder == null) {
            builder = new android.support.v7.app.AlertDialog.Builder(this)
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

    public android.support.v7.app.AlertDialog getAlertDialog(final String permission, String title, String msg, final PermissionHelper permissionHelper) {
        if (builder == null) {
            builder = new android.support.v7.app.AlertDialog.Builder(this)
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

        }
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private void setPrice(String weight, String Quantity, String flavour, String egglessOrWith) {
        calWeight = 0.0;
        calWeightPrice = 0.0;
        designPrice = 0.0;
        totalPrice = 0.0;
        calFlvAmt = 0.0;
        calDefFlvPrice = 0.0;
        calEggPrice = 0.0;
        try {

            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("detail"));
            JSONObject object, jobj, eggJObject;
            JSONArray jsonArray;
            eggJObject = UserDataPreferences.getEggPrice(ItemDetailActivity.this);
            if (eggJObject != null) {
                calEggSign = JSONData.getString(eggJObject, "sign");
                calEggPrice = JSONData.getDouble(eggJObject, "amount");
            }
            try {
                jsonArray = jsonObject.getJSONArray("price_per_kg");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.getJSONObject(i);
                        calWeight = JSONData.getDouble(object, "weight");
                        if (weight.toString().equals(calWeight + "")) {
                            calWeightPrice = JSONData.getDouble(object, "price");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                object = jsonObject.getJSONObject("price_per_piece");
                calWeight = JSONData.getDouble(object, "weight");
                calWeightPrice = JSONData.getDouble(object, "price");

            } catch (Exception e) {
                e.printStackTrace();
            }
            designPrice = JSONData.getDouble(jsonObject, "design_price");
            try {
                jsonArray = jsonObject.getJSONArray("flavours");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.getJSONObject(i);
                        if (flavour.toString().equals(object.has("name") ? (object.isNull("name") ? "" : object.getString("name")) : "")) {
                            calFlvId = object.has("id") ? (object.isNull("id") ? "" : object.getString("id")) : "";
                            isAllowFlavourPrice = JSONData.getBoolean(object, "is_allow_flavour_price");
                            calDefFlvPrice = JSONData.getDouble(object, "price_per_kg");
                            jsonArray = object.getJSONArray("price_by_weight");
                            try {
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jobj = jsonArray.getJSONObject(j);
                                    final String wgt = jobj.has("weight") ? (jobj.isNull("weight") ? "" : jobj.getString("weight")) : "";
                                    if (wgt.equals(weight)) {
                                        calFlvSign = jobj.has("sign") ? (jobj.isNull("sign") ? "" : jobj.getString("sign")) : "";
                                        calFlvAmt = JSONData.getDouble(jobj, "amount");
                                        break;
                                    }
                                    if (Double.parseDouble(wgt) == 1) {
                                        if (Double.parseDouble(weight) > 1) {
                                            calFlvSign = jobj.has("sign") ? (jobj.isNull("sign") ? "" : jobj.getString("sign")) : "";
                                            calFlvAmt = JSONData.getDouble(jobj, "amount");
                                            calFlvAmt *= Double.parseDouble(weight);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            totalPrice += designPrice;
            totalPrice += calWeightPrice;
            if (isAllowFlavourPrice) {
                calDefFlvPrice *= calWeight;
                totalPrice += calDefFlvPrice;
                if (calFlvSign.equals("+")) {
                    totalPrice += calFlvAmt;
                } else if (calFlvSign.equals("-")) {
                    totalPrice -= calFlvAmt;
                }
            }
            if (!pricePerPiece) {
                if (egglessOrWith.toString().equals("eggwith")) {
                    if (calEggSign.toString().equals("+")) {
                        totalPrice += (calEggPrice * Double.parseDouble(weight));
                    } else if (calEggSign.toString().equals("-")) {
                        totalPrice -= (calEggPrice * Double.parseDouble(weight));
                    }
                }
            }
            if (pricePerPiece) {
                txtPriceDetail.setVisibility(View.GONE);
                txtPricePerUnit.setText(AppConstant.rupee_symbol + totalPrice + "/pc");
            } else {
//                if (Double.parseDouble(weight) == 1)
//                    txtPricePerUnit.setText(AppConstant.rupee_symbol + totalPrice + "/kg");
//                else if (Double.parseDouble(weight) < 1) {
//                    txtPricePerUnit.setText(AppConstant.rupee_symbol + totalPrice + "/" + (int) (Double.parseDouble(weight) * 1000) + "g");
//                } else {
//                    txtPricePerUnit.setText(AppConstant.rupee_symbol + totalPrice + "/" + weight + "kg");
                txtPriceDetail.setText("[ Flavour Price: " + (int) (totalPrice - designPrice) + "  Design Price: " + (int) designPrice + " ]");
                txtPricePerUnit.setText(AppConstant.rupee_symbol + (int) totalPrice + " ( " + weight + " KG )");
            }


            totalPrice *= Integer.parseInt(Quantity);

            txtPrice.setText(AppConstant.rupee_symbol + (int) totalPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ItemDetailActivity==>", "requestCode==>" + requestCode + "\nresultCode==>" + resultCode);
        if (requestCode == 7 && resultCode == 8) {
            Intent i1 = new Intent();
            setResult(8, i1);
            finish();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(
                            data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    AppConstant.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    // AppConstant.saveProfilePic(DetailInformationActivity.this, inputStream);
                    startCropImage();
                } catch (Exception e) {
                    // Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:
                try {
                    bitmap = ImageCropActivity.cropped;
                    ImgUploadImage.setImageBitmap(bitmap);
                    imgFrameLayout.setVisibility(View.VISIBLE);
                    isProfileSelected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImage() {
        Intent intent = new Intent(ItemDetailActivity.this, ImageCropActivity.class);
        intent.putExtra("image_path", mFileTemp.getPath());
        intent.putExtra("type", "ractangle");
      /*  intent.putExtra(CropImage.SCALE, false);

        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 3);*/
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (radiobuttonWithegg.getId() == radioButtonGroup.getCheckedRadioButtonId()) {
            selectedRadioButton = "eggwith";
        } else {
            selectedRadioButton = "eggless";
        }
        if (v.getId() == R.id.txtWeight) {
            if (hasFocus) {
                onSaveMain(weightList, "Weight", editTextWeight);
            }
        } else if (v.getId() == R.id.txtQuantity) {
            if (hasFocus) {
                onSaveMain(QuantityList, "Quantity", editTextQuantity);
            }
        } else if (v.getId() == R.id.txtflavours) {
            if (hasFocus) {
                onSaveMain(flavoursList, "Flavours", editTextflavours);
            }
        } else if (v.getId() == R.id.txtDeliveryDate) {
            if (hasFocus) {
                dialog.show();
            }
        } else if (v.getId() == R.id.txtDeliveryTime) {
            if (hasFocus) {
                picker.show();
            }
        }

    }

    private void closeActivity() {
        setResult(-1, new Intent());
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private MultipartEntity getEntity() {
        try {
//            entity.addPart("", new StringBody());
            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            entity.addPart("item_id", new StringBody(id));
            entity.addPart("weight", new StringBody(editTextWeight.getText().toString()));
            entity.addPart("qty", new StringBody(editTextQuantity.getText().toString()));
            entity.addPart("flavour_id", new StringBody(calFlvId));
            if (radiobuttonWithegg.isChecked()) {
                entity.addPart("eggwith", new StringBody("true"));
            } else if (radiobuttonWithoutegg.isChecked()) {
                entity.addPart("eggless", new StringBody("true"));
            }
            entity.addPart("delivery_date", new StringBody(String.valueOf(myCalendar.getTimeInMillis())));
            entity.addPart("delivery_time", new StringBody(String.valueOf(myCalendar.getTimeInMillis())));
            entity.addPart("message", new StringBody(edtxtMessageOnCakes.getText().toString()));
            entity.addPart("instruction", new StringBody(edtxtSpecialInstruction.getText().toString()));
            if (isProfileSelected) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                entity.addPart("user_custom_image", new ByteArrayBody(data,
                        "myImage.jpg"));
            } else {
                // entity.addPart("user_custom_image", new StringBody("null"));
            }


            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openImageEditPopup() {
        String[] array = {"Replace", "Remove"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
        builder.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showAlertDialog();
                        break;
                    case 1:
                        removeImage();
                        break;
                }
            }
        });
        builder.show();
    }

    private void removeImage() {
        imgFrameLayout.setVisibility(View.GONE);
        ImgUploadImage.setImageResource(0);
        isProfileSelected = false;
    }

//    private class GetReviewListTask extends AsyncTask<Void, Void, Void> {
//
//        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(ItemDetailActivity.this);
//        private ArrayList<String> reviewList = new ArrayList<>();
//        private JSONObject userReviewObject;
//        private int responseCode;
//        private String message;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            JSONParser jParser = new JSONParser(ItemDetailActivity.this);
//            JSONStringer jsonStringer;
//            try {
//                jsonStringer = new JSONStringer().object().key("product_id").value(id).endObject();
//                String[] data = jParser.sendPostReq(Constants.api_v1 + Constants.api_productwise_rating_review, jsonStringer.toString());
//                responseCode = Integer.parseInt(data[0]);
//                if (responseCode == 200) {
//                    JSONObject jsonObject = new JSONObject(data[1]);
//                    message = JSONData.getString(jsonObject, "message");
//                    if (JSONData.getBoolean(jsonObject, "flag")) {
////                        AppConstant.copyJSONArray(JSONData.getJSONArray(jsonObject, "data"), reviewList);
//                        JSONObject dataObject = JSONData.getJSONObjectDefNull(jsonObject, "data");
//                        if (dataObject != null) {
//                            AppConstant.copyJSONArray(JSONData.getJSONArray(dataObject, "rating_reviews"), reviewList);
//                            JSONArray itemArray = JSONData.getJSONArray(dataObject, "rating_review_of_user");
//                            if (itemArray.length() > 0) {
//                                userReviewObject = itemArray.getJSONObject(0);
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//            super.onPostExecute(aVoid);
//            try {
//                if (dialog.isShowing())
//                    dialog.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                if (responseCode == 200) {
////                    if (reviewList.size() > 0) {
//                    Intent intent = new Intent(ItemDetailActivity.this, ItemReviewActivity.class);
//                    intent.putStringArrayListExtra("reviewList", reviewList);
//                    intent.putExtra("product_id", id);
//                    if (userReviewObject != null) {
//                        intent.putExtra("userReviewObject", userReviewObject.toString());
//                    }
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.animation, R.anim.animation2);
////                    } else {
////                        AppConstant.showToastLong(ItemDetailActivity.this, message);
////                    }
//                } else {
//                    AppConstant.unableConnectServer(ItemDetailActivity.this);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    private void showDeliveryInfo() {
        txtDeliveryInfo.setClickable(false);
        Dialog deliveryInfo = new Dialog(this);
        deliveryInfo.getWindow().setBackgroundDrawableResource(R.color.activityBackground);
        deliveryInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deliveryInfo.setContentView(R.layout.delivery_information);
        deliveryInfo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                txtDeliveryInfo.setClickable(true);
            }
        });
        JSONArray jsonArray = UserDataPreferences.getDeliveryInfo(this);
        if (null != jsonArray && jsonArray.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            LinearLayout layoutInfo = (LinearLayout) deliveryInfo.findViewById(R.id.layoutInfo);
            TextView txtDeliveryTime, txtDeliveryCharge, txtDeliveryType;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject item = jsonArray.getJSONObject(i);
                    Log.d("item ", item.toString());
                    View order_item_row = (LinearLayout) LayoutInflater.from(this).inflate(
                            R.layout.delivery_information_row, null);
                    txtDeliveryTime = (TextView) order_item_row.findViewById(R.id.txtDeliveryTime);
                    txtDeliveryCharge = (TextView) order_item_row.findViewById(R.id.txtDeliveryCharge);
                    txtDeliveryType = (TextView) order_item_row.findViewById(R.id.txtDeliveryType);

                    txtDeliveryTime.setText(sdf.format(JSONData.getLong(item, "start_time")) + " - " +
                            sdf.format(JSONData.getLong(item, "end_time")));
                    txtDeliveryCharge.setText(JSONData.getString(item, "price"));
                    txtDeliveryType.setText(JSONData.getString(item, "name"));
                    layoutInfo.addView(order_item_row);
                    if (jsonArray.length() - 1 == i) {
                        View order_item_row1 = (LinearLayout) LayoutInflater.from(this).inflate(
                                R.layout.delivery_information_row, null);
                        order_item_row1.setVisibility(View.INVISIBLE);
                        layoutInfo.addView(order_item_row1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            deliveryInfo.show();
        }
    }

    private String getTimeInHours(long time) {
        return new SimpleDateFormat("hh:mm aa").format(new Date(time));
    }


    private void checkItemAvalibility(boolean showDialog, boolean addCart) {
        try {

            String msg = "";
            boolean availibility = false, satisfyDiliveryTime = false;
            double diff_notSatisfy = 0;
            JSONObject jObj = new JSONObject(UserDataPreferences.getSettingDetail(this));
            JSONObject data = JSONData.getJSONObject(jObj, "data");
            JSONObject setting = JSONData.getJSONObject(jObj, "setting");
            JSONArray deliveryArray = JSONData.getJSONArray(jObj, "deliveries");
            Calendar today = Calendar.getInstance();
            Calendar caltime = Calendar.getInstance();
            Calendar calNextDay = Calendar.getInstance();


            JSONObject opening_hour = JSONData.getJSONObject(setting, "opening_hour");
            long openFrom = JSONData.getLong(opening_hour, "from");
            long openTo = JSONData.getLong(opening_hour, "to");
            Calendar openingHour = Calendar.getInstance(Locale.ENGLISH);
            openingHour.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
            openingHour.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
            int diff_current_sroreOpenTime = (int) ((today.getTimeInMillis() - openingHour.getTimeInMillis()) / (60 * 1000));
            if (diff_current_sroreOpenTime < 0) {
                today.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                today.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
                caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                caltime.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
                calNextDay.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                calNextDay.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));

            }
            Calendar closingHour = Calendar.getInstance(Locale.ENGLISH);
            closingHour.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openTo));
            closingHour.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openTo))));
            Log.d("current", caltime + "");


            caltime.add(Calendar.HOUR_OF_DAY, itemAvailableTime);
            Log.d("current+available", caltime + "");
            int diff = (int) ((caltime.getTimeInMillis() - today.getTimeInMillis()) / (INTERVAL));
            Log.d("difference", diff + "");
            double storeOpen = Double.parseDouble(AppConstant.getHour(openFrom) + "." + AppConstant.getMinutes(openFrom));
            double storeClose = Double.parseDouble(AppConstant.getHour(openTo) + "." + AppConstant.getMinutes(openTo));
            double selectedTimeHour_minut = Double.parseDouble(AppConstant.getHour(caltime.getTimeInMillis()) + "." + AppConstant.getMinutes(caltime.getTimeInMillis()));
            int diff_shopclose_and_day_over = (24 * 60) - ((AppConstant.getHour(openTo) * 60) + (Integer.parseInt(AppConstant.getMinutes(openTo))));

            if (caltime.getTimeInMillis() < closingHour.getTimeInMillis()) {
                if (selectedTimeHour_minut >= storeOpen && selectedTimeHour_minut <= storeClose) {
                    availibility = true;
                    satisfyDiliveryTime = checkDelivery(deliveryArray, selectedTimeHour_minut, msg, caltime, satisfyDiliveryTime, addCart);
                }
            } else {
                long totalHourDiff = (int) ((caltime.getTimeInMillis() - today.getTimeInMillis()) / (60 * 1000));
                Log.d("total hour difference", totalHourDiff + "");
                calNextDay.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openTo));
                calNextDay.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(openTo)));
                Log.d("calNextDay=", sdf.format(calNextDay.getTime()).toString() + "");
                long completeTaskTime = (int) ((calNextDay.getTimeInMillis() - today.getTimeInMillis()) / (60 * 1000));
                Log.d("completeTaskTime=", completeTaskTime + "");
                long compleTask = completeTaskTime;

                long remainingTaskTime = totalHourDiff - completeTaskTime;
                Log.d("remainingTaskTime=", remainingTaskTime + "");
                boolean flag = false;
                long workingHr = (closingHour.getTimeInMillis() - openingHour.getTimeInMillis()) / (60 * 1000);
                calNextDay.setTimeInMillis(today.getTimeInMillis());
                calNextDay.add(Calendar.MINUTE, (Integer.parseInt(completeTaskTime + "")));
                do {
                    if (remainingTaskTime > (workingHr)) {
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(workingHr + "") + diff_shopclose_and_day_over));
                        remainingTaskTime -= workingHr;
                        flag = true;
                    } else {
                        flag = false;
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(remainingTaskTime + "") + diff_shopclose_and_day_over));
                    }
                } while (flag);
                double selectedNextDayTimeHour_minut = Double.parseDouble(AppConstant.getHour(calNextDay.getTimeInMillis()) + "." + AppConstant.getMinutes(calNextDay.getTimeInMillis()));
                satisfyDiliveryTime = checkDelivery(deliveryArray, selectedNextDayTimeHour_minut, msg, calNextDay, satisfyDiliveryTime, addCart);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkDelivery(JSONArray deliveryArray, double selectedTimeHour_minut, String msg, Calendar caltime, boolean satisfyDiliveryTime, boolean addCart) {
        JSONObject deliveryObj = null;
        long minTime = 0;
        Calendar lastDelivery = Calendar.getInstance();
        lastDelivery.setTimeInMillis(caltime.getTimeInMillis());
        for (int i = 0; i < deliveryArray.length(); i++) {
            try {
                deliveryObj = deliveryArray.getJSONObject(i);
                if (i == 0) {
                    lastDelivery.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time")));
                    lastDelivery.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time"))));
                    Log.d("calNextDayfinal time=", sdf.format(lastDelivery.getTime()).toString() + "");
                }
                double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));

                if (selectedTimeHour_minut >= startTime && selectedTimeHour_minut <= endTime) {
                    msg = "at " + sdf.format(caltime.getTime()).toString();
                    satisfyDiliveryTime = true;
                    Log.d("date : ", msg);
                    Log.d("satisfyDeliveryTime", satisfyDiliveryTime + "");
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (addCart) {
            if (((myCalendar.getTimeInMillis() - caltime.getTimeInMillis()) / (60 * 1000)) >= 0) {
                boolean deliverySatisfy = false;
                double delivery = Double.parseDouble(myCalendar.get(Calendar.HOUR_OF_DAY) + "." + myCalendar.get(Calendar.MINUTE) + "");
                for (int i = 0; i < deliveryArray.length(); i++) {
                    try {
                        deliveryObj = deliveryArray.getJSONObject(i);
                        if (i == 0) {
                            lastDelivery.setTimeInMillis(myCalendar.getTimeInMillis());
                            lastDelivery.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time")));
                            lastDelivery.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time"))));
                        }
                        double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                        double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));
                        if (delivery >= startTime && delivery <= endTime) {
                            deliverySatisfy = true;
                            validateAddToCart();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!deliverySatisfy) {
                    try {
                        caltime = setDeliveryNotSatisfyTime(myCalendar, deliveryArray, lastDelivery, delivery);
                        AppConstant.showSingleButtonAlertDialog(ItemDetailActivity.this, "Message", "Product is not available at your time.It will be available to you around " + sdf.format(caltime.getTime()).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AppConstant.showSingleButtonAlertDialog(ItemDetailActivity.this, "Message", "Product is not available at your time.It will be available to you around " + sdf.format(caltime.getTime()).toString());
            }
        } else {
            if (!satisfyDiliveryTime) {
                try {

                    caltime = setDeliveryNotSatisfyTime(caltime, deliveryArray, lastDelivery, selectedTimeHour_minut);
                    satisfyDiliveryTime = true;
                    Log.d("not mycalandar", sdf.format(myCalendar.getTime()).toString());
                    Log.d("not calTime", sdf.format(caltime.getTime()).toString());
                    msg = "Product delivery not available at your requested time. It is available to you approx " + sdf.format(caltime.getTime()).toString();
                    AppConstant.showSingleButtonAlertDialog(ItemDetailActivity.this, "Message", msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(" mycalandar", sdf.format(myCalendar.getTime()).toString());
                Log.d(" calTime", sdf.format(caltime.getTime()).toString());

                if (myCalendar.getTimeInMillis() > caltime.getTimeInMillis()) {
                    msg = "Your product is available at your time.";
                } else {
                    msg = "Product delivery not available at your requested time. It is available to you approx " + sdf.format(caltime.getTime()).toString();
                }
                AppConstant.showSingleButtonAlertDialog(ItemDetailActivity.this, "Message", msg);
            }
        }


        return satisfyDiliveryTime;
    }

    private Calendar setDeliveryNotSatisfyTime(Calendar caltimefinal, JSONArray deliveryArray, Calendar lastDelivery, double selectedTimeHour_minut) {
        Calendar caltime = Calendar.getInstance();
        caltime.setTimeInMillis(caltimefinal.getTimeInMillis());
        try {
            JSONObject deliveryObj;
            Log.d("caltimefinal time=", sdf.format(caltimefinal.getTime()).toString() + "");
            Log.d("lastDelivery time=", sdf.format(lastDelivery.getTime()).toString() + "");

            if (caltime.getTimeInMillis() < lastDelivery.getTimeInMillis()) {
                for (int i = 0; i < deliveryArray.length() - 1; i++) {
                    deliveryObj = deliveryArray.getJSONObject(i);
                    double startTime = Double.parseDouble(AppConstant.getHour(deliveryArray.getJSONObject(i + 1).getLong("start_time")) + "." + AppConstant.getMinutes(deliveryArray.getJSONObject(i + 1).getLong("start_time")));
                    double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));
                    if (selectedTimeHour_minut >= endTime && selectedTimeHour_minut <= startTime) {
                        caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(i + 1).getLong("start_time")));
                        caltime.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(i + 1).getLong("start_time"))));
                        break;
                    }
                }
            } else {
                caltime.add(Calendar.DAY_OF_MONTH, 1);
                caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(0).getLong("start_time")));
                caltime.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(0).getLong("start_time"))));
            }
        } catch (Exception e) {
        }
        return caltime;
    }


    private void validateAddToCart() {
        if (AppConstant.isNetworkAvailable(ItemDetailActivity.this)) {
            if (!checkboxnoMsg.isChecked()) {
                if (edtxtMessageOnCakes.getText().length() > 0) {
                    new AddToCartTask(false).execute();
                } else {
                    AppConstant.displayErroMessage(edtxtMessageOnCakes, "Please add message to be added on Cake.", ItemDetailActivity.this);
                }
            } else {
                new AddToCartTask(false).execute();
            }
        } else {
            AppConstant.showNetworkError(ItemDetailActivity.this);
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {
        ArrayList<String> classicList;
        LayoutInflater dataInflater;
        LinearLayout dotLayout;
        ArrayList<String> url;
        private List<ImageView> dots;

        ViewPagerAdapter(ArrayList<String> imageList) {
            this.classicList = imageList;
        }

        @Override
        public float getPageWidth(int position) {
            // TODO Auto-generated method stub
            return 1f;
        }

        @Override
        public int getCount() {
            return classicList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final TouchImageView imageView = new TouchImageView(ItemDetailActivity.this);
            imageView.setImageResource(R.drawable.bg_drawer);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, AppConstant.dpToPx(16, ItemDetailActivity.this));
            imageView.setLayoutParams(lp);
            if (imageArrayList != null) {
                if (imageArrayList.size() > 0) {
                    ProgressPageIndicator mIndicator = (ProgressPageIndicator) findViewById(R.id.indicator);
                    mIndicator.setViewPager(viewPager);
                    mIndicator.setRadius(18);
                    mIndicator.setDotGap(6);
                    mIndicator.setStrokeColor(getResources().getColor(R.color.actionbar));
                    mIndicator.setFillColor(getResources().getColor(R.color.actionbar));


                    viewPager.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });


                }
            }
            setImage(imageArrayList.get(position), imageView);
            (container).addView(imageView, (container)
                    .getChildCount() > position ? position
                    : (container).getChildCount());
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((ImageView) object);
        }

    }

    class AddToCartTask extends AsyncTask<Void, Void, String> {


        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(ItemDetailActivity.this);
        InputStream is;
        String sResponse;
        boolean isBuyNow;

        public AddToCartTask(boolean isBuyNow) {
            this.isBuyNow = isBuyNow;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... unsued) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.api_v1 + Constants.api_add_to_cart);
                httpPost.setHeader("Authorization", "Bearer " + UserDataPreferences.getToken(ItemDetailActivity.this));
                Log.d("token", UserDataPreferences.getToken(ItemDetailActivity.this));
                httpPost.setEntity(getEntity());

                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                // BufferedReader reader = new BufferedReader(
                // new InputStreamReader(
                // response.getEntity().getContent(), "UTF-8"));
                //
                // String sResponse = reader.readLine();

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
                    String message = jObj.has("message") ? jObj.getString("message") : "";
                    if (flag) {
                        JSONObject jsonObject = jObj.getJSONObject("data");
                        int cartCount = UserDataPreferences.getCartCount(ItemDetailActivity.this) + 1;
                        UserDataPreferences.saveCartCount(ItemDetailActivity.this, cartCount);
                        MainActivity.txtCart.setVisibility(View.VISIBLE);
                        MainActivity.txtCart.setText(cartCount + "");

                        Intent intent = new Intent(ItemDetailActivity.this, MyCart.class);
                        startActivityForResult(intent, 7);
                        overridePendingTransition(R.anim.animation, R.anim.animation2);
                        finish();

                    } else {

                        if (message.equals("")) {
                            AppConstant.showSingleButtonAlertDialog(
                                    ItemDetailActivity.this, "Error",
                                    "Something went wrong.\nPlease try again");
                        } else {
                            new AlertDialog.Builder(ItemDetailActivity.this)
                                    .setTitle("Message")
                                    .setMessage(Html.fromHtml(message))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setCancelable(true).show();
                        }
                    }

                }

            } catch (Exception e) {
                AppConstant.showSingleButtonAlertDialog(ItemDetailActivity.this,
                        "Error",
                        "Unable to connect Web Services.\nPlease try again.");
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

}
