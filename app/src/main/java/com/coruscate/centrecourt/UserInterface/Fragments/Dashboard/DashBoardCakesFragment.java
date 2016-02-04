package com.coruscate.centrecourt.UserInterface.Fragments.Dashboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coruscate.centrecourt.Adapter.DashboardCakesAdapter;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.DetailFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Bookends;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by cis on 7/30/2015.
 */
@SuppressWarnings("ALL")
public class DashBoardCakesFragment extends Fragment {
    public static final String TAG = "Cakes";
    public static final String FRAGMENT_No = "fragmentNo";
    private MainActivity mainActivity;
    private RecyclerView dashboardCakesRecycler;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private Bookends<DashboardCakesAdapter> mBookends;
    private RecyclerView.LayoutManager myLayoutManager;
    private DashboardCakesAdapter adapter;
    private int fragPosition;
    private TextView txtNoResult;
    private ArrayList<String> categoryList;
    private ArrayList<String> subCategoryList;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private Menu menu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.dashboard_cakes_fragment, container, false);
        Bundle bdl = getArguments();
        fragPosition = bdl.getInt(FRAGMENT_No);
        initializeViews(view);
        setAdapterData();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        menu.findItem(R.id.itemMyCart).setVisible(true);
        menu.findItem(R.id.itemSearch).setVisible(false);

    }

    private void initializeViews(View view) {

        dashboardCakesRecycler = (RecyclerView) view.findViewById(R.id.DashboardCakesRecycler);
        dashboardCakesRecycler.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(getActivity());
        dashboardCakesRecycler.setLayoutManager(myLayoutManager);
        subCategoryList = new ArrayList<>();
        txtNoResult = (TextView) view.findViewById(R.id.txtNoResult);
        adapter = new DashboardCakesAdapter(subCategoryList, mainActivity, DashBoardCakesFragment.this);
        mBookends = new Bookends<>(adapter);
        dashboardCakesRecycler.setAdapter(adapter);
    }

    private void addFooter() {
        if (null != AppConstant.getProgressFooter(mainActivity)
                && mBookends != null) {
            mBookends.addFooter(AppConstant.getProgressFooter(mainActivity));
            mBookends.notifyDataSetChanged();
            dashboardCakesRecycler.smoothScrollToPosition(totalItemCount);
        }
    }

    private void removeFooter() {
        if (null != AppConstant.getProgressFooter(mainActivity)
                && mBookends != null) {
            mBookends.removeFooter(AppConstant.getProgressFooter(mainActivity));
            mBookends.notifyDataSetChanged();
        }
    }

    private void makeRecyclerVisible() {
        txtNoResult.setVisibility(View.GONE);
        dashboardCakesRecycler.setVisibility(View.VISIBLE);
    }

    private void makeRecyclerGone(String strNoResult) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
        dashboardCakesRecycler.setVisibility(View.GONE);
    }

    private void setAdapterData() {
        categoryList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(UserDataPreferences.getCategoryInfo(mainActivity));
            AppConstant.copyJSONArray(jsonArray, categoryList);
            JSONObject jobj;
            JSONArray jArray;
            for (int i = 0; i < categoryList.size(); i++) {
                if (i == fragPosition) {

                    jobj = new JSONObject(categoryList.get(i));
                    mainActivity.setActionbarTitle(jobj.getString("name"));
                    jArray = jobj.getJSONArray("sub_categories");
                    AppConstant.copyJSONArray(jArray, subCategoryList);
                    if (subCategoryList.size() == 0) {
                        makeRecyclerGone("Opps!!! No cake available for this category.");
                    } else {
                        makeRecyclerVisible();
                        mBookends.notifyDataSetChanged();
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}