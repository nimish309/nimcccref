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
 * Created by cis on 7/28/2015.
 */
public class AddressFragment extends Fragment {
    public static final String TAG = "Contact Info";
    private MainActivity mainActivity;
    private TextView edtxtFirstName, edtxtLastName, edtxtEmail, edtxtPhone;
    private Button btnNext;

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
        View view = localInflater.inflate(R.layout.address_fragment, container, false);

        return view;
    }
}
