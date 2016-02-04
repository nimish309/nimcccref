package com.coruscate.centrecourt.AsynkTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.ItemDetailActivity;
import com.coruscate.centrecourt.UserInterface.Activity.MyCart;
import com.coruscate.centrecourt.UserInterface.Fragments.DetailFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.MyWishListFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by cis on 9/1/2015.
 */
public class AddToWishListTask extends AsyncTask<Void, Void, Void> {

    private JSONObject jObj;
    private boolean flag = false;
    private int position;
    private String itemId;
    private int responseCode;
    private Activity context;
    private String id, message, type;
    private ImageView imageView;
    private String[] data;
    private boolean isInWishList;
    private CustomProgressDialog dialog;
    private MyCart myCart;

    public AddToWishListTask(ImageView imageView, String id, Activity context, boolean isInWishList, String type, int position, MyCart myCart) {
        this.imageView = imageView;
        this.id = id;
        this.context = context;
        this.isInWishList = isInWishList;
        this.type = type;
        this.position = position;
        dialog = CustomProgressDialog.createProgressBar(context);
        this.myCart = myCart;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (context instanceof MyCart) {
            dialog.setCancelable(false);
            dialog.show();
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONParser jsonParser = new JSONParser(context);
            JSONStringer jsonData = new JSONStringer().object()
                    .key("item_id").value(id).endObject();

            if (!isInWishList) {
                data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_add_to_wishlist, jsonData.toString());
            } else {
                data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_remove_to_wishlist, jsonData.toString());
            }
            responseCode = Integer.valueOf(data[0]);
            if (responseCode == 200) {
                jObj = new JSONObject(data[1]);
                flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                message = jObj.has("message") ? (jObj.isNull("message") ? "" : jObj.getString("message")) : "";
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            imageView.setClickable(true);
            if (responseCode == 200) {
                if (flag) {
                    if (context instanceof MyCart) {
                        myCart.afterRemoveCart(position);
                        AppConstant.showToastShort(context, message);
                    } else {
                        isInWishList = !isInWishList;
                        try {
                            ArrayList<String> arrayList;
                            arrayList = UserDataPreferences.getCategoryItemList(context);
                            JSONObject jobject;
                            if (arrayList != null) {
                                if (!type.equals("WishList")) {
                                    jobject = new JSONObject(arrayList.get(position).toString());
                                    if (isInWishList) {
                                        try {
                                            ItemDetailActivity.isInWishList = true;
                                            jobject.put("is_in_wishlist", true);
                                        } catch (Exception e) {

                                        }
                                    } else {
                                        try {

                                            ItemDetailActivity.isInWishList = false;
                                            jobject.put("is_in_wishlist", false);
                                        } catch (Exception e) {

                                        }
                                    }
                                    arrayList.set(position, jobject.toString());
                                    UserDataPreferences.saveCategoryItemList(context, arrayList);
                                    DetailFragment.detailList.set(position, jobject.toString());
                                    DetailFragment.notifyAdapter(position);

                                } else {
                                    if (isInWishList) {
                                        try {
                                            JSONObject object = jObj.getJSONObject("data");
                                            JSONObject object1 = object.getJSONObject("itms");
                                            JSONObject object2 = object1.getJSONObject("item_details");
                                            MyWishListFragment.detailList.add(position, object2.toString());
                                            MyWishListFragment.mBookends.notifyDataSetChanged();
                                            ItemDetailActivity.isInWishList = true;
                                        } catch (Exception e) {

                                        }
                                    } else {
                                        try {
                                            MyWishListFragment.removeItem(id);
                                            ItemDetailActivity.isInWishList = false;
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppConstant.showToastShort(context, message);
                    }
                } else {
                    if (isInWishList) {
                        imageView.setImageResource(R.drawable.icon_heart_2);
                    } else {
                        imageView.setImageResource(R.drawable.icon_heart_1);
                    }
                    AppConstant.showToastShort(context, message);
                }
            } else {
                if (isInWishList) {
                    imageView.setImageResource(R.drawable.icon_heart_2);
                } else {
                    imageView.setImageResource(R.drawable.icon_heart_1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
