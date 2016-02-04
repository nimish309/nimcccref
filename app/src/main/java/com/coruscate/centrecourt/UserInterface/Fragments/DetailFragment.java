package com.coruscate.centrecourt.UserInterface.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.DetailAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.RangeSeekBar;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedCheckBox;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.TwoButtonListener;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.animators.FadeInAnimator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 7/25/2015.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "Detail";
    public static RecyclerView detailRecycler;
    public static DetailAdapter adapter;
    public static ArrayList<String> detailList;
    private static Bookends<DetailAdapter> mBookends;
    private static TextView txtNoResult;
    private static ArrayList<String> detailListMainList;
    private static ImageView floatingBth;
    private static LinearLayout filterLayoutMain;
    public int layoutMode = 0, filterMode = 0;
    @InjectView(R.id.txtPriceMin)
    TypefacedTextView txtPriceMin;
    @InjectView(R.id.txtPriceMax)
    TypefacedTextView txtPriceMax;
    @InjectView(R.id.txtWeightMin)
    TypefacedTextView txtWeightMin;
    @InjectView(R.id.txtWeightMax)
    TypefacedTextView txtWeightMax;
    ArrayList<String> typeList;
    @InjectView(R.id.btnClear)
    TypedfacedButton btnClear;
    @InjectView(R.id.btnOk)
    TypedfacedButton btnOk;
    @InjectView(R.id.imgAddFlavour)
    ImageView imgAddFlavour;
    @InjectView(R.id.lenearLayoutFlavour)
    LinearLayout lenearLayoutFlavour;
    @InjectView(R.id.filterLayout)
    LinearLayout filterLayout;
    @InjectView(R.id.cbEggless)
    TypefacedCheckBox cbEggless;
    @InjectView(R.id.cbEggWith)
    TypefacedCheckBox cbEggWith;
    @InjectView(R.id.floatingBth_1)
    FloatingActionButton fabBtn;
    private ArrayList<HashMap<String, String>> flavourList = new ArrayList<HashMap<String, String>>();
    private ArrayList<String> selectedFlavour = new ArrayList<>();
    private ArrayList<String> selectedFlavourId = new ArrayList<>();
    private ArrayList<Integer> selectedFlavourPosition = new ArrayList<>();
    private ArrayList<String> detailOldList;
    private MainActivity mainActivity;
    private ProgressBar progressBar;
    private RecyclerView.LayoutManager myLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private int totalItemCount;
    private SwipeRefreshLayout swipeLayout;
    private String id, type;
    private Menu menu;
    private ScrollView filterLinearLayout;
    private LinearLayout lenearLayoutSeekBar;
    private RangeSeekBar<Double> PriceRangeSeekBar;
    private RangeSeekBar<Double> WeightRangeSeekBar;
    private String queryStringdetail = "";
    private String filterString = "", filterFlavourId = "";
    private TextView txtFiltered;
    private LinearLayout item, linearLayoutFilter;
    private TextView txtButton;

    public static void notifyAdapter(int position) {
        mBookends.notifyItemChanged(position);
    }

    private static void makeRecyclerVisible() {
        txtNoResult.setVisibility(View.GONE);
        detailRecycler.setVisibility(View.VISIBLE);
        filterLayoutMain.setVisibility(View.VISIBLE);
    }

    private static void makeRecyclerGone(String strNoResult) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
        detailRecycler.setVisibility(View.GONE);
        filterLayoutMain.setVisibility(View.GONE);
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
                    jsonObject = new JSONObject(detailListMainList.get(i));
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
        View view = localInflater.inflate(R.layout.detail_fragment, container, false);

        ButterKnife.inject(this, view);

        initializeViews(view);
        setSeekBarValue();
        PriceRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                setFilterString();
            }
        });
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.lenearLayoutSeekBar);
        layout.addView(PriceRangeSeekBar);
        WeightRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                setFilterString();

            }
        });
        // Add to layout
        LinearLayout layoutweight = (LinearLayout) view.findViewById(R.id.lenearLayoutSeekBarWeihgt);
        layoutweight.addView(WeightRangeSeekBar);
        floatingBth.setOnClickListener(this);
        filterLayout.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        cbEggWith.setOnClickListener(this);
        cbEggless.setOnClickListener(this);
        imgAddFlavour.setOnClickListener(this);
        try {
            Bundle bdl = getArguments();
            id = bdl.getString("id");
        } catch (Exception e) {

        }
        if (id != null) {
            if (AppConstant.isNetworkAvailable(mainActivity)) {
                new GetItemCategoriesTask(true, false, "").execute();
            } else {
                floatingBth.setVisibility(View.GONE);

                makeRecyclerGone(AppConstant.noInternetMsg);
                AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                    @Override
                    public void button1_Click() {
                        new GetItemCategoriesTask(true, false, "").execute();
                    }

                    @Override
                    public void button2_Click() {

                    }
                });
            }
        } else {
            try {
                Bundle bdl = getArguments();
                type = bdl.getString("type");
            } catch (Exception e) {

            }
            if (type != null && type.toString().equals("NewArrivals")) {
                filterLayoutMain.setVisibility(View.GONE);

                fabBtn.setVisibility(View.VISIBLE);
                fabBtn.setImageResource(R.drawable.icon_layout);
                fabBtn.setOnClickListener(this);
                FrameLayout.LayoutParams recyclerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                recyclerLayoutParams.setMargins(0, 0, 0, 0);
                detailRecycler.setLayoutParams(recyclerLayoutParams);
                if (AppConstant.isNetworkAvailable(mainActivity)) {
                    new GetItemCategoriesTask(true, false, "NewArrivals").execute();
                } else {
                    floatingBth.setVisibility(View.GONE);

                    makeRecyclerGone(AppConstant.noInternetMsg);
                    AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                        @Override
                        public void button1_Click() {
                            new GetItemCategoriesTask(true, false, "NewArrivals").execute();
                        }

                        @Override
                        public void button2_Click() {

                        }
                    });
                }
            }
        }
        return view;
    }

    private void setFilterString() {
        filterString = "Price : " + PriceRangeSeekBar.getSelectedMinValue() + " - " + PriceRangeSeekBar.getSelectedMaxValue();
        if (cbEggless.isChecked()) {
            filterString += " EggLess : true";
        }
        if (cbEggWith.isChecked()) {
            filterString += " With Egg : true";
        }
        filterString += " Weight : " + WeightRangeSeekBar.getSelectedMinValue() + " - " + WeightRangeSeekBar.getSelectedMaxValue();
        txtPriceMin.setText(PriceRangeSeekBar.getSelectedMinValue().toString());
        txtPriceMax.setText(PriceRangeSeekBar.getSelectedMaxValue().toString());
        txtWeightMin.setText(WeightRangeSeekBar.getSelectedMinValue().toString());
        txtWeightMax.setText(WeightRangeSeekBar.getSelectedMaxValue().toString());
        txtFiltered.setText(filterString + "");
    }

    private void setSeekBarValue() {
        PriceRangeSeekBar.setRangeValues(0.0, 5000.0);
        PriceRangeSeekBar.setSelectedMinValue(0.0);
        PriceRangeSeekBar.setSelectedMaxValue(5000.0);
        WeightRangeSeekBar.setRangeValues(0.0, 10.0);

        WeightRangeSeekBar.setSelectedMinValue(0.0);
        WeightRangeSeekBar.setSelectedMaxValue(10.0);
        cbEggless.setChecked(false);
        cbEggWith.setChecked(false);
        filterString = "";
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
        floatingBth = (ImageView) view.findViewById(R.id.floatingBth);
        floatingBth.setImageResource(R.drawable.icon_layout);
        filterLayoutMain = (LinearLayout) view.findViewById(R.id.filterLayoutMain);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        txtNoResult = (TextView) view.findViewById(R.id.txtNoResult);
        progressBar.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.GONE);
        filterLinearLayout = (ScrollView) view.findViewById(R.id.filterLinearLayout);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.item_swipe_container_list);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        lenearLayoutSeekBar = (LinearLayout) view.findViewById(R.id.lenearLayoutSeekBar);
        PriceRangeSeekBar = new RangeSeekBar<>(mainActivity);
        WeightRangeSeekBar = new RangeSeekBar<>(mainActivity);
        txtFiltered = (TextView) view.findViewById(R.id.txtFiltered);
        txtFiltered.setSelected(true);
        linearLayoutFilter = (LinearLayout) view.findViewById(R.id.linearLayoutFilter);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                if (AppConstant.isNetworkAvailable(getActivity())) {
                    if (type != null) {
                        if (type.toString().equals("NewArrivals")) {
                            new GetItemCategoriesTask(false, true, type).execute();
                        }
                    } else {
                        new GetItemCategoriesTask(false, true, "").execute();
                    }
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
        myLayoutManager = new LinearLayoutManager(mainActivity);
        gridLayoutManager = new GridLayoutManager(mainActivity, 2);
        detailRecycler.setLayoutManager(gridLayoutManager);
        detailList = new ArrayList<>();
        detailOldList = new ArrayList<>();
        adapter = new DetailAdapter(detailList, mainActivity, this);
        detailListMainList = new ArrayList<>();
        mBookends = new Bookends<>(adapter);
        detailRecycler.setAdapter(mBookends);
        if (AppConstant.isAndroid5()) {
            btnOk.setBackground(mainActivity.getDrawable(R.drawable.ripple_accent_round_corner));
            btnClear.setBackground(mainActivity.getDrawable(R.drawable.ripple_accent_round_corner));
        }


        detailRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                               @Override
                                               public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                   super.onScrollStateChanged(recyclerView, newState);

                                                   if (layoutMode == 0) {

                                                       boolean enable = false;
                                                       if (myLayoutManager != null && myLayoutManager.getChildCount() > 0) {
                                                           enable = ((LinearLayoutManager) myLayoutManager).findFirstVisibleItemPosition() == 0 &&
                                                                   detailRecycler.getChildAt(0).getTop() == detailRecycler.getPaddingTop() + AppConstant.dpToPx(8, mainActivity);
                                                       }
                                                       Log.d("enable", (enable) + "");

                                                       swipeLayout.setEnabled(enable);
                                                   } else {
                                                       Log.d("child", detailRecycler.getChildAt(0).getTop() + "");
                                                       Log.d("top", (detailRecycler.getPaddingTop() + AppConstant.dpToPx(8, mainActivity)) + "");

                                                       boolean enable = false;
                                                       if (gridLayoutManager != null && gridLayoutManager.getChildCount() > 0) {
                                                           enable = ((GridLayoutManager) gridLayoutManager).findFirstVisibleItemPosition() == 0 &&
                                                                   detailRecycler.getChildAt(0).getTop() == detailRecycler.getPaddingTop() + AppConstant.dpToPx(8, mainActivity);
                                                       }
                                                       Log.d("enable", (enable) + "");

                                                       swipeLayout.setEnabled(enable);
                                                   }

//                                                   if (AppConstant.isNetworkAvailable(mainActivity)) {
//                                                       if (gridLayoutManager.isAttachedToWindow()) {
//                                                           if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                                                               swipeLayout.setEnabled(true);
//                                                           }
//
//                                                       } else {
//
//                                                       }
//
//                                                   }

                                               }

                                               @Override
                                               public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                   super.onScrolled(recyclerView, dx, dy);

                                               }

                                           }

        );
        try {
            JSONArray jsonArray = UserDataPreferences.getFlavourInfo(mainActivity);
            JSONObject object;
            HashMap<String, String> flavourItem;
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    flavourItem = new HashMap<>();
                    flavourItem.put("id", JSONData.getString(object, "_id"));
                    flavourItem.put("name", JSONData.getString(object, "name"));
                    flavourList.add(flavourItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingBth:
                swipeLayout.setRefreshing(false);
                if (detailList.size() > 0) {
                    if (layoutMode == 0) {
                        layoutMode = 1;
                        floatingBth.setImageResource(R.drawable.icon_grid);
                        myLayoutManager.scrollToPosition(((GridLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                        detailRecycler.setLayoutManager(myLayoutManager);
                        mBookends.notifyDataSetChanged();
                    } else {
                        layoutMode = 0;
                        floatingBth.setImageResource(R.drawable.icon_layout);
                        gridLayoutManager.scrollToPosition(((LinearLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                        detailRecycler.setLayoutManager(gridLayoutManager);
                        mBookends.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.filterLayout:
                if (filterMode == 0) {
                    filterMode = 1;
                    if (selectedFlavour.size() > 0) {
                        lenearLayoutFlavour.removeAllViews();
                        showflavour(selectedFlavour);
                    }
                    txtNoResult.setVisibility(View.GONE);
                    //filterLinearLayout.setVisibility(View.VISIBLE);
                    AppConstant.expand(linearLayoutFilter);
                    setFilterString();
                    txtFiltered.setText(filterString);
                    swipeLayout.setEnabled(false);
                    filterLinearLayout.setFocusable(true);
                    filterLinearLayout.setSmoothScrollingEnabled(true);
                } else {
                    detailRecycler.setFocusable(false);
                    filterMode = 0;
                    if (detailList.size() == 0) {
                        txtNoResult.setVisibility(View.VISIBLE);
                    }
                    //filterLinearLayout.setVisibility(View.GONE);
                    AppConstant.collapse(linearLayoutFilter);
                    txtFiltered.setText("");
                }
                break;
            case R.id.btnOk:
                if (AppConstant.isNetworkAvailable(mainActivity)) {
                    queryStringdetail = "";
                    filterFlavourId = "";
                    queryStringdetail = "?weight=" + WeightRangeSeekBar.getSelectedMinValue() + "," + WeightRangeSeekBar.getSelectedMaxValue() + "&price_range=" + PriceRangeSeekBar.getSelectedMinValue() + "," + PriceRangeSeekBar.getSelectedMaxValue();
                    if (cbEggless.isChecked()) {
                        queryStringdetail += "&eggless=true";
                    }
                    if (cbEggWith.isChecked()) {
                        queryStringdetail += "&eggwith=true";
                    }
                    for (int i = 0; i < selectedFlavourId.size(); i++) {
                        filterFlavourId += selectedFlavourId.get(i);
                        if (i + 1 != selectedFlavourId.size()) {
                            filterFlavourId += ",";
                        }
                    }
                    if (filterFlavourId.length() > 0) {
                        queryStringdetail += "&flavour_ids=" + filterFlavourId;
                    }
                    new GetItemFilterTask(true, false, queryStringdetail).execute();
                } else {
                    AppConstant.showNetworkError(mainActivity);
                }
                filterMode = 0;
                //filterLinearLayout.setVisibility(View.GONE);
                AppConstant.collapse(linearLayoutFilter);
                break;
            case R.id.btnClear:
                setSeekBarValue();
                detailList.clear();
                detailListMainList.clear();
                detailList.addAll(detailOldList);
                detailListMainList.addAll(detailOldList);
                UserDataPreferences.saveCategoryItemList(mainActivity, detailList);
                if (detailOldList.size() > 0) {
                    makeRecyclerVisible();
                } else {
                    makeRecyclerGone(AppConstant.noDataFound);
                }
                mBookends.notifyDataSetChanged();
                swipeLayout.setEnabled(true);
                swipeLayout.setFocusable(true);
                filterMode = 0;
                //filterLinearLayout.setVisibility(View.GONE);
                AppConstant.collapse(linearLayoutFilter);
                lenearLayoutFlavour.removeAllViews();
                selectedFlavour.clear();
                selectedFlavourId.clear();
                selectedFlavourPosition.clear();
                filterFlavourId = "";
                filterString = "";
                txtFiltered.setText(filterString);
                break;
            case R.id.cbEggless:
                setFilterString();
                break;
            case R.id.cbEggWith:
                setFilterString();
                break;
            case R.id.imgAddFlavour:
                showFlavourListlDialog(flavourList, "");
                break;
            case R.id.floatingBth_1:
                swipeLayout.setRefreshing(false);
                if (detailList.size() > 0) {
                    if (layoutMode == 0) {
                        layoutMode = 1;
                        fabBtn.setImageResource(R.drawable.icon_grid);
                        myLayoutManager.scrollToPosition(((GridLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                        detailRecycler.setLayoutManager(myLayoutManager);
                        mBookends.notifyDataSetChanged();
                    } else {
                        layoutMode = 0;
                        fabBtn.setImageResource(R.drawable.icon_layout);
                        gridLayoutManager.scrollToPosition(((LinearLayoutManager) detailRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
                        detailRecycler.setLayoutManager(gridLayoutManager);
                        mBookends.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @SuppressLint("NewApi")
    public void showFlavourListlDialog(final ArrayList<HashMap<String, String>> flavourList2, final String name) {

        final ArrayAdapter<String> Adapter;
        final Dialog popDetailDialog = new Dialog(mainActivity);
        popDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popDetailDialog.setContentView(R.layout.filter_popup);

        Button cancel, save;

        final ListView listShape = (ListView) popDetailDialog
                .findViewById(R.id.listShapePopUp);

        ArrayList<String> values = new ArrayList<>(flavourList2.size());

        for (int i = 0; i < flavourList2.size(); i++) {
            values.add(flavourList2.get(i).get("name"));
        }

        Adapter = new ArrayAdapter<String>(mainActivity,
                android.R.layout.simple_list_item_multiple_choice, values) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder vh;
                if (convertView == null) {
                    convertView = super.getView(position, convertView, parent);
                    vh = new ViewHolder();
                    vh.textView = (TextView) convertView
                            .findViewById(android.R.id.text1);
                    vh.textView.setTextColor(getResources().getColor(R.color.black));
                    AppConstant.setTextViewTypeFace(mainActivity, vh.textView);
                    convertView.setTag(vh);

                } else {
                    vh = (ViewHolder) convertView.getTag();
                }
                return super.getView(position, convertView, parent);
            }
        };
        listShape.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listShape.setItemsCanFocus(false);
        listShape.setAdapter(Adapter);

        cancel = (Button) popDetailDialog
                .findViewById(R.id.btnCancel);
        save = (Button) popDetailDialog.findViewById(R.id.btnSave);
        if (AppConstant.isAndroid5()) {
            cancel.setBackground(mainActivity.getDrawable(R.drawable.ripple_accent));
            save.setBackground(mainActivity.getDrawable(R.drawable.ripple_accent));
        }
        try {
            for (int i = 0; i < selectedFlavourPosition.size(); i++) {
                listShape.setItemChecked(selectedFlavourPosition.get(i), true);
            }
        } catch (NullPointerException e) {
        }

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popDetailDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listShape
                        .getCheckedItemPositions();
                selectedFlavour.clear();
                selectedFlavourId.clear();
                selectedFlavourPosition.clear();
                typeList = new ArrayList<>();
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);

                    if (checked.valueAt(i)) {
                        selectedFlavour.add(Adapter.getItem(position));
                        selectedFlavourPosition.add(position);
                        // selectedShapeId.put(shapeList2.get(position).get("id"));
                        selectedFlavourId
                                .add(flavourList2.get(position).get("id"));
                        typeList.add(flavourList2.get(position).get("type"));
                    }
                }
//                if (type.contains("1") || type.contains("3")) {
//                    if (name.equals("")) {
//                        layoutNormalSize.setVisibility(View.VISIBLE);
//                    } else {
//                        layoutNonNormalSize.setVisibility(View.VISIBLE);
//                    }
//                } else {
//
//                    if (name.equals("")) {
//                        clearNormalSize();
//                    } else {
//                        clearNonNormalSize();
//                    }
//                }

                // if (type.contains("3")) {
                // if (name.equals("")) {
                // layoutTapers.setVisibility(View.VISIBLE);
                // } else {
                // layoutNonTapers.setVisibility(View.VISIBLE);
                // }
                // } else {
                // if (name.equals("")) {
                // clearTapers();
                // } else {
                // clearNonTapers();
                // }
                // }

                showflavour(selectedFlavour);
                popDetailDialog.dismiss();
            }
        });

        popDetailDialog.show();

    }

    @SuppressLint("InflateParams")
    private void showflavour(List<String> flavourList) {
        lenearLayoutFlavour.removeAllViews();

        for (int i = 0; i < flavourList.size(); i++) {
            item = (LinearLayout) LayoutInflater.from(mainActivity).inflate(
                    R.layout.textview_filter_layout, null);
            txtButton = (TextView) item.findViewById(R.id.txtButton);
            txtButton.setText(flavourList.get(i));
            lenearLayoutFlavour.addView(item);
        }

    }

    static class ViewHolder {
        TextView textView;
    }

    public class GetItemCategoriesTask extends AsyncTask<Void, Void, Void> {
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(mainActivity);
        boolean showProgressBar, flag = false;
        //ProgressDialog dialog;
        JSONObject jsonObjects;
        boolean isRefreshed;
        String data[];
        private int responseCode;
        private ArrayList<String> categoryList;
        private JSONArray jsonArray;
        private String type;

        public GetItemCategoriesTask(boolean showProgressBar, boolean isRefreshed, String type) {
            this.showProgressBar = showProgressBar;
            this.isRefreshed = isRefreshed;
            this.type = type;
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
                if (type.toString().equals("NewArrivals")) {
                    data = jParser.sendGetReq(Constants.api_v1 + Constants.api_new_arrivals);
                } else {
                    data = jParser.sendGetReq(Constants.api_v1 + Constants.api_get_category_item + id + "/items");
                }
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jsonObjects = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jsonObjects, "flag");
                    if (flag) {
                        jsonArray = jsonObjects.getJSONArray("data");
                        // UserDataPreferences.saveItemCategory(mainActivity, jsonArray.toString());
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
                if (flag) {
                    // swipeLayout.setRefreshing(false);
                    // setEnabled(false)

                    if (isRefreshed) {
                        detailList.clear();
                        detailListMainList.clear();
                        swipeLayout.setRefreshing(false);
                        swipeLayout.setEnabled(false);
                    }

                    AppConstant.copyJSONArray(jsonArray, detailList);
                    UserDataPreferences.saveCategoryItemList(mainActivity, detailList);
                    if (detailList.size() > 0) {
                        detailListMainList.clear();
                        detailListMainList.addAll(detailList);
                        detailOldList.clear();
                        detailOldList.addAll(detailList);
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
                AppConstant.showTwoButtonDialog(mainActivity, "Error", "Unable to connect Server.", new TwoButtonListener() {
                    @Override
                    public void button1_Click() {
                        if (type.toString().equals("NewArrivals")) {
                            new GetItemCategoriesTask(true, false, "NewArrivals").execute();
                        } else {
                            new GetItemCategoriesTask(true, false, "").execute();
                        }
                    }

                    @Override
                    public void button2_Click() {

                    }
                });
            }
        }

    }

    public class GetItemFilterTask extends AsyncTask<Void, Void, Void> {
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(mainActivity);
        boolean showProgressBar, flag = false;
        //ProgressDialog dialog;
        JSONObject jObj;
        boolean isRefreshed;
        private int responseCode;
        private ArrayList<String> categoryList;
        private JSONArray jsonArray;
        private String type, message, queryString;

        public GetItemFilterTask(boolean showProgressBar, boolean isRefreshed, String queryString) {
            this.showProgressBar = showProgressBar;
            this.isRefreshed = isRefreshed;
            this.queryString = queryString;
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
            try {
                JSONParser jsonParser = new JSONParser(mainActivity);
                String data[] = jsonParser.sendGetReq(Constants.api_v1 + Constants.api_get_category_item + id + "/items/" + queryString);
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = jObj.has("message") ? (jObj.isNull("message") ? "" : jObj.getString("message")) : "";
                    if (flag) {
                        jsonArray = jObj.getJSONArray("data");
                        // UserDataPreferences.saveItemCategory(mainActivity, jsonArray.toString());

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
                if (flag) {
                    // swipeLayout.setRefreshing(false);
                    // setEnabled(false)

                    if (isRefreshed) {
                        detailList.clear();
                        detailListMainList.clear();
                        swipeLayout.setRefreshing(false);
                        swipeLayout.setEnabled(false);
                    }
                    detailList.clear();
                    AppConstant.copyJSONArray(jsonArray, detailList);
                    UserDataPreferences.saveCategoryItemList(mainActivity, detailList);
                    if (detailList.size() > 0) {
                        detailListMainList.clear();
                        detailListMainList.addAll(detailList);
                        makeRecyclerVisible();
                        mBookends.notifyDataSetChanged();
                    } else {
                        makeRecyclerGone(AppConstant.noDataFound);
                        filterLayoutMain.setVisibility(View.VISIBLE);

                    }
                } else {
                    makeRecyclerGone(AppConstant.noDataFound);
                    filterLayoutMain.setVisibility(View.VISIBLE);
                }
            } else if (responseCode != 200) {
                makeRecyclerGone(AppConstant.noInternetMsg);
                AppConstant.showTwoButtonDialog(mainActivity, "Network Error", "Please check your internet connection", new TwoButtonListener() {
                    @Override
                    public void button1_Click() {

                    }

                    @Override
                    public void button2_Click() {

                    }
                });
            }
        }

    }
}
