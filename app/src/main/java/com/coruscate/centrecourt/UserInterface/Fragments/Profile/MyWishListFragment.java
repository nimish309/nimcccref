package com.coruscate.centrecourt.UserInterface.Fragments.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.WishlistAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.TwoButtonListener;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.animators.FadeInAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by cis on 7/27/2015.
 */
public class MyWishListFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "My Wishlist";
    public static ArrayList<String> detailList;
    public static WishlistAdapter adapter;
    private static MainActivity mainActivity;
    private static RecyclerView detailRecycler;
    private static TextView txtNoResult;
    private static ArrayList<String> detailListMainList;
    public static Bookends<WishlistAdapter> mBookends;
    private static FloatingActionButton fabBtn;
    private ProgressBar progressBar;
    private RecyclerView.LayoutManager myLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private SwipeRefreshLayout swipeLayout;
    public int layoutMode = 0;
    private Menu menu;
    private LinearLayout filterLayout;

    public static void removeItem(String id) {
        int position;
        JSONObject jobject;
        String itemId;
        for (int i = 0; i < detailList.size(); i++) {
            try {
                jobject = new JSONObject(detailList.get(i).toString());
                itemId = jobject.has("_id") ? (jobject.isNull("_id") ? "" : jobject.getString("_id")) : "";
                if (itemId.toString().equals(id.toString())) {
                    position = i;
                    detailList.remove(position);
                    UserDataPreferences.saveCategoryItemList(mainActivity, detailList);
                    mBookends.notifyItemRemoved(position);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (detailList.size() == 0) {

            makeRecyclerGone(AppConstant.noDataFound);
        }
    }

    private static void makeRecyclerGone(String strNoResult) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
        detailRecycler.setVisibility(View.GONE);
        fabBtn.setVisibility(View.GONE);
    }

    public static void filter(String strName) {
        JSONObject jsonObject;
        detailList.clear();
        if (null == strName) {
            detailList.addAll(detailListMainList);
        } else {
            strName = strName.toLowerCase(Locale.getDefault());
            for (int i = 0; i < detailListMainList.size(); i++) {
                try {
                    jsonObject = new JSONObject(detailListMainList.get(i).toString());
                    String name = jsonObject.has("name") ? (jsonObject.isNull("name") ? "" : jsonObject.getString("name")) : "";
                    if (name.toLowerCase(Locale.getDefault()).contains(strName)) {
                        detailList.add(detailListMainList.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (detailList.size() == 0) {
            makeRecyclerGone(AppConstant.noDataFound);
            mBookends.notifyDataSetChanged();
        } else {
            makeRecyclerVisible();
            mBookends.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }


//    @Override
//    public void onRefresh() {
//        // TODO Auto-generated method stub
//        if (AppConstant.isNetworkAvailable(getActivity())) {
//            new GetItemCategoriesTask(false, true).execute();
//            swipeLayout.setRefreshing(false);
//        } else {
//            swipeLayout.setRefreshing(false);
//            AppConstant.showTwoButtonDialog(mainActivity, "Network Error", AppConstant.noInternetMsg, null);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //sets custom theme to fragment
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.detail_fragment, container, false);
        initializeViews(view);
        fabBtn.setOnClickListener(this);

        if (AppConstant.isNetworkAvailable(mainActivity)) {
            new GetWishListTask(true, false).execute();
        } else {
            fabBtn.setVisibility(View.GONE);
            makeRecyclerGone(AppConstant.noInternetMsg);
            AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                @Override
                public void button1_Click() {
                    new GetWishListTask(true, false).execute();
                }

                @Override
                public void button2_Click() {

                }
            });
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        menu.findItem(R.id.itemMyCart).setVisible(true);
        menu.findItem(R.id.itemSearch).setVisible(true);

    }

    @SuppressLint("NewApi")
    private void initializeViews(View view) {

        fabBtn = (FloatingActionButton) view.findViewById(R.id.floatingBth_1);
        fabBtn.setVisibility(View.VISIBLE);
        fabBtn.setImageResource(R.drawable.icon_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        txtNoResult = (TextView) view.findViewById(R.id.txtNoResult);
        progressBar.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.GONE);
        filterLayout = (LinearLayout) view.findViewById(R.id.filterLayoutMain);
        filterLayout.setVisibility(View.GONE);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.item_swipe_container_list);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                if (AppConstant.isNetworkAvailable(getActivity())) {
                    new GetWishListTask(false, true).execute();
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(false);
                    AppConstant.showTwoButtonDialog(mainActivity, "Network Error", AppConstant.noInternetMsg, null);
                }
            }
        });
        detailRecycler = (RecyclerView) view.findViewById(R.id.detailRecycler);
        detailRecycler.setHasFixedSize(true);
        detailRecycler.setItemAnimator(new FadeInAnimator());
        detailRecycler.getItemAnimator().setRemoveDuration(500);

        FrameLayout.LayoutParams recyclerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        recyclerLayoutParams.setMargins(0, 0, 0, 0);
        detailRecycler.setLayoutParams(recyclerLayoutParams);

        myLayoutManager = new LinearLayoutManager(mainActivity);
        gridLayoutManager = new GridLayoutManager(mainActivity, 2);

        detailRecycler.setLayoutManager(gridLayoutManager);

        detailList = new ArrayList<>();
        detailListMainList = new ArrayList<>();
        adapter = new WishlistAdapter(detailList, mainActivity,this);
        mBookends = new Bookends<>(adapter);
        detailRecycler.setAdapter(mBookends);

        detailRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                               @Override
                                               public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                   super.onScrollStateChanged(recyclerView, newState);
//                                                   if (AppConstant.isNetworkAvailable(mainActivity)) {
////                                                       if (gridLayoutManager.isAttachedToWindow()) {
////                                                           if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
////                                                               swipeLayout.setEnabled(true);
////                                                           }
////
////                                                       } else {
////
////                                                       }
//                                                       Log.d("child", detailRecycler.getChildAt(0).getTop() + "");
//                                                       Log.d("top", (detailRecycler.getPaddingTop() + AppConstant.dpToPx(8, mainActivity)) + "");
//
//                                                       boolean enable = false;
//                                                       if (gridLayoutManager != null && gridLayoutManager.getChildCount() > 0) {
//                                                           enable = gridLayoutManager.findFirstVisibleItemPosition() == 0;
//                                                       }
//                                                       Log.d("enable", (enable) + "");
//
//                                                       swipeLayout.setEnabled(enable);
//
//                                                   }

                                               }

                                               @Override
                                               public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                   super.onScrolled(recyclerView, dx, dy);
                                                   int topRowVerticalPosition =
                                                           (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                                                   swipeLayout.setEnabled(topRowVerticalPosition >= 0);
                                               }

                                           }

        );
        if (AppConstant.isAndroid5())

        {
            fabBtn.setBackground(getActivity().getDrawable(R.drawable.ripple_fab));
        }

    }

    private void addFooter() {
        if (null != AppConstant.getProgressFooter(mainActivity)
                && mBookends != null) {
            mBookends.addFooter(AppConstant.getProgressFooter(mainActivity));
            mBookends.notifyDataSetChanged();
            detailRecycler.smoothScrollToPosition(totalItemCount);
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
        if (v.getId() == R.id.floatingBth_1) {
            swipeLayout.setRefreshing(false);
            if (detailList.size() > 0) {
                if (layoutMode == 0) {
                    layoutMode = 1;
                    fabBtn.setImageResource(R.drawable.icon_grid);
                    myLayoutManager.scrollToPosition(((GridLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                    detailRecycler.setLayoutManager(myLayoutManager);
                    mBookends.notifyDataSetChanged();
                  /*  notifyRemoveEach();
                    notifyAddEach();*/
                } else {
                    layoutMode = 0;
                    fabBtn.setImageResource(R.drawable.icon_layout);
                    gridLayoutManager.scrollToPosition(((LinearLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                    detailRecycler.setLayoutManager(gridLayoutManager);
                    mBookends.notifyDataSetChanged();
                    /*notifyRemoveEach();
                    notifyAddEach();
                */
                }
            }
        }
    }

    public void notifyRemoveEach() {
        for (int i = 0; i < detailList.size(); i++) {
            mBookends.notifyItemRemoved(i);
        }
    }

    public void notifyAddEach() {
        for (int i = 0; i < detailList.size(); i++) {
            mBookends.notifyItemInserted(i);
        }
    }

    private static void makeRecyclerVisible() {
        txtNoResult.setVisibility(View.GONE);
        detailRecycler.setVisibility(View.VISIBLE);
        fabBtn.setVisibility(View.VISIBLE);
    }

    public class GetWishListTask extends AsyncTask<Void, Void, Void> {
        boolean showProgressBar, flag = false;

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(mainActivity);
        JSONObject jsonObjects;
        boolean isRefreshed;
        private int responseCode;
        private ArrayList<String> categoryList;
        private JSONArray jsonArray;

        public GetWishListTask(boolean showProgressBar, boolean isRefreshed) {
            this.showProgressBar = showProgressBar;
            this.isRefreshed = isRefreshed;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showProgressBar) {
                dialog.setCancelable(true);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(mainActivity);
            try {
                String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_view_wishlist);
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jsonObjects = new JSONObject(data[1]);
                    flag = Boolean.parseBoolean(jsonObjects.getString("flag"));
                    if (flag) {
                        JSONObject jObject = jsonObjects.getJSONObject("data");
                        jsonArray = jObject.getJSONArray("itms");
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
            if (responseCode == 200) {
                //    swipeLayout.setRefreshing(true);
//                setEnabled(false)
                if (flag) {
                    if (isRefreshed) {
                        detailList.clear();
                        detailListMainList.clear();
                        swipeLayout.setRefreshing(false);
                        swipeLayout.setEnabled(false);
                    }
                    JSONObject jobj, object;
                    JSONArray jArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jobj = jsonArray.getJSONObject(i);
                            object = jobj.getJSONObject("item_details");
                            jArray.put(object);
                            detailList.add(object.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    detailListMainList.addAll(detailList);
                    UserDataPreferences.saveCategoryItemList(mainActivity, detailList);
                    //UserDataPreferences.saveItemCategory(mainActivity, jArray.toString());
                    if (detailList.size() > 0) {
                        makeRecyclerVisible();
                        mBookends.notifyDataSetChanged();
                    } else {
                        makeRecyclerGone(AppConstant.noDataFound);
                    }
                } else {
                    makeRecyclerGone(AppConstant.noDataFound);
                }
            } else if (responseCode != 200) {
                makeRecyclerGone(AppConstant.noInternetMsg);
                AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                    @Override
                    public void button1_Click() {
                        new GetWishListTask(true, false).execute();
                    }

                    @Override
                    public void button2_Click() {

                    }
                });
            }
        }

    }
}
