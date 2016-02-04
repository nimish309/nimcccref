package com.coruscate.centrecourt.UserInterface.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;

/**
 * Created by cis on 7/30/2015.
 */
public class TermsAndCondition extends Fragment {
    public static final String TAG = "Terms & condition";
    private MainActivity mainActivity;
    private TextView txtIntroductionTitle, txtIntroductionDiscription, txtSitProhibitioTitle, txtSitProhibitioDiscription;

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
        View view = localInflater.inflate(R.layout.terms_and_condition_fragment, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        txtIntroductionTitle = (TextView) view.findViewById(R.id.txtIntroductionTitle);
        txtIntroductionDiscription = (TextView) view.findViewById(R.id.txtIntroductionDiscription);
        txtSitProhibitioTitle = (TextView) view.findViewById(R.id.txtSitProhibitioTitle);
        txtSitProhibitioDiscription = (TextView) view.findViewById(R.id.txtSitProhibitioDiscription);

    }

}