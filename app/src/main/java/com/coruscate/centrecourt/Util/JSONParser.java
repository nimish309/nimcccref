package com.coruscate.centrecourt.Util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class JSONParser {
    static final String COOKIES_HEADER = "Set-Cookie";
    //    static InputStream is = null;
    static JSONObject jObj = null;
    // static String jsonString = "";
    Context context;

    static String json = "";

    public JSONParser(Context context) {
        this.context = context;

    }

    public JSONParser() {
    }

    public String sendReq(String strUrl, int reqType, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }
        conn.setRequestProperty("Authorization", "Bearer " + UserDataPreferences.getToken(context));
        if (reqType == 0) {
            conn.setRequestMethod("GET");
        } else if (reqType == 1) {
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setFixedLengthStreamingMode(jsonData.getBytes().length);

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(jsonData);
            out.close();
        }
        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null,
                        HttpCookie.parse(cookie).get(0));
            }
        }

        // read the response
        System.out.println("Response Code: " + conn.getResponseCode() + "---"
                + cookieManager.getCookieStore().getCookies());
        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        return jsonString;
    }

    public String[] sendPostReq(String strurl, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestProperty("Authorization", "Bearer " + UserDataPreferences.getToken(context));
        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setFixedLengthStreamingMode(jsonData.getBytes().length);
//        conn.setConnectTimeout(1000);
        conn.setConnectTimeout(9000);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(jsonData);
        out.close();


        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }

    public String[] sendPostReqLogin(String strurl, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setFixedLengthStreamingMode(jsonData.getBytes().length);
//        conn.setConnectTimeout(1000);
        conn.setConnectTimeout(9000);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(jsonData);
        out.close();


        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }

    public String[] sendGetReq(String strUrl) throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestProperty("Authorization", "Bearer " + UserDataPreferences.getToken(context));
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.connect();


        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null,
                        HttpCookie.parse(cookie).get(0));
            }
        }

        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }

    public String[] sendPostReq(String strurl)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");


        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }


    public String[] sendPutReq(String strurl, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestProperty("Authorization", "Bearer " + UserDataPreferences.getToken(context));
        conn.setRequestMethod("PUT");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setFixedLengthStreamingMode(jsonData.getBytes().length);
//        conn.setConnectTimeout(1000);
        conn.setConnectTimeout(9000);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(jsonData);
        out.close();
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }


//     To perform an HTTP PUT:

//     URL url = new URL("http://www.example.com/resource");
//     HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//     httpCon.setDoOutput(true);
//     httpCon.setRequestMethod("PUT");
//     OutputStreamWriter out = new OutputStreamWriter(
//     httpCon.getOutputStream());
//     out.write("Resource content");
//     out.close();
//     httpCon.getInputStream();


    // To perform an HTTP DELETE:
    //
    // URL url = new URL("http://www.example.com/resource");
    // HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
    // httpCon.setDoOutput(true);
    // httpCon.setRequestProperty(
    // "Content-Type", "application/x-www-form-urlencoded" );
    // httpCon.setRequestMethod("DELETE");
    // httpCon.connect();

}