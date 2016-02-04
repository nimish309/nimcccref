package com.coruscate.centrecourt.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by chiruit.
 */
public class UserDataPreferences {

    private static final String IS_FIRST_TIME_PREF = "isFirstTimePref";
    private static final String IS_FIRST_TIME_DATA = "isFirstTimeData";
    public static String LAST_FRAG_PREF = "lastFragPref";
    public static String LAST_FRAG_NAME = "lastFragName";
    public static String CATEGORY_ITEM_PREF = "CategoryItemPref";
    public static String CART_ITEM_COUNT_PREF = "CartItemPref";
    public static String ACCESSORIES_ITEM_PREF = "AccessoriesItemPref";
    public static String SETTING_PREF = "SettingPref";
    private static String MISCELLANEOUS_PREF = "MiscellaneousPref";
    private static String CATEGORY_PREF = "CategoryPref";
    private static String TOKEN_PREF = "TokenPref";
    private static String TOKEN_DATA = "TokenData";
    private static String USER_INFO_PREF = "UserInfoPref";
    private static String CATEGORY_DATA = "categoryData";
    private static String MISCELLANEOUS_DATA = "miscellaneousData";
    private static String ITEM_CATEGORY_PREF = "itemCatagoryPref";
    private static String ITEM_CATEGORY_DATA = "itemCatagoryData";
    private static String USER_SHIPPING_DETAIL = "userShippingDetail";
    private static String SHIPPING_INFO = "shippingInfo";
    private static String BILLING_INFO = "billingInfo";
    private static String CATEGORY_ITEM_LIST = "CategoryItemList";
    private static String CART_ITEM_COUNT = "CartItemcount";
    private static String RESULT_CODE_PREFERENCES = "ResultCodePreference";

    public static void saveLastFrag(Context context, String strName) {
        context.getSharedPreferences(
                LAST_FRAG_PREF, Context.MODE_PRIVATE).edit().putString(LAST_FRAG_NAME, strName).commit();
    }


    public static String getLastFragname(Context context) {
        return context.getSharedPreferences(LAST_FRAG_PREF,
                Context.MODE_PRIVATE).getString(LAST_FRAG_NAME, "");

    }


    public static void saveMiscellaneousInfo(Context context, String str) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(MISCELLANEOUS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit().clear();
        editor.putString(MISCELLANEOUS_DATA, str);
        editor.commit();
    }

    public static void saveCategoryInfo(Context context, String str) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(CATEGORY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit().clear();
        editor.putString(CATEGORY_DATA, str);
        editor.commit();
    }

