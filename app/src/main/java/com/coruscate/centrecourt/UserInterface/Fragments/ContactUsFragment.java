package com.coruscate.centrecourt.UserInterface.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Created by cis on 7/27/2015.
 */
public class ContactUsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "Contact Us";
    private MainActivity mainActivity;
    private GoogleMap googleMap;
    private TextView txtLocationAddress, edtxtContactInquiry;
    private Button btnSubmit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.itemMyCart).setVisible(false);
        menu.findItem(R.id.itemSearch).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.contact_us_fragment, container, false);
        initializeViews(view);
        btnSubmit.setOnClickListener(this);
        return view;
    }

    @SuppressLint("NewApi")
    private void initializeViews(View view) {
        txtLocationAddress = (TextView) view.findViewById(R.id.txtLocationAddress);
        edtxtContactInquiry = (TextView) view.findViewById(R.id.edtxtContactInquiry);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        if (AppConstant.isAndroid5()) {
            btnSubmit.setBackground(mainActivity.getDrawable(R.drawable.ripple_accent));
        }
        googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        setLatLang(Double.valueOf("21.1760616"), Double.valueOf("72.795595"));
    }

    public void setLatLang(Double strLat, Double strLong) {
        try {
            if (strLat != 0 && strLong != 0) {
                LatLng curPosition = new LatLng(strLat, strLong);
                CameraPosition cp = new CameraPosition(curPosition, 12, 0, 0);
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, null);
                MarkerOptions marker = new MarkerOptions()
                        .title("The Centre Court cakes")
                        .position(curPosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return false;
                    }

                });

                googleMap.addMarker(marker);
            } else {
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {
            if (edtxtContactInquiry.getText().toString().length() > 0) {
                if (AppConstant.isNetworkAvailable(mainActivity)) {
                    new ContactUsTask(edtxtContactInquiry.getText().toString()).execute();
                } else {
                    AppConstant.showNetworkError(mainActivity);
                }
            } else {
                AppConstant.displayErroMessage(view, "Must enter inquiry information", mainActivity);
            }
        }
    }

    private class ContactUsTask extends AsyncTask<Void, Void, Void> {
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(mainActivity);
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        String message;

        public ContactUsTask(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                JSONParser jsonParser = new JSONParser(mainActivity);
                JSONObject object = new JSONObject(UserDataPreferences.getUserInfo(mainActivity));
                JSONStringer jsonData = new JSONStringer().object()
                        .key("email").value(JSONData.getString(object, "email")).key("message").value(message).endObject();
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_Contact, jsonData.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = JSONData.getString(jObj, "message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (responseCode == 200) {
                    if (flag) {
                        AppConstant.showToastShort(mainActivity,message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}