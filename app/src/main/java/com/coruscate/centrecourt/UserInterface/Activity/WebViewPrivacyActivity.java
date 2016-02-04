package com.coruscate.centrecourt.UserInterface.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.coruscate.centrecourt.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WebViewPrivacyActivity extends Activity {

    @InjectView(R.id.webview)
    WebView webview;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_webview);
        ButterKnife.inject(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.thecentrecourtcakes.com/privacy-policy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }



}