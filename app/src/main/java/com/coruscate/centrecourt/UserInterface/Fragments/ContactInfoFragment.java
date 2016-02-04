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
import android.widget.EditText;
import android.widget.TextView;

import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by cis on 7/28/2015.
 */
public class ContactInfoFragment extends Fragment {
    public static final String TAG = "Contact Info";
    private MainActivity mainActivity;
    private EditText edtxtFirstName, edtxtLastName, edtxtEmail, edtxtPhone;
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
        View view = localInflater.inflate(R.layout.contact_info_fragment, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        edtxtFirstName = (EditText) view.findViewById(R.id.edtxtFirstName);
        edtxtLastName = (EditText) view.findViewById(R.id.edtxtLastName);
        edtxtEmail = (EditText) view.findViewById(R.id.edtxtEmail);
        edtxtPhone = (EditText) view.findViewById(R.id.edtxtPhone);
        btnNext = (Button) view.findViewById(R.id.btnNext);
    }

}
