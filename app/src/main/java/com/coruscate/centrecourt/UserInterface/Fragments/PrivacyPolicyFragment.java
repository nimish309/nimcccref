package com.coruscate.centrecourt.UserInterface.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;

/**
 * Created by cis on 7/30/2015.
 */
public class PrivacyPolicyFragment extends Fragment {
    public static final String TAG = "Privacy Policy";
    private MainActivity mainActivity;
    private TextView txtIntroductionTitle, txtIntroductionDiscription, txtCoverageTitle, txtCoverageDiscription,txtInfoCollectionTitle,txtInfoCollectionDiscription;


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
        View view = localInflater.inflate(R.layout.privacy_and_policy_fragment, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        txtIntroductionTitle = (TextView) view.findViewById(R.id.txtIntroductionTitle);
        txtIntroductionDiscription = (TextView) view.findViewById(R.id.txtIntroductionDiscription);
        txtCoverageTitle = (TextView) view.findViewById(R.id.txtCoverageTitle);
        txtCoverageDiscription = (TextView) view.findViewById(R.id.txtCoverageDiscription);
        txtInfoCollectionTitle = (TextView) view.findViewById(R.id.txtInfoCollectionTitle);
        txtInfoCollectionDiscription = (TextView) view.findViewById(R.id.txtInfoCollectionDiscription);
    }


}
