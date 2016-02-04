package com.coruscate.centrecourt.UserInterface.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.MiscellaneousFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MiscellaneousActivity extends AppCompatActivity implements View.OnClickListener {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    private JSONArray jsonArray;
    private static ArrayList<String> idList = new ArrayList<>();
    private ViewPagerAdapter adapter;
    public TextView txtSubTotal, txtTotalPrice;
    private String productItemList;
    private Button btnContiueCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miscellaneous);
        ButterKnife.inject(this);
    /*    try {
            AppConstant.setToolBarColor(MiscellaneousActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        productItemList = getIntent().getStringExtra("products");
        setUpToolbar();
        initializeViews();
        UserDataPreferences.saveAccessoriesItemList(this, new ArrayList<String>());
        setupViewPager();

//        if (AppConstant.isNetworkAvailable(this)) {
//            new GetMiscellaneousTask(true).execute();
//        } else {
//            layoutCantConnect.setVisibility(View.VISIBLE);
//            setNoDataFound();
//            txtNoResult.setVisibility(View.GONE);
//        }
        btnContiueCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray jsonArray = UserDataPreferences.getUserAddressBook(MiscellaneousActivity.this);
                if (null != jsonArray && jsonArray.length() > 0) {
                    Intent intent = new Intent(MiscellaneousActivity.this, PaymentInfoActivity.class);
                    intent.putExtra("products", productItemList);
                    startActivityForResult(intent, 5);
                    overridePendingTransition(R.anim.animation, R.anim.animation2);
                } else {
                    Intent intent = new Intent(MiscellaneousActivity.this, AddAddressActivity.class);
                    startActivityForResult(intent, 9);
                    overridePendingTransition(R.anim.animation, R.anim.animation2);
                }
            }
        });
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Accessories", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Miscellaneous==>", "requestCode==>" + requestCode + "\nresultCode==>" + resultCode);
        if (requestCode == 5 && resultCode == 5) {
            Intent i1 = new Intent();
            setResult(5, i1);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (requestCode == 9 && resultCode == 1) {
            Intent intent = new Intent(MiscellaneousActivity.this, PaymentInfoActivity.class);
            intent.putExtra("products", productItemList);
            startActivityForResult(intent, 5);
            overridePendingTransition(R.anim.animation, R.anim.animation2);
        } else if (resultCode == Constants.WEBVIEW_CODE) {
//                getActivity().finish();
            Intent i = new Intent();
            setResult(Constants.WEBVIEW_CODE, i);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

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

    private void closeActivity() {
        //clearAccessoriesList();
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

    @SuppressLint("NewApi")
    private void initializeViews() {
        txtSubTotal = (TextView) findViewById(R.id.txtSubTotal);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        btnContiueCheckOut = (Button) findViewById(R.id.btnContiueCheckOut);
        if (AppConstant.isAndroid5()) {
            btnContiueCheckOut.setBackground(getDrawable(R.drawable.ripple_accent));
        }
    }

    public void setCurrentItem(int id) {
        viewPager.setCurrentItem(id);
    }

    public void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ArrayList<String> miscellaneousList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(UserDataPreferences.getMiscellaneousInfo(MiscellaneousActivity.this));
            jsonArray = jsonObject.getJSONArray("subcategories");
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppConstant.copyJSONArray(jsonArray, miscellaneousList);

        if (miscellaneousList.size() > 0) {

            viewPager.setVisibility(View.VISIBLE);
            viewPager.setOffscreenPageLimit(miscellaneousList.size());
            JSONObject jsonObject;
            for (int i = 0; i < miscellaneousList.size(); i++) {
                try {
                    jsonObject = new JSONObject(miscellaneousList.get(i));
                    String str = jsonObject.has("title") ? jsonObject.getString("title") : "";
                    String id = jsonObject.has("_id") ? jsonObject.getString("_id") : "";
                    idList.add(id);
                    adapter.addFrag(str);
                    Log.d("catagory", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            setNoDataFound();
        }
    }

    private void setNoDataFound() {
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            MiscellaneousFragment fragment = new MiscellaneousFragment();
            Bundle bdl = new Bundle();
            bdl.putString("id", idList.get(position));
            fragment.setArguments(bdl);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentTitleList.size();
        }

        public void addFrag(String title) {
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
