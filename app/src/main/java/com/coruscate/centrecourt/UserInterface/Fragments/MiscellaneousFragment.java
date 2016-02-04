package com.coruscate.centrecourt.UserInterface.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.coruscate.centrecourt.Adapter.MiscellaneousAdapter;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.Model.MiscellaneousData;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.MiscellaneousActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import recycler.Util;

/**
 * Created by cis on 7/25/2015.
 */
public class MiscellaneousFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "Detail";
    public static MiscellaneousAdapter adapter;
    private static Bookends<MiscellaneousAdapter> mBookends;
    private static ArrayList<MiscellaneousData> miscellaneousList = new ArrayList<>();
    private static ArrayList<String> miscellaneousListMainList = new ArrayList<>();
    private static RecyclerView miscellaneousRecycler;
    private static TypefacedTextView txtNoResult;
    private MiscellaneousActivity mainActivity;
    private RecyclerView.LayoutManager myLayoutManager;
    private String id;
    private JSONArray jsonArray;
    private Menu menu;


    private static void makeRecyclerGone(String strNoResult) {
        miscellaneousRecycler.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MiscellaneousActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //sets custom theme to fragment
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.miscellaneous_fragment, container, false);
        initializeViews(view);
        try {
            Bundle bdl = getArguments();
            id = bdl.getString("id");
        } catch (Exception e) {
        }
        String s = UserDataPreferences.getMiscellaneousInfo(mainActivity);
        if (id != null && s != null) {
            miscellaneousList.clear();
            miscellaneousListMainList.clear();
            try {
                Log.d("Json Data", s);
                JSONObject jsonObject = new JSONObject(s);
                Log.d("json Object", jsonObject.toString());
                JSONObject proObj = jsonObject.getJSONObject("products");
                jsonArray = proObj.getJSONArray("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppConstant.copyJSONArray(jsonArray, miscellaneousListMainList);
            Log.d(TAG, miscellaneousListMainList.size() + "");
            if (miscellaneousListMainList.size() > 0) {
                JSONObject jsonObject;
                for (int i = 0; i < miscellaneousListMainList.size(); i++) {
                    try {
                        jsonObject = new JSONObject(miscellaneousListMainList.get(i));
                        Log.d("jsonObject==>", jsonObject.toString());
                        JSONArray jsonArray1 = jsonObject.getJSONArray("sub_cat_ids");
                        Log.d("jsonArray1==>", jsonArray1.length() + "");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            String str = jsonArray1.getString(j);
                            Log.d(TAG, str);
                            Log.d(TAG, id);
                            if (str.equals(id)) {
                                MiscellaneousData data = new MiscellaneousData();
                                data.setData(miscellaneousListMainList.get(i));
                                data.setIsSelected(false);
                                data.setQty(jsonObject.has("min_ordered_quantity") ? (jsonObject.isNull("min_ordered_quantity") ? 1 : jsonObject.getInt("min_ordered_quantity")) : 1);
                                miscellaneousList.add(data);
//                                miscellaneousList.add(miscellaneousListMainList.get(i));
//                                Log.d("sizeofarray", String.valueOf(miscellaneousList.size()));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if (miscellaneousList.size() == 0) {
                makeRecyclerGone("Sorry,No Item Found.");
            }
        } else {
            makeRecyclerGone("Sorry,No Item Found.");
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        menu.findItem(R.id.itemMyCart).setVisible(false);
        menu.findItem(R.id.itemSearch).setVisible(false);
    }

    @SuppressLint("NewApi")
    private void initializeViews(View view) {
        miscellaneousRecycler = (RecyclerView) view.findViewById(R.id.miscellaneousRecycler);
        txtNoResult = (TypefacedTextView) view.findViewById(R.id.txtNoResult);
        txtNoResult.setVisibility(View.GONE);
        miscellaneousRecycler.setHasFixedSize(true);
        miscellaneousRecycler.setItemAnimator(new ScaleInAnimator());
        miscellaneousRecycler.getItemAnimator().setRemoveDuration(500);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        miscellaneousRecycler.setLayoutManager(myLayoutManager);
        miscellaneousList = new ArrayList<>();
        adapter = new MiscellaneousAdapter(miscellaneousList, mainActivity);
        miscellaneousListMainList = new ArrayList<>();
        miscellaneousRecycler.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingBth) {

            if (miscellaneousList.size() > 0) {
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
