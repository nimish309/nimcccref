package com.coruscate.centrecourt.UserInterface.Fragments.Profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.OrderFragAdapter;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.TwoButtonListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cis on 7/27/2015.
 */
@SuppressWarnings("ALL")
public class OrderFragment extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private RecyclerView recOrderFrag;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;
    private Bookends<OrderFragAdapter> mBookends;
    private TextView txtNoResult;
    private RecyclerView.LayoutManager myLayoutManager;
    private OrderFragAdapter adapter;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ArrayList<String> OrderList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //sets custom theme to fragment
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.order_fragment, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        txtNoResult = (TextView) view.findViewById(R.id.txtNoResult);
        progressBar.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.GONE);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.item_swipe_container_list);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                if (AppConstant.isNetworkAvailable(getActivity())) {
//                    new GetItemCategoriesTask(false, true).execute();
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(false);
                    AppConstant.showTwoButtonDialog(mainActivity, "Network Error", AppConstant.noInternetMsg, null);
                }
            }
        });
        recOrderFrag = (RecyclerView) view.findViewById(R.id.recOrderFrag);

        recOrderFrag.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(getActivity());
        recOrderFrag.setLayoutManager(myLayoutManager);
        OrderList = new ArrayList<>();
        adapter = new OrderFragAdapter(OrderList, mainActivity);
        mBookends = new Bookends<>(adapter);
        recOrderFrag.setAdapter(adapter);
        recOrderFrag.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (AppConstant.isNetworkAvailable(mainActivity)) {
                    if (myLayoutManager.isAttachedToWindow()) {
                        if (myLayoutManager.getItemCount() == 0) {

                            swipeLayout.setEnabled(true);

                        }

                    } else {

                    }

                }

           /*     if (UserDataPreferences.isLogin(mainActivity)) {
                    visibleItemCount = mainLayoutManager.getChildCount();
                    totalItemCount = mainLayoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) mainLayoutManager).findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (totalRows > ((page - 1) * limit) && !running) {
                            if (AppConstant.isNetworkAvailable(mainActivity)) {
                                running = true;
                                if (task != null) {
                                    task.cancel(true);
                                }
                                task = new GetSearchResult().execute();
                            } else {
                                AppConstant.showNetworkError(mainActivity);
                            }
                        }
                    }
                }*/
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }


        });


    }

    private void makeRecyclerVisible() {
        txtNoResult.setVisibility(View.GONE);
        recOrderFrag.setVisibility(View.VISIBLE);

    }

    private void makeRecyclerGone(String strNoResult) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
        recOrderFrag.setVisibility(View.GONE);
    }


    private void addFooter() {
        if (null != AppConstant.getProgressFooter(mainActivity)
                && mBookends != null) {
            mBookends.addFooter(AppConstant.getProgressFooter(mainActivity));
            mBookends.notifyDataSetChanged();
            recOrderFrag.smoothScrollToPosition(totalItemCount);
        }
    }

    private void removeFooter() {
        if (null != AppConstant.getProgressFooter(mainActivity)
                && mBookends != null) {
            mBookends
                    .removeFooter(AppConstant.getProgressFooter(mainActivity));
            mBookends.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
    }

    public class GetOrderTask extends AsyncTask<Void, Void, Void> {
        boolean showProgressBar, flag = false;
        ProgressDialog dialog;
        JSONObject jsonObjects;
        boolean isRefreshed;
        private int responseCode;
        private ArrayList<String> categoryList;
        private JSONArray jsonArray;

        public GetOrderTask(boolean showProgressBar, boolean isRefreshed) {
            this.showProgressBar = showProgressBar;
            this.isRefreshed = isRefreshed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showProgressBar) {
                dialog = new ProgressDialog(mainActivity);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(mainActivity);
            try {
                String data[] = jParser.sendGetReq("");
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jsonObjects = new JSONObject(data[1]);
                    flag = Boolean.parseBoolean(jsonObjects.getString("flag"));
                    if (flag) {
                        jsonArray = jsonObjects.getJSONArray("data");
//                        UserDataPreferences.saveItemCategory(mainActivity, jsonArray.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (showProgressBar) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
            if (responseCode == 200 && flag) {
//                swipeLayout.setRefreshing(false);
//                setEnabled(false)

                if (isRefreshed) {
                    swipeLayout.setRefreshing(false);
                    swipeLayout.setEnabled(false);
                }
//                AppConstant.copyJSONArray(jsonArray, detailList);
//                if () {
//                    makeRecyclerVisible();
//                    mBookends.notifyDataSetChanged();
//                } else {
//                    makeRecyclerGone(AppConstant.noDataFound);
//                }
            } else if (responseCode != 200) {
                makeRecyclerGone(AppConstant.noInternetMsg);
                AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                    @Override
                    public void button1_Click() {
//                        new GetItemCategoriesTask(true, false).execute();
                    }

                    @Override
                    public void button2_Click() {

                    }
                });
            }
        }

    }
}