package com.coruscate.centrecourt.UserInterface.Activity;

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
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.OrderHistoryAdapter;
import com.coruscate.centrecourt.AsynkTask.GetOrderListTask;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.ProfileFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInAnimator;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderListActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private RecyclerView.LayoutManager myLayoutManager;
    public static OrderHistoryAdapter adapter;
    private static Bookends<OrderHistoryAdapter> mBookends;
    public static RecyclerView myOrderRecycler;
    public static TextView txtNoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.inject(this);
      /*  try {
            AppConstant.setToolBarColor(OrderListActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setUpToolbar();
//        myOrderList = getIntent().getStringArrayListExtra("orderList");
        initializeViews();
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("My Orders", this));
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
        myOrderRecycler = (RecyclerView) findViewById(R.id.myOrderRecycler);
        txtNoResult = (TextView) findViewById(R.id.txtNoResult);
        myOrderRecycler.setHasFixedSize(true);
        myOrderRecycler.setItemAnimator(new ScaleInAnimator());
        myOrderRecycler.getItemAnimator().setRemoveDuration(500);
        myLayoutManager = new LinearLayoutManager(OrderListActivity.this);
        myOrderRecycler.setLayoutManager(myLayoutManager);

//        myOrderList = new ArrayList<>();
        if (getIntent().getBooleanExtra("isCart", false)) {
            if (AppConstant.isNetworkAvailable(OrderListActivity.this)) {
                new GetOrderListTask(OrderListActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppConstant.showNetworkError(OrderListActivity.this);
            }
        } else {
            adapter = new OrderHistoryAdapter(ProfileFragment.orderList, OrderListActivity.this);
        }
//        mBookends = new Bookends<>(adapter);
        myOrderRecycler.setAdapter(adapter);

    }

}
