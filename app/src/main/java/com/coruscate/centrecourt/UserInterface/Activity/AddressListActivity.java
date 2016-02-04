package com.coruscate.centrecourt.UserInterface.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.coruscate.centrecourt.Adapter.AddressListAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInAnimator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddressListActivity extends AppCompatActivity implements View.OnClickListener {

    private static Bookends<AddressListAdapter> mBookends;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.myAddressRecycler)
    RecyclerView myAddressRecycler;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.txtNoResult)
    TypefacedTextView txtNoResult;
    @InjectView(R.id.txttotalAddress)
    TypefacedTextView txttotalAddress;
    @InjectView(R.id.txtAddAddress)
    TypefacedTextView txtAddAddress;
    private RecyclerView.LayoutManager myLayoutManager;
    private AddressListAdapter adapter;
    private ArrayList<String> myAddressList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // resultCode 1 for add address and 2 for edit address
        if (requestCode == 3 && (resultCode == 1 || resultCode == 2)) {
            refreshListData();
        }
    }

    public void refreshListData() {
        JSONArray jsonArray = UserDataPreferences.getUserAddressBook(this);
        if (jsonArray != null) {
            if (jsonArray.length() > 0) {
                AppConstant.copyJSONArray(jsonArray, myAddressList);
            }
            if (myAddressList.size() > 0) {
                txttotalAddress.setVisibility(View.VISIBLE);
                txttotalAddress.setText(myAddressList.size() + " Saved Address");
            } else {
                txttotalAddress.setVisibility(View.GONE);
            }
            mBookends.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address_list);
        ButterKnife.inject(this);

     /*   try {
            AppConstant.setToolBarColor(AddressListActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setUpToolbar();
        initializeViews();
        txtAddAddress.setOnClickListener(this);
        if (AppConstant.isNetworkAvailable(this)) {
            new GetAddressBookTask().execute();
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Address", this));
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

    private void closeActivity() {
        setResult(10, new Intent());
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

    private void initializeViews() {
        myAddressRecycler.setHasFixedSize(true);
        myAddressRecycler.setItemAnimator(new ScaleInAnimator());
        myAddressRecycler.getItemAnimator().setRemoveDuration(500);
        myLayoutManager = new LinearLayoutManager(AddressListActivity.this);
        myAddressRecycler.setLayoutManager(myLayoutManager);
        myAddressList = new ArrayList<>();

        JSONArray jsonArray = UserDataPreferences.getUserAddressBook(this);
        try {
            if (jsonArray != null) {
                AppConstant.copyJSONArray(jsonArray, myAddressList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (myAddressList.size() > 0) {
            txttotalAddress.setVisibility(View.VISIBLE);
            txttotalAddress.setText(myAddressList.size() + " Saved Address");
        } else {
            txttotalAddress.setVisibility(View.GONE);
        }
        adapter = new AddressListAdapter(myAddressList, AddressListActivity.this);
        mBookends = new Bookends<>(adapter);
        myAddressRecycler.setAdapter(mBookends);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtAddAddress) {
            Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
            startActivityForResult(intent, 3);
            overridePendingTransition(R.anim.animation, R.anim.animation2);
        }
    }

    public class GetAddressBookTask extends AsyncTask<Void, Void, String[]> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(AddressListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(AddressListActivity.this);
            try {
                return jParser.sendGetReq(Constants.api_v1 + Constants.api_address_book);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String[] data) {
            super.onPostExecute(data);
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (Integer.valueOf(data[0]) == 200) {
                    JSONObject jObj = new JSONObject(data[1]);
                    if (JSONData.getBoolean(jObj, "flag")) {
                        UserDataPreferences.saveUserInfo(AddressListActivity.this, JSONData.getJSONObject(jObj, "data").toString());
                        refreshListData();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
