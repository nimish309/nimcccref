package com.coruscate.centrecourt.UserInterface;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.LoginActivity;
import com.coruscate.centrecourt.UserInterface.Activity.PermissionIntro;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by CIS-16 on 7/3/2015.
 */
public class SplashActivity extends AppCompatActivity {
    Animation anim;
    @InjectView(R.id.layoutCantConnect)
    LinearLayout layoutCantConnect;
    @InjectView(R.id.btnTryAgain)
    TypedfacedButton btnTryAgain;
    @InjectView(R.id.liniarLayoutSpalsh)
    LinearLayout liniarLayoutSpalsh;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        LayoutTransition layoutTransition = liniarLayoutSpalsh.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

////        try {
////            AppConstant.setToolBarColor(SplashActivity.this);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }

        if (AppConstant.isAndroid5()) {
            btnTryAgain.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        getCatagory();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutCantConnect.setVisibility(View.GONE);
                getCatagory();
            }
        });

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),  //Replace your package name here
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                System.out.println(Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//                AppConstant.showSingleButtonAlertDialog(SplashActivity.this, "HAsh Key", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

    }

    private void getCatagory() {
        if (AppConstant.isNetworkAvailable(SplashActivity.this)) {
            if (AppConstant.isAndroid6()) {
                if (!UserDataPreferences.isFirstTime(SplashActivity.this)) {
                    Intent intent = new Intent(SplashActivity.this, PermissionIntro.class);
                    startActivityForResult(intent, 1);
                } else {
                    new GetCategoriesTask(false).execute();
                }
            } else {
                new GetCategoriesTask(false).execute();
            }
        } else {
            layoutCantConnect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            new GetCategoriesTask(false).execute();
        }
    }

    public class GetCategoriesTask extends AsyncTask<Void, Void, Void> {
        boolean showProgressBar;
        ProgressDialog dialog;
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        private JSONArray jsonArray;


        public GetCategoriesTask(boolean showProgressBar) {
            this.showProgressBar = showProgressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showProgressBar) {
                dialog = new ProgressDialog(SplashActivity.this);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(SplashActivity.this);
            try {
                String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_get_category);
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    if (flag) {
                        jsonArray = jObj.getJSONArray("data");
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
            try {
                if (showProgressBar) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
                if (responseCode == 200 && flag) {
                    UserDataPreferences.saveCategoryInfo(SplashActivity.this, jsonArray.toString());
//                    Log.d("splash", UserDataPreferences.getToken(SplashActivity.this));
                    String s = UserDataPreferences.getToken(SplashActivity.this);
//                    Log.d("splashData", s);
                    if (s.equals("")) {
                        final Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.start_activity, R.anim.close_activity);
                        finish();
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        Intent i;
                        if (((calendar.getTimeInMillis() - UserDataPreferences.getLoginDate(SplashActivity.this)) / (24 * 60 * 60 * 1000)) > 364) {
                            i = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.animation, R.anim.animation2);
                            finish();
                        } else {
                            i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.animation, R.anim.animation2);
                            finish();
                        }
                    }

                } else if (responseCode != 200) {
                    layoutCantConnect.setVisibility(View.VISIBLE);
                } else if (responseCode == 200 && !flag) {
//                    AppConstant.displayErroMessage(liniarLayoutSpalsh, "Unable to reach Server.App will close now.", SplashActivity.this);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 1500);
                    layoutCantConnect.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