    public static void saveSettingDetail(Context context, String str) {
        context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE).edit().putString("settingInfo", str).commit();
    }

    public static String getSettingDetail(Context context) {
        return context.getSharedPreferences(SETTING_PREF, context.MODE_PRIVATE).getString("settingInfo", "");
    }

    public static void saveToken(Context context, String str) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(USER_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit();
        editor.putString(TOKEN_DATA, str);
        editor.commit();
    }

    public static void saveIsFirstTime(Context context, boolean b) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(IS_FIRST_TIME_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_DATA, b);
        editor.commit();
    }


    public static void saveUserInfo(Context context, String str) {
        context.getSharedPreferences(USER_INFO_PREF, Context.MODE_PRIVATE).edit().putString("UserInfoData", str).commit();
    }

    public static void removeUserInfo(Context context) {
        context.getSharedPreferences(USER_INFO_PREF, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static String getUserInfo(Context context) {
        return context.getSharedPreferences(USER_INFO_PREF, context.MODE_PRIVATE).getString("UserInfoData", "");
    }
    public static void saveLoginDate(Context context, long loginDate) {
        context.getSharedPreferences(
                USER_INFO_PREF, Context.MODE_PRIVATE).edit().putLong("loginDate", loginDate).commit();
    }

    public static long getLoginDate(Context context) {
        return context.getSharedPreferences(USER_INFO_PREF, context.MODE_PRIVATE).getLong("loginDate", 0);
    }

    public static JSONObject getEggPrice(Context context){
        try {
            String settingInfo = getSettingDetail(context);
            if (settingInfo.length() > 0) {
                JSONObject data = new JSONObject(settingInfo);
                if (data == null) {
                    return null;
                } else {
                    JSONObject object = data.getJSONObject("setting");
                    return JSONData.getJSONObject(object, "egg_price");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getDeliveryInfo(Context context) {
        try {
            String settingInfo = getSettingDetail(context);
            if (settingInfo.length() > 0) {
                JSONObject data = new JSONObject(settingInfo);
                if (data == null) {
                    return null;
                } else {
                    JSONArray array = data.getJSONArray("deliveries");
                    if (array.length() > 0) {
                        return array;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getFlavourInfo(Context context) {
        try {
            String settingInfo = getSettingDetail(context);
            if (settingInfo.length() > 0) {
                JSONObject data = new JSONObject(settingInfo);
                if (data == null) {
                    return null;
                } else {
                    JSONArray array = data.getJSONArray("flavours");
                    if (array.length() > 0) {
                        return array;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getUserAddressBook(Context context) {
        try {
            String userInfo = getUserInfo(context);
            if (userInfo.length() > 0) {
                JSONObject data = new JSONObject(userInfo);
                if (data == null) {
                    return null;
                } else {
                    return JSONData.getJSONArrayDefNull(data, "address_book");
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getItemCategory(Context context) {
        return context.getSharedPreferences(ITEM_CATEGORY_PREF, context.MODE_PRIVATE).getString(ITEM_CATEGORY_DATA, null);
    }


    public static String getCategoryInfo(Context context) {
        return context.getSharedPreferences(CATEGORY_PREF, context.MODE_PRIVATE).getString(CATEGORY_DATA, null);
    }

    public static String getMiscellaneousInfo(Context context) {
        return context.getSharedPreferences(MISCELLANEOUS_PREF, context.MODE_PRIVATE).getString(MISCELLANEOUS_DATA, null);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(USER_INFO_PREF, context.MODE_PRIVATE).getString(TOKEN_DATA, "");
    }

    public static boolean isFirstTime(Context context) {
        return context.getSharedPreferences(IS_FIRST_TIME_PREF, context.MODE_PRIVATE).getBoolean(IS_FIRST_TIME_DATA,false);
    }

    public static String getUserId(Context context) {
        try {
            JSONObject jsonObject = new JSONObject(getUserInfo(context));
            return jsonObject.has("_id") ? (jsonObject.isNull("_id") ? "" : jsonObject.getString("_id")) : "";
        } catch (Exception e) {

        }
        return null;
    }

    public static void saveUserShippingDetail(Context context, String shippingDetail, String billingDetail) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(USER_SHIPPING_DETAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit().clear();
        editor.putString(SHIPPING_INFO, shippingDetail);
        editor.putString(BILLING_INFO, billingDetail);
        editor.commit();
    }

    public static String getUserShippingDetail(Context context) {
        return context.getSharedPreferences(USER_SHIPPING_DETAIL, context.MODE_PRIVATE).getString(SHIPPING_INFO, "");
    }

    public static String getUserBillingDetail(Context context) {
        return context.getSharedPreferences(USER_SHIPPING_DETAIL, context.MODE_PRIVATE).getString(BILLING_INFO, "");
    }

    public static void saveCategoryItemList(Context context, List<String> notificationListItems) {
        Gson gson = new Gson();
        String jsonCategoryItem = gson.toJson(notificationListItems);
        SharedPreferences editSharedPreferences = context.getSharedPreferences(
                CATEGORY_ITEM_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit();
        editor.putString(CATEGORY_ITEM_LIST, jsonCategoryItem);
        editor.commit();
    }

    public static ArrayList<String> getCategoryItemList(Context context) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(
                CATEGORY_ITEM_PREF, Context.MODE_PRIVATE);

        List<String> item;
        if (editSharedPreferences.contains(CATEGORY_ITEM_LIST)) {
            String jsonData = editSharedPreferences.getString(CATEGORY_ITEM_LIST, null);
            Gson gson = new Gson();
            String[] categoryItems = gson.fromJson(jsonData, String[].class);
            item = Arrays.asList(categoryItems);
            item = new ArrayList<>(item);
        } else
            return null;
        return (ArrayList<String>) item;
    }

    public static void saveCartCount(Context context, int count) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(USER_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit();
        editor.putInt(CART_ITEM_COUNT, count);
        editor.commit();
    }

    public static int getCartCount(Context context) {
        return context.getSharedPreferences(USER_INFO_PREF, context.MODE_PRIVATE).getInt(CART_ITEM_COUNT, 0);
    }

    public static void saveResultCode(Context context, int Code) {
        context.getSharedPreferences(
                RESULT_CODE_PREFERENCES, Context.MODE_PRIVATE).edit().clear().putInt("resultCode", Code).commit();
    }

    public static int getResultCode(Context context) {
        return context.getSharedPreferences(
                RESULT_CODE_PREFERENCES, Context.MODE_PRIVATE).getInt("resultCode", 0);
    }

    public static void saveAccessoriesItemList(Context context, ArrayList<String> accessoriesListItems) {
        Gson gson = new Gson();
        String jsonCategoryItem = gson.toJson(accessoriesListItems);
        SharedPreferences editSharedPreferences = context.getSharedPreferences(
                ACCESSORIES_ITEM_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = editSharedPreferences.edit();
        editor.putString("AccessoriesItemList", jsonCategoryItem);
        editor.commit();
    }

    public static ArrayList<String> getAccessoriesItemList(Context context) {
        SharedPreferences editSharedPreferences = context.getSharedPreferences(
                ACCESSORIES_ITEM_PREF, Context.MODE_PRIVATE);
        List<String> item;
        if (editSharedPreferences.contains("AccessoriesItemList")) {
            String jsonData = editSharedPreferences.getString("AccessoriesItemList", null);
            Gson gson = new Gson();
            String[] accessoriesItems = gson.fromJson(jsonData, String[].class);
            item = Arrays.asList(accessoriesItems);
            item = new ArrayList<String>(item);
        } else
            return null;
        return (ArrayList<String>) item;
    }

}
