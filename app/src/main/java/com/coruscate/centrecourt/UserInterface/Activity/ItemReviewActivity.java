package com.coruscate.centrecourt.UserInterface.Activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.ItemReviewAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.marshalchen.ultimaterecyclerview.divideritemdecoration.HorizontalDividerItemDecoration;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ItemReviewActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.itemReviewRecycler)
    RecyclerView itemReviewRecycler;
    @InjectView(R.id.layoutWriteReview)
    LinearLayout layoutWriteReview;
    @InjectView(R.id.txtReview)
    TextView txtReview;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.txtNoResult)
    TypefacedTextView txtNoResult;

    private RecyclerView.LayoutManager myLayoutManager;
    private Bookends<ItemReviewAdapter> mBookends;
    private ArrayList<String> reviewList;
    private JSONObject userReviewObject;

    // For Review Dialog
    private Dialog reviewDialog;
    private RatingBar rattingBar;
    private EditText etReview;
    private Button btnCloseReview, btnSendReview;
    public String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_review);
        ButterKnife.inject(this);
     /*   try {
            AppConstant.setToolBarColor(ItemReviewActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Intent intent = getIntent();
        reviewList = intent.getStringArrayListExtra("reviewList");
        product_id = intent.getStringExtra("product_id");
        String user_review = intent.getStringExtra("userReviewObject");
        if (user_review != null) {
            try {
                userReviewObject = new JSONObject(user_review);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (userReviewObject != null) {
            txtReview.setText("Modify Review");
        }
        setUpToolbar();
        initializeViews();

    }

    @Override
    public void onBackPressed() {
        closeActivity();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        itemReviewRecycler.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(ItemReviewActivity.this);
        itemReviewRecycler.setLayoutManager(myLayoutManager);
        itemReviewRecycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ItemReviewActivity.this).build());
        if (reviewList != null) {
            if (reviewList.size() > 0) {
                mBookends = new Bookends<>(new ItemReviewAdapter(reviewList, ItemReviewActivity.this));
                itemReviewRecycler.setAdapter(mBookends);
                itemReviewRecycler.setVisibility(View.VISIBLE);
                txtNoResult.setVisibility(View.GONE);
            } else {
                itemReviewRecycler.setVisibility(View.GONE);
                txtNoResult.setText("No Review Found.");
                txtNoResult.setVisibility(View.VISIBLE);
            }
        } else {
            itemReviewRecycler.setVisibility(View.GONE);
            txtNoResult.setText("No Review Found.");
            txtNoResult.setVisibility(View.VISIBLE);
        }
        layoutWriteReview.setOnClickListener(this);
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Reviews", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutWriteReview:
                showReviewPopup();
                break;
        }
    }

    private void showReviewPopup() {


        if (reviewDialog == null) {
            reviewDialog = new Dialog(this);
            reviewDialog.getWindow().setBackgroundDrawableResource(R.color.activityBackground);
            reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            if (reviewDialog.isShowing()) {
                reviewDialog.hide();
            }
        }

        reviewDialog.setContentView(R.layout.review_popup);
        RelativeLayout layoutReviewPopup = (RelativeLayout) reviewDialog
                .findViewById(R.id.layoutReviewPopup);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                AppConstant.getScreenWidth(this), ActionBar.LayoutParams.MATCH_PARENT);

        layoutReviewPopup.setLayoutParams(params);

        reviewDialog.show();

        rattingBar = (RatingBar) reviewDialog.findViewById(R.id.rattingBar);
        etReview = (EditText) reviewDialog.findViewById(R.id.etReview);
        btnCloseReview = (Button) reviewDialog.findViewById(R.id.btnClose);
        btnSendReview = (Button) reviewDialog.findViewById(R.id.btnSend);

        DrawableCompat.setTint(DrawableCompat.wrap(((LayerDrawable) rattingBar.getProgressDrawable()).getDrawable(2)), Color.YELLOW);

        if (userReviewObject != null) {
            rattingBar.setRating(Float.parseFloat(String.valueOf(JSONData.getInt(userReviewObject, "rating"))));
            etReview.setText(JSONData.getString(userReviewObject, "review"));
        }
//        if (ourRatting > 0) {
//            rattingBar.setRating((float) ourRatting);
//            Log.d("ourReview", " " + ourReview);
//            if (null != ourReview) {
//                etReview.setText(ourReview);
//            }
//        }

        btnCloseReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reviewDialog.hide();
            }
        });

        btnSendReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (rattingBar.getRating() == 0) {
                    AppConstant.showToastShort(ItemReviewActivity.this, "Please give rate");
                } else {
                    try {
                        if (AppConstant.isNetworkAvailable(ItemReviewActivity.this)) {
                            new SendReviewTask((int) rattingBar.getRating(), etReview
                                    .getText().toString().trim()).execute();
                        } else {
                            AppConstant.showNetworkError(ItemReviewActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class SendReviewTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(ItemReviewActivity.this);
        private String message, review;
        private int ratting, responseCode;
        boolean flag;

        public SendReviewTask(int ratting, String review) {
            this.ratting = ratting;
            this.review = review;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser(ItemReviewActivity.this);
            JSONStringer jsonStringer;
            String[] data;
            try {
                jsonStringer = new JSONStringer().object().key("product_id")
                        .value(product_id).key("rating").value(ratting)
                        .key("review").value(review).endObject();

                if (userReviewObject == null) {
                    data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_rating_review, jsonStringer.toString());
                } else {
                    data = jsonParser.sendPutReq(Constants.api_v1 + Constants.api_rating_review + "/" + JSONData.getString(userReviewObject, "_id"), jsonStringer.toString());
                }
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    JSONObject jsonObject = new JSONObject(data[1]);
                    message = JSONData.getString(jsonObject, "message");
                    flag = JSONData.getBoolean(jsonObject, "flag");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (responseCode == 200) {
                    if (flag) {
                        if (null != reviewDialog && reviewDialog.isShowing()) {
                            reviewDialog.hide();
                        }
                        closeActivity();
                    }
                    AppConstant.showToastLong(ItemReviewActivity.this, message);
                } else {
                    AppConstant.unableConnectServer(ItemReviewActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
