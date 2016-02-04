package com.coruscate.centrecourt.UserInterface.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AvenuesParams;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.RSAUtility;
import com.coruscate.centrecourt.Util.ServiceUtility;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import java.net.URLEncoder;


public class WebViewActivity extends Activity {
    private ProgressDialog dialog;
    Intent mainIntent;
    String encVal, redirect_url, cancel_url;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_webview);
        mainIntent = getIntent();
//        if (mainIntent.getBooleanExtra(AvenuesParams.IS_GIFT_VOUCHER, false)) {
//            finish();
//            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//        } else {
//            setResult(11);
//            finish();
//            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//            //set result code
//        }
        // // Calling async task to get display content
         new RenderView().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class RenderView extends AsyncTask<Void, Void, Void> {

        JSONObject jobj;
        int respCode = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dialog = new ProgressDialog(WebViewActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            try {
                JSONParser jParser = new JSONParser(WebViewActivity.this);
                JSONObject reqJsonObj = new JSONObject();
                reqJsonObj.put("order_id", mainIntent.getStringExtra(AvenuesParams.ORDER_ID));

                String[] vResponse = jParser.sendPostReq(Constants.api_v1 + Constants.api_get_rsa, reqJsonObj.toString());
                respCode = Integer.parseInt(vResponse[0]);
                if (respCode == 200) {
                    if (!ServiceUtility.chkNull(vResponse[1]).equals("")
                            && ServiceUtility.chkNull(vResponse[1]).toString().indexOf("ERROR") == -1) {
                        StringBuffer vEncVal = new StringBuffer("");
                        jobj = new JSONObject(vResponse[1]);
                        if (jobj.getBoolean("flag")) {
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                            Log.d("AMOUNT", mainIntent.getStringExtra(AvenuesParams.AMOUNT));
                            JSONObject dataObj = jobj.getJSONObject("data");
                            redirect_url = dataObj.getString("redirect_url");
                            cancel_url = dataObj.getString("cancel_url");
                            encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), dataObj.getString("rsa_key"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (dialog.isShowing())
                dialog.dismiss();

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    // process the html as needed by the app
                    String status = null;
                    Log.d("MyJavaScriptInterface", html);
                    if (html.indexOf("Success") != -1) {
                        status = "Transaction Successful";
                    } else if (html.indexOf("Aborted") != -1) {
                        status = "Transaction Cancelled!";
                    } else {
                        status = "Transaction Failed!";
                    }
                    showPopup(status);
                }
            }

            final WebView webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    if (url.contains("thecentrecourtcakes")) {
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.documentElement.outerHTML);");
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.body.innerHTML);");
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.documentElement.innerHTML);");
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.documentElement.outerHTML);");
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.documentElement.innerHTML);");
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });

			/* An instance of this class will be registered as a JavaScript interface */
            StringBuffer params = new StringBuffer();
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, mainIntent.getStringExtra(AvenuesParams.ORDER_ID)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, redirect_url));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, cancel_url));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal)));

            params.append(ServiceUtility.addToPostParams(AvenuesParams.LANGUAGE, "EN"));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME, mainIntent.getStringExtra(AvenuesParams.BILLING_NAME)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS, mainIntent.getStringExtra(AvenuesParams.BILLING_ADDRESS)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY, mainIntent.getStringExtra(AvenuesParams.BILLING_CITY)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE, mainIntent.getStringExtra(AvenuesParams.BILLING_STATE)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP, mainIntent.getStringExtra(AvenuesParams.BILLING_ZIP)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY, mainIntent.getStringExtra(AvenuesParams.BILLING_COUNTRY)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL, mainIntent.getStringExtra(AvenuesParams.BILLING_TEL)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL, mainIntent.getStringExtra(AvenuesParams.BILLING_EMAIL)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_NAME, mainIntent.getStringExtra(AvenuesParams.DELIVERY_NAME)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ADDRESS, mainIntent.getStringExtra(AvenuesParams.DELIVERY_ADDRESS)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_CITY, mainIntent.getStringExtra(AvenuesParams.DELIVERY_CITY)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_STATE, mainIntent.getStringExtra(AvenuesParams.DELIVERY_STATE)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ZIP, mainIntent.getStringExtra(AvenuesParams.DELIVERY_ZIP)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_COUNTRY, mainIntent.getStringExtra(AvenuesParams.DELIVERY_COUNTRY)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_TEL, mainIntent.getStringExtra(AvenuesParams.DELIVERY_TEL)));

            params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM3, "mobapp"));

            if (mainIntent.getBooleanExtra(AvenuesParams.IS_GIFT_VOUCHER, false)) {
                params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM1, mainIntent.getStringExtra(AvenuesParams.MERCHANT_PARAM1)));
                params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM2, mainIntent.getStringExtra(AvenuesParams.MERCHANT_PARAM2)));
            }

            String vPostParams = params.substring(0, params.length() - 1);
            try {
                webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                showToast("Exception occured while opening webview.");
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }


    private void showPopup(String status) {
        String msg = null;
        if (mainIntent.getBooleanExtra(AvenuesParams.IS_GIFT_VOUCHER, false))
            msg = "Gift Voucher Id:" + mainIntent.getStringExtra(AvenuesParams.ORDER_ID)+" \nAmount"+mainIntent.getStringExtra(AvenuesParams.ORDER_ID);
        else
            msg = "Order Id:" + mainIntent.getStringExtra(AvenuesParams.ORDER_ID)+" \nAmount"+mainIntent.getStringExtra(AvenuesParams.ORDER_ID);

        new AlertDialog.Builder(WebViewActivity.this)
                .setTitle(status)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mainIntent.getBooleanExtra(AvenuesParams.IS_GIFT_VOUCHER, false)) {
                            finish();
                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        } else {
                            setResult(11);
                            finish();
                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                            //set result code
                        }
                    }
                }).setCancelable(true).show();

    }
}