package com.coruscate.centrecourt.Util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by User on 8/19/2015.
 */
public class JSONData {

    public static String getStringDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.getString(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? "" : item.getString(key))
                    : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean getBoolean(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? false : item.getBoolean(key))
                    : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getBooleanDefTrue(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? true : item.getBoolean(key))
                    : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getInt(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0 : item.getInt(key))
                    : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLong(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0 : item.getLong(key))
                    : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Double getDouble(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0.0 : item.getDouble(key))
                    : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static Double getDoubleDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.getDouble(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? new JSONObject() : item.getJSONObject(key))
                    : new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject getJSONObjectDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.getJSONObject(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getJSONArray(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? new JSONArray() : item.getJSONArray(key))
                    : new JSONArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONArray getJSONArrayDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.getJSONArray(key))
                    : null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getObject(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? new Object() : item.get(key))
                    : new Object();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object();
    }

    public static Object getObjectDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.get(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
