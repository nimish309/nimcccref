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
public class RefundPolicyFragment extends Fragment {
    public static final String TAG = "Refund Policy";
    private MainActivity mainActivity;
    private TextView txtReturnPolicyTitle, txtReturnPolicyDiscription, txtRefundPolicyTitle, txtRefundPolicyDiscription,txtCancelationPolicyTitle,txtCancelationPolicyDiscription;


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
        View view = localInflater.inflate(R.layout.refund_policy_fragment, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        txtReturnPolicyTitle = (TextView) view.findViewById(R.id.txtReturnPolicyTitle);
        txtReturnPolicyDiscription = (TextView) view.findViewById(R.id.txtReturnPolicyDiscription);
        txtRefundPolicyTitle = (TextView) view.findViewById(R.id.txtRefundPolicyTitle);
        txtRefundPolicyDiscription = (TextView) view.findViewById(R.id.txtRefundPolicyDiscription);
        txtCancelationPolicyTitle = (TextView) view.findViewById(R.id.txtCancelationPolicyTitle);
        txtCancelationPolicyDiscription = (TextView) view.findViewById(R.id.txtCancelationPolicyDiscription);

    }
}
