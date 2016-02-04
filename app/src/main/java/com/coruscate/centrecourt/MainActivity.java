package com.coruscate.centrecourt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coruscate.centrecourt.AsynkTask.SettingTask;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.UserInterface.Activity.LoginActivity;
import com.coruscate.centrecourt.UserInterface.Activity.MyCart;
import com.coruscate.centrecourt.UserInterface.Activity.UpdateProfileActivity;
import com.coruscate.centrecourt.UserInterface.Fragments.ContactUsFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.Dashboard.DashBoardCakesFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.DetailFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.MyWishListFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.ProfileFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFERENCES_FILE = "mymaterialapp_settings";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public static TextView txtCart;
    public int subCategoryPos = 0;
    public SearchView searchView;
    FrameLayout mContentFrame;
    LinearLayout.LayoutParams params;
    int expand = 0;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;
    private LinearLayout linearLayoutSubMenu, navDrawerHeader;
    private ImageView imgToogleMenuType, imageViewUser;
    private RelativeLayout relativeLayoutMenu;
    private TextView txtMenuItem2, txtMenuItem3, txtMenuItem4, txtMenuItem5, txtMenuItem6, txtMenuItem7, txtMenuItem8, txtDrawerName, txtDrawerEmail;
    private ArrayList<TextView> textViewArraylist;
    private ArrayList<String> categoryList;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private RelativeLayout relativeLayoutCart;
    private int fragmentPosition;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public void setCartCount() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity==>", "requestCode==>" + requestCode + "\nresultCode==>" + resultCode);
        if (requestCode == 4 && resultCode == 4) {
            setUserDetail();
        } else if (requestCode == 8 && (resultCode == 8 || resultCode == 0)) {
            setCartCount();
        } else if (requestCode == 12 && resultCode == 12) {
            UserDataPreferences.removeUserInfo(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        } else if (requestCode == 8 && resultCode == 1) {
            fragmentPosition = 1;
//            makeFragmentVisible(new DashboardFragment(), DashboardFragment.TAG);
//            setActionbarTitle(DashboardFragment.TAG);

            mCurrentSelectedPosition = 0;
            DashBoardCakesFragment frag = new DashBoardCakesFragment();

            Bundle bdl = new Bundle();
            bdl.putInt(DashBoardCakesFragment.FRAGMENT_No, 0);
            frag.setArguments(bdl);
            makeFragmentVisible(frag, DashBoardCakesFragment.TAG);
            setActionbarTitle(DashBoardCakesFragment.TAG);
            textViewArraylist.get(0).setTextColor(getResources().getColor(R.color.white));
            textViewArraylist.get(0).setBackgroundColor(getResources().getColor(R.color.actionbar));

            expand = 1;
            imgToogleMenuType.setImageResource(R.drawable.icon_arrow_down);
            linearLayoutSubMenu.setVisibility(View.VISIBLE);
            textViewArraylist.get(subCategoryPos).setTextColor(getResources().getColor(R.color.white));
            textViewArraylist.get(subCategoryPos).setBackgroundColor(getResources().getColor(R.color.actionbar));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avtar)
                .showImageForEmptyUri(R.drawable.avtar)
                .showImageOnFail(R.drawable.avtar).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        setContentView(R.layout.activity_main);
//        try {
//            AppConstant.setToolBarColor(MainActivity.this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        setUpToolbar();
        initializeViews();
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        setUpNavDrawer();
        customNavigationDrawer();
        relativeLayoutMenu.setOnClickListener(this);
        txtMenuItem2.setOnClickListener(this);
        txtMenuItem3.setOnClickListener(this);
        txtMenuItem4.setOnClickListener(this);
        txtMenuItem5.setOnClickListener(this);
        txtMenuItem6.setOnClickListener(this);
        txtMenuItem7.setOnClickListener(this);
        navDrawerHeader.setOnClickListener(this);


        if (savedInstanceState == null) {
            fragmentPosition = 1;
            expand = 1;
            imgToogleMenuType.setImageResource(R.drawable.icon_arrow_down);
            linearLayoutSubMenu.setVisibility(View.VISIBLE);
            textViewArraylist.get(subCategoryPos).setTextColor(getResources().getColor(R.color.white));
            textViewArraylist.get(subCategoryPos).setBackgroundColor(getResources().getColor(R.color.actionbar));

            mCurrentSelectedPosition = 0;

            checkSubCat(0);
            textViewArraylist.get(0).setTextColor(getResources().getColor(R.color.white));
            textViewArraylist.get(0).setBackgroundColor(getResources().getColor(R.color.actionbar));

        }

        if (UserDataPreferences.getSettingDetail(MainActivity.this).toString().equals("")) {
            if (AppConstant.isNetworkAvailable(MainActivity.this)) {
                new SettingTask(MainActivity.this, false).execute();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem itemSearch = menu.findItem(R.id.itemSearch);
        final MenuItem itemCart = menu.findItem(R.id.itemMyCart);
        itemSearch.setVisible(false);
        itemCart.setVisible(true);

        relativeLayoutCart = (RelativeLayout) MenuItemCompat.getActionView(itemCart);
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        final View viewCart = menu.findItem(R.id.itemMyCart).getActionView();
        txtCart = (TextView) viewCart.findViewById(R.id.txtCartItemCount);
        if (UserDataPreferences.getCartCount(MainActivity.this) > 0) {
            txtCart.setVisibility(View.VISIBLE);
            txtCart.setText(UserDataPreferences.getCartCount(MainActivity.this) + "");
        } else {
            txtCart.setVisibility(View.GONE);
        }
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                itemCart.setVisible(true);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCart.setVisible(false);
                searchView.requestFocus();
            }
        });
        relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCart.class);
                startActivityForResult(intent, 8);
                overridePendingTransition(R.anim.animation, R.anim.animation2);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (fragmentPosition == 1) {
                    DetailFragment.filter(query);
                } else if (fragmentPosition == 2) {
                    MyWishListFragment.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (fragmentPosition == 1) {
                    DetailFragment.filter(query);
                } else if (fragmentPosition == 2) {
                    MyWishListFragment.filter(query);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);

            }
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
        }

    }

    private void closeNavigationDrawer() {
        if (mUserLearnedDrawer) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void setActionbarTitle(String string) {
        getSupportActionBar().setTitle(AppConstant.spanFont(string, this));
    }

    private void makeFragmentVisible(Fragment fragment, String strName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction()
                .addToBackStack(strName)
                .replace(R.id.nav_contentframe, fragment).commit();
    }

    private void initializeViews() {

        textViewArraylist = new ArrayList<>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        linearLayoutSubMenu = (LinearLayout) findViewById(R.id.linearLayoutSubMenu);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);
        imgToogleMenuType = (ImageView) findViewById(R.id.imgToogleMenuType);
        imageViewUser = (ImageView) findViewById(R.id.imageViewUser);
        txtDrawerName = (TextView) findViewById(R.id.txtDrawerName);
        txtDrawerEmail = (TextView) findViewById(R.id.txtDrawerEmail);
        navDrawerHeader = (LinearLayout) findViewById(R.id.navDrawerHeader);

        setUserDetail();

        relativeLayoutMenu = (RelativeLayout) findViewById(R.id.relativeLayoutMenu);
        if (expand == 0) {
            linearLayoutSubMenu.setVisibility(View.GONE);
        }
        txtMenuItem2 = (TextView) findViewById(R.id.txtMenuItem2);
        txtMenuItem3 = (TextView) findViewById(R.id.txtMenuItem3);
        txtMenuItem4 = (TextView) findViewById(R.id.txtMenuItem4);
        txtMenuItem5 = (TextView) findViewById(R.id.txtMenuItem5);
        txtMenuItem6 = (TextView) findViewById(R.id.txtMenuItem6);
        txtMenuItem7 = (TextView) findViewById(R.id.txtMenuItem7);
        txtMenuItem2.setText("New Arrival");
        txtMenuItem3.setText("My Cart");
        txtMenuItem4.setText("My Wishlist");
        txtMenuItem5.setText("My Account");
        txtMenuItem6.setText("Contact Us");
        txtMenuItem7.setText("Logout");
        setBagroundWhite();
        categoryList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(UserDataPreferences.getCategoryInfo(MainActivity.this));

        } catch (Exception e) {
            e.printStackTrace();
        }
        AppConstant.copyJSONArray(jsonArray, categoryList);
    }

    public void setUserDetail() {

        String strUserInfo = UserDataPreferences.getUserInfo(MainActivity.this);
        if (strUserInfo.length() > 0) {
            try {

                JSONObject jsonObject = new JSONObject(strUserInfo);
                txtDrawerName.setText((jsonObject.has("first_name") ? (jsonObject.isNull("first_name") ? "" : jsonObject.getString("first_name")) : "") + " " + (jsonObject.has("last_name") ? (jsonObject.isNull("last_name") ? "" : jsonObject.getString("last_name")) : ""));
                txtDrawerEmail.setText(jsonObject.has("email") ? (jsonObject.isNull("email") ? "" : jsonObject.getString("email")) : "");
                final String imageUrl = jsonObject.has("image") ? (jsonObject.isNull("image") ? "" : jsonObject.getString("image")) : "";
                if (imageUrl.trim().length() > 0) {
                    imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(this));
                    if (imageUrl.contains("http")) {
                        imageLoader.displayImage(imageUrl, imageViewUser, options);
                    } else {
                        imageLoader.displayImage(Constants.api_ip + "/" + imageUrl, imageViewUser, options);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void customNavigationDrawer() {
        JSONObject jsonObject;
        String str;
        for (int i = 0; i < categoryList.size(); i++) {
            try {
                jsonObject = new JSONObject(categoryList.get(i));
                str = jsonObject.has("title") ? jsonObject.getString("title") : "";
                TextView tv = new TextView(this);
                tv.setText(AppConstant.spanFontRegular(str, MainActivity.this));
                tv.setId(i);
                tv.setOnClickListener(this);
                tv.setPadding(AppConstant.dpToPx(32, this), AppConstant.dpToPx(8, this), AppConstant.dpToPx(8, this), AppConstant.dpToPx(8, this));
                textViewArraylist.add(tv);
                linearLayoutSubMenu.addView(tv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.relativeLayoutMenu) {
            closeNavigationDrawer();
        }
        if (id == R.id.relativeLayoutMenu) {
            if (expand == 0) {
                expand = 1;
                imgToogleMenuType.setImageResource(R.drawable.icon_arrow_down);
                linearLayoutSubMenu.setVisibility(View.VISIBLE);
                AppConstant.expand(linearLayoutSubMenu);
            } else {
                expand = 0;
                imgToogleMenuType.setImageResource(R.drawable.icon_arrow_right);
                AppConstant.collapse(linearLayoutSubMenu);
            }
        } else if (id == R.id.txtMenuItem2) {
            setBagroundWhite();
            fragmentPosition = 1;
            txtMenuItem2.setTextColor(getResources().getColor(R.color.white));
            txtMenuItem2.setBackgroundColor(getResources().getColor(R.color.actionbar));
            Bundle bundle = new Bundle();
            bundle.putString("type", "NewArrivals");
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            makeFragmentVisible(detailFragment, DetailFragment.TAG);
            setActionbarTitle("New Arrivals");
        } else if (id == R.id.txtMenuItem3) {
            Intent intent = new Intent(MainActivity.this, MyCart.class);
            startActivityForResult(intent, 8);
            overridePendingTransition(R.anim.animation, R.anim.animation2);
            mCurrentSelectedPosition = 1;
        } else if (id == R.id.txtMenuItem4) {
            setBagroundWhite();
            fragmentPosition = 2;
            txtMenuItem4.setTextColor(getResources().getColor(R.color.white));
            txtMenuItem4.setBackgroundColor(getResources().getColor(R.color.actionbar));
            makeFragmentVisible(new MyWishListFragment(), MyWishListFragment.TAG);
            setActionbarTitle(MyWishListFragment.TAG);
        } else if (id == R.id.txtMenuItem5) {
            setBagroundWhite();
            txtMenuItem5.setTextColor(getResources().getColor(R.color.white));
            txtMenuItem5.setBackgroundColor(getResources().getColor(R.color.actionbar));
            makeFragmentVisible(new ProfileFragment(), ProfileFragment.TAG);
            setActionbarTitle(ProfileFragment.TAG);
        } else if (id == R.id.txtMenuItem6) {
            setBagroundWhite();
            txtMenuItem6.setTextColor(getResources().getColor(R.color.white));
            txtMenuItem6.setBackgroundColor(getResources().getColor(R.color.actionbar));
            makeFragmentVisible(new ContactUsFragment(), ContactUsFragment.TAG);
            setActionbarTitle(ContactUsFragment.TAG);
        } else if (id == R.id.txtMenuItem7) {
            setBagroundWhite();

            // txtMenuItem7.setTextColor(getResources().getColor(R.color.white));
            //  txtMenuItem7.setBackgroundColor(getResources().getColor(R.color.actionbar));
//            Intent intent = new Intent(MainActivity.this, MiscellaneousActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.animation, R.anim.animation2);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("message")
                    .setMessage("Are you sure, you wan't to logout?")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (AppConstant.isNetworkAvailable(MainActivity.this)) {
                                new LogoutTask().execute();
                            }
                        }
                    }).setCancelable(true).show();
            //mCurrentSelectedPosition = 2;
        } else if (id == R.id.navDrawerHeader) {
            Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivityForResult(intent, 4);
            overridePendingTransition(R.anim.animation, R.anim.animation2);
        } else {
            setBagroundWhite();
            for (int i = 0; i < textViewArraylist.size(); i++) {
                if (id == textViewArraylist.get(i).getId()) {
                    mCurrentSelectedPosition = 0;
                    checkSubCat(i);
                    textViewArraylist.get(i).setTextColor(getResources().getColor(R.color.white));
                    textViewArraylist.get(i).setBackgroundColor(getResources().getColor(R.color.actionbar));
                    break;
                }
            }
        }
    }

    private void checkSubCat(int i) {
        try {
            ArrayList<String> cat = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(UserDataPreferences.getCategoryInfo(MainActivity.this));
            AppConstant.copyJSONArray(jsonArray, cat);
            JSONObject jobj;
            JSONArray jArray;
            ArrayList<String> subCategoryList = new ArrayList<>();
            for (int k = 0; k < cat.size(); k++) {
                if (k == i) {

                    jobj = new JSONObject(cat.get(i));
                    setActionbarTitle(jobj.getString("name"));
                    jArray = jobj.getJSONArray("sub_categories");
                    AppConstant.copyJSONArray(jArray, subCategoryList);
                    if (subCategoryList.size() == 0) {
                        DetailFragment frag = new DetailFragment();
                        subCategoryPos = i;
                        fragmentPosition = 1;
                        String strId = "";
                        try {
                            strId = new JSONObject(categoryList.get(i)).getString("_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Bundle bdl = new Bundle();
                        bdl.putString("id", strId);
                        frag.setArguments(bdl);
                        makeFragmentVisible(frag, DetailFragment.TAG);
                        setActionbarTitle(new JSONObject(categoryList.get(i)).getString("name"));
                    } else {
                        DashBoardCakesFragment frag = new DashBoardCakesFragment();
                        subCategoryPos = i;
                        fragmentPosition = 1;

                        Bundle bdl = new Bundle();
                        bdl.putInt(DashBoardCakesFragment.FRAGMENT_No, i);
                        frag.setArguments(bdl);
                        makeFragmentVisible(frag, DashBoardCakesFragment.TAG);
                        setActionbarTitle(DashBoardCakesFragment.TAG);
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBagroundWhite() {
        txtMenuItem2.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem3.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem4.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem5.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem6.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem7.setBackgroundColor(getResources().getColor(R.color.white));
        txtMenuItem2.setTextColor(getResources().getColor(R.color.gray));
        txtMenuItem3.setTextColor(getResources().getColor(R.color.gray));
        txtMenuItem4.setTextColor(getResources().getColor(R.color.gray));
        txtMenuItem5.setTextColor(getResources().getColor(R.color.gray));
        txtMenuItem6.setTextColor(getResources().getColor(R.color.gray));
        txtMenuItem7.setTextColor(getResources().getColor(R.color.gray));
        setDynamicTextViewBackground(textViewArraylist);
    }

    private void setDynamicTextViewBackground(ArrayList<TextView> textViewArraylist) {
        for (int i = 0; i < textViewArraylist.size(); i++) {
            textViewArraylist.get(i).setTextColor(getResources().getColor(R.color.gray));
            textViewArraylist.get(i).setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragMgr = getSupportFragmentManager();
            if (fragMgr.getBackStackEntryCount() > 1) {
                fragMgr.popBackStack();
                setActionbarTitle(DashBoardCakesFragment.TAG);
                mCurrentSelectedPosition = 0;
            } else {
                if (mCurrentSelectedPosition == 0) {
                    AppConstant.closeAppPopup(MainActivity.this);
                } else {
                    fragmentPosition = 1;
                    DashBoardCakesFragment frag = new DashBoardCakesFragment();
                    Bundle bdl = new Bundle();
                    bdl.putInt(DashBoardCakesFragment.FRAGMENT_No, 0);
                    frag.setArguments(bdl);

                    makeFragmentVisible(new DashBoardCakesFragment(), DashBoardCakesFragment.TAG);
                    mCurrentSelectedPosition = 0;
                    setActionbarTitle(DashBoardCakesFragment.TAG);

                    expand = 1;
                    imgToogleMenuType.setImageResource(R.drawable.icon_arrow_down);
                    linearLayoutSubMenu.setVisibility(View.VISIBLE);
                    textViewArraylist.get(subCategoryPos).setTextColor(getResources().getColor(R.color.white));
                    textViewArraylist.get(subCategoryPos).setBackgroundColor(getResources().getColor(R.color.actionbar));
                }

            }
        }
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(MainActivity.this);
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser(MainActivity.this);
            try {
                String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_logout);
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = JSONData.getString(jObj, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (responseCode == 200) {
                    if (flag) {
                        UserDataPreferences.removeUserInfo(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        finish();
                    } else {
                        AppConstant.showToastShort(MainActivity.this, message);
                    }
                } else {
                    AppConstant.showNetworkError(MainActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}