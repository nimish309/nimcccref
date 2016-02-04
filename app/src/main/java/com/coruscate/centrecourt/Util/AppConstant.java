package com.coruscate.centrecourt.Util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coruscate.centrecourt.CustomControls.TypefaceSpan;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.Dashboard.DashBoardCakesFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cis on 7/25/2015.
 */
public class AppConstant {


    public static final String PROFILE_PIC_FILE_NAME = "profilePic.jpg";
    public static final String UPLOAD_ITEM_PIC_FILE_NAME = "uploadItemPic.jpg";
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat stf = new SimpleDateFormat("hh:mm aa");
    public static Stack<DashBoardCakesFragment> mFragmentList = new Stack<>();
    // Progamatic Constant
    public static String rupee_symbol = "\u20B9" + " ";
    public static String dollar_symbol = "\u0024";
    public static String noInternetMsg = "No Internet Connection Found";
    public static String noDataFound = "Opps!!! No Data Found";
    private static View footer;

    public static void saveProfilePic(Context context, InputStream is)
            throws IOException {
        File mFileTemp;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        } else {
            mFileTemp = new File(context.getFilesDir(),
                    AppConstant.PROFILE_PIC_FILE_NAME);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
        copyStream(is, fileOutputStream);
        fileOutputStream.close();
        is.close();

        // // sends broadcast to change profile pic image
        // Intent i = new Intent(SYNC_FINISHED);
        // context.sendBroadcast(i);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static void setToolBarColor(Activity activity)
            throws NoSuchMethodException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusbar));
        }
    }

    /*    public static void setToolBarColor(Activity activity)
                throws NoSuchMethodException {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(new Deprecated(activity).getColor(android.R.color.));

            }
        }*/
    public static void unableConnectServer(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle("Network Error")
                .setMessage("Unable to connect server,\nPlease try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setToolBarColorStandAloneActivity(Activity activity)
            throws NoSuchMethodException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(
                    R.color.statusbar));

        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static SpannableString spanFont(String string, Context context) {
        SpannableString s;
        s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(context, "fonts/AvenirNext-Bold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static void showNetworkError(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle("Network Error")
                .setMessage("Internet connection not found,\nPlease try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();

    }

    public static SpannableString spanFontRegular(String string, Context context) {
        SpannableString s;
        s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(context, "fonts/AvenirNext-Regular.ttf", "regular"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static void showToastLong(Context context, String str) {
        if (str != null) {
            if (str.trim().length() > 0) {
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void showToastShort(Context context, String str) {
        if (str != null) {
            if (str.trim().length() > 0) {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static View getProgressFooter(Activity activity) {
        if (footer == null) {
            try {
                footer = activity.getLayoutInflater().inflate(
                        R.layout.list_footer_progress_bar, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return footer;
    }

    public static int dpToPx(int dp, Context context) {
        return Math
                .round(dp
                        * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void copyJSONArray(JSONArray src, ArrayList<String> dest) {
        if (src != null && dest != null) {
            dest.clear();
            for (int i = 0; i < src.length(); i++) {
                try {
                    dest.add(src.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void bindJSONArray(JSONArray src, ArrayList<String> dest) {
        if (src != null && dest != null) {
            for (int i = 0; i < src.length(); i++) {
                try {
                    dest.add(src.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static void showNetworkError(final Context context) {
//
//        new AlertDialog.Builder(context)
//                .setTitle("Network Error")
//                .setMessage("Internet connection not found,\nPlease try again.")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).setCancelable(true).show();
//
//    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
        return height;
    }

    public static void closeAppPopup(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true).show();

    }

    public static void showSingleButtonAlertDialog(final Context context, String title, String message) {
        if (null != message && message.trim().length() > 0) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(true).show();
        }
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);

        Matcher matcher = pattern.matcher(emailAddress);

        if (!matcher.find()) {
            return false;
        }

        return true;
    }

    public static void showTwoButtonDialog(final Context context, String strTitle, String strMessage, final TwoButtonListener btnListener) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View model = inflater.inflate(R.layout.two_button_dialog, null, false);
        TextView txtTitle = (TextView) model.findViewById(R.id.txtTitle);
        txtTitle.setText(strTitle);

        TextView txtMessage = (TextView) model.findViewById(R.id.txtMessage);
        txtMessage.setText(strMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(model);
        final AlertDialog dialog = builder.create();
        Button btn = (Button) model.findViewById(R.id.button_accept);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != btnListener)
                    btnListener.button1_Click();
                dialog.dismiss();
            }
        });
        Button btn2 = (Button) model.findViewById(R.id.button_cancel);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != btnListener)
                    btnListener.button2_Click();
                dialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setBackground(context.getDrawable(R.drawable.ripple_dialog_btn));
            btn2.setBackground(context.getDrawable(R.drawable.ripple_dialog_btn));
        }
        dialog.show();
    }

    public static void displayErroMessage(View view, String message, Context context) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.labelRed));
        snackbar.show();
    }

    public static void scaleAnimation(ImageView view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.popup_animation));
    }

    public static void scaleAnimationOfView(View view, Context context) {
//        ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        fade_in.setDuration(500);     // animation duration in milliseconds
//        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
//        view.startAnimation(fade_in);
        AlphaAnimation fade_in = new AlphaAnimation(0.5f, 1.0f);
        fade_in.setDuration(400);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        view.startAnimation(fade_in);

    }

    public static void translateAnimation(ImageView view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.translate_animation));
    }

    public static final int getScreenWidth(Activity context) {
        int Measuredwidth = 0;
        WindowManager wManager = context.getWindowManager();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wManager.getDefaultDisplay().getSize(size);

            Measuredwidth = size.x;

        } else {
            Display d = wManager.getDefaultDisplay();
            Measuredwidth = d.getWidth();

        }
        return Measuredwidth;

    }

    public static final int getScreenHeight(Activity context) {

        int Measuredheight = 0;
        Point size = new Point();
        WindowManager wManager = context.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wManager.getDefaultDisplay().getSize(size);
            Measuredheight = size.y;
        } else {
            Display d = wManager.getDefaultDisplay();
            Measuredheight = d.getHeight();
        }
        return Measuredheight;
    }

    /* public static void collapse(final View mLinearLayout) {

         AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
         animation1.setDuration(500);
         mLinearLayout.startAnimation(animation1);


         mLinearLayout.setVisibility(View.GONE);
 //        int finalHeight = mLinearLayout.getHeight();
 //
 //        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, mLinearLayout);
 //
 //        mAnimator.addListener(new Animator.AnimatorListener() {
 //            @Override
 //            public void onAnimationStart(Animator animation) {
 //
 //            }
 //
 //            @Override
 //            public void onAnimationEnd(Animator animator) {
 //                //Height=0, but it set visibility to GONE
 //                mLinearLayout.setVisibility(View.GONE);
 //            }
 //
 //            @Override
 //            public void onAnimationCancel(Animator animation) {
 //
 //            }
 //
 //            @Override
 //            public void onAnimationRepeat(Animator animation) {
 //
 //            }
 //        });
 //
 //        mAnimator.start();
     }

     public static void expand(final View mLinearLayout) {
         //set Visible

         AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
         animation1.setDuration(500);
         mLinearLayout.startAnimation(animation1);


         mLinearLayout.setVisibility(View.VISIBLE);


         //        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
         //        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
         //        mLinearLayout.measure(widthSpec, heightSpec);
         //
         //        ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight(), mLinearLayout);
         //        mAnimator.start();
     }

     private static ValueAnimator slideAnimator(int start, int end, final View mLinearLayout) {

         ValueAnimator animator = ValueAnimator.ofInt(start, end);

         animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
             @Override
             public void onAnimationUpdate(ValueAnimator valueAnimator) {
                 //Update Height
                 int value = (Integer) valueAnimator.getAnimatedValue();
                 ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                 layoutParams.height = value;
                 mLinearLayout.setLayoutParams(layoutParams);
             }
         });
         return animator;
     }
 */
    public static void collapse(final LinearLayout mLinearLayout) {
        int finalHeight = mLinearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, mLinearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator.start();
    }

    public static void expand(final LinearLayout mLinearLayout) {
        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight(), mLinearLayout);
        mAnimator.start();
    }

    private static ValueAnimator slideAnimator(int start, int end, final LinearLayout mLinearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static String getTimeDiff(String from_time) {
        String time = "";
        final String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DateFormat formatter = new SimpleDateFormat(OLD_FORMAT);
        long diffHours = 0;
        long diffMinutes = 0;
        long diffDays = 0;
        try {
            Date d1 = formatter.parse(from_time);
            long diff = Calendar.getInstance().getTimeInMillis() - d1.getTime() - TimeZone.getDefault().getRawOffset();
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (diffDays == 0) {
            if (diffHours < 1) {
                time = String.valueOf(diffMinutes) + " minutes ago";
            } else {
                if (diffHours > 1 || diffHours < 24) {
                    time = String.valueOf(diffHours) + " hours ago";
                }
            }
        } else {
            time = String.valueOf(diffDays) + " days ago";
        }
        return time;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        SimpleDateFormat month_date = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
        String date = month_date.format(cal.getTime()).toString();
        return date;
    }


    public static String getCurrentDate(long time, int availabilityHrs) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + availabilityHrs);

        SimpleDateFormat month_date = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
        String date = month_date.format(cal.getTime()).toString();
        return date;
    }


    public static String getNextDate(long time, int availabilityHrs, int storeOpen) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, storeOpen + availabilityHrs);
        Log.d("open", storeOpen + "--" + availabilityHrs);
        SimpleDateFormat month_date = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
        String date = month_date.format(cal.getTime()).toString();
        return date;
    }

    public static String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        SimpleDateFormat month_date = new SimpleDateFormat("hh:mm a");
        String date = month_date.format(cal.getTime()).toString();
        return date;
    }

    public static int getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static String getCurrentMinutes() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String strMin = "";
        int min = cal.get(Calendar.MINUTE);
        if (min < 10)
            strMin = "0" + min;
        else
            strMin = String.valueOf(min);

        return strMin;
    }

    public static int getHour(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);

        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static String getMinutes(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String strMin = "";
        int min = cal.get(Calendar.MINUTE);
        if (min < 10)
            strMin = "0" + min;
        else
            strMin = String.valueOf(min);

        return strMin;
    }

    public static long getCurrentTimeStamps() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();

    }

    public static long getTimeStamp(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        Calendar k = Calendar.getInstance();


        c.set(k.get(Calendar.YEAR), k.get(Calendar.MONTH), k.get(Calendar.DAY_OF_MONTH));


        return c.getTimeInMillis();
    }

    public static void setTextViewTypeFace(Context context, TextView tv) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/AvenirNext-Regular.ttf");
        tv.setTypeface(typeface);
    }


}
