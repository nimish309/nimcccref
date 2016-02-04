package com.coruscate.centrecourt.UserInterface.Fragments.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coruscate.centrecourt.AsynkTask.GetOrderListTask;
import com.coruscate.centrecourt.CustomControls.CircleImageView;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.AddressListActivity;
import com.coruscate.centrecourt.UserInterface.Activity.ChangePassword;
import com.coruscate.centrecourt.UserInterface.Activity.GiftVoucher;
import com.coruscate.centrecourt.UserInterface.Activity.NewsLetterActivity;
import com.coruscate.centrecourt.UserInterface.Activity.OrderListActivity;
import com.coruscate.centrecourt.UserInterface.Activity.UpdateProfileActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 7/27/2015.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "My Account";
    CollapsingToolbarLayout collapsingToolbar;
    int mutedColor = R.attr.colorPrimary;
    @InjectView(R.id.imageViewProfile)
    CircleImageView imageViewProfile;
    @InjectView(R.id.txtName)
    TypefacedTextView txtName;
    @InjectView(R.id.txtContact)
    TypefacedTextView txtContact;
    @InjectView(R.id.txtEmail)
    TypefacedTextView txtEmail;
    @InjectView(R.id.ImgEditProfile)
    ImageView ImgEditProfile;
    @InjectView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @InjectView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @InjectView(R.id.txtViewGift)
    TypefacedTextView txtViewGift;
    @InjectView(R.id.cardGiftVoucher)
    CardView cardGiftVoucher;
    @InjectView(R.id.txtViewAllOrders)
    TypefacedTextView txtViewAllOrders;
    @InjectView(R.id.txtAddress)
    TypefacedTextView txtAddress;
    @InjectView(R.id.txtViewMoreAddress)
    TypefacedTextView txtViewMoreAddress;
    @InjectView(R.id.cardNewsLatter)
    LinearLayout cardNewsLatter;
    @InjectView(R.id.root_coordinator)
    CoordinatorLayout rootCoordinator;
    @InjectView(R.id.cardChangePassword)
    LinearLayout cardChangePassword;

    private MainActivity mainActivity;

    public static ArrayList<String> orderList = new ArrayList<>();
    public static TextView txtNoOrder;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == 4) {
            setUserDetail();
        } else if (requestCode == 10 && resultCode == 10) {
            setAddressDetail();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.itemMyCart).setVisible(false);
        menu.findItem(R.id.itemSearch).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderList.clear();
        setHasOptionsMenu(true);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avtar)
                .showImageForEmptyUri(R.drawable.avtar)
                .showImageOnFail(R.drawable.avtar).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.inject(this, view);
        initializeViews(view);
        txtNoOrder = (TextView) view.findViewById(R.id.txtNoOrder);
        txtViewAllOrders.setOnClickListener(this);
        ImgEditProfile.setOnClickListener(this);
        txtViewMoreAddress.setOnClickListener(this);
        cardNewsLatter.setOnClickListener(this);
        cardGiftVoucher.setOnClickListener(this);
        cardChangePassword.setOnClickListener(this);
        setUserDetail();
        setAddressDetail();
        if (AppConstant.isNetworkAvailable(mainActivity)) {
            new GetOrderListTask(mainActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            new GetOrderListTask().execute();
//            new GetAddressBookTask().execute();
        } else {
            AppConstant.showNetworkError(mainActivity);
        }


        return view;
    }

    private void initializeViews(View view) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.txtViewAllOrders:
                if (orderList.size() > 0) {
                    intent = new Intent(mainActivity, OrderListActivity.class);
                    //intent.putStringArrayListExtra("orderList", orderList);
                    intent.putExtra("isCart", false);
                    startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                } else {
                    AppConstant.displayErroMessage(v, "You have no new order", mainActivity);
                }
                break;
            case R.id.ImgEditProfile:
                intent = new Intent(mainActivity, UpdateProfileActivity.class);
                startActivityForResult(intent, 4);
                mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.txtViewMoreAddress:
                intent = new Intent(mainActivity, AddressListActivity.class);
                startActivityForResult(intent, 10);
                mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.cardGiftVoucher:
                intent = new Intent(mainActivity, GiftVoucher.class);
                startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.cardNewsLatter:
                intent = new Intent(mainActivity, NewsLetterActivity.class);
                startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.cardChangePassword:
                intent = new Intent(mainActivity, ChangePassword.class);
                mainActivity.startActivityForResult(intent, 12);
                mainActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
        }
    }

    private void setUserDetail() {
        try {
            JSONObject jobj = new JSONObject(UserDataPreferences.getUserInfo(mainActivity));
            txtName.setText(JSONData.getString(jobj, "first_name") + " " + JSONData.getString(jobj, "last_name"));
            txtContact.setText(JSONData.getString(jobj, "mobile"));
            txtEmail.setText(JSONData.getString(jobj, "email"));
            String imageUrl = (JSONData.getString(jobj, "image"));


            if (imageUrl.trim().length() > 0) {
                imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
                if (imageUrl.contains("http")) {
                    imageLoader.displayImage(imageUrl, imageViewProfile, options);
                } else {
                    imageLoader.displayImage(Constants.api_ip + "/" + imageUrl, imageViewProfile, options);
                }

            }

//            if (imageUrl.length() > 0) {
//                imageLoader = ImageLoader.getInstance();
//                imageLoader.init(ImageLoaderConfiguration.createDefault(mainActivity));
//                imageLoader.displayImage(Constants.api_ip + "/" + imageUrl, imageViewProfile, options);
//            }
            mainActivity.setUserDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddressDetail() {
        JSONArray addressArray = UserDataPreferences.getUserAddressBook(mainActivity);
        if (addressArray != null) {
            if (addressArray.length() > 0) {
                try {
                    JSONObject object = addressArray.getJSONObject(0);
                    String line2 = JSONData.getString(object, "line2");
                    if (line2.length() > 0) {
                        line2 += "\n";
                    }
                    txtAddress.setText(JSONData.getString(object, "line1") + "\n" + line2 +
                            JSONData.getString(object, "city") + "," + JSONData.getString(object, "state") + "-" +
                            JSONData.getString(object, "pincode") + "\n" +
                            JSONData.getString(object, "country"));
                } catch (Exception e) {
                    e.printStackTrace();
                    txtAddress.setText("");
                }
            }
        } else {
            txtAddress.setText("");
        }
    }

}
