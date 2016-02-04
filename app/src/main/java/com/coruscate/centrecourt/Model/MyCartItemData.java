package com.coruscate.centrecourt.Model;

/**
 * Created by cis on 8/28/2015.
 */
public class MyCartItemData {
    String itemId;
    String UserData;
    String OriginalData;
    String UpdateQty;
    String UpdatePrice;


    public MyCartItemData(String itemId, String userData, String originalData, String updateQty, String updatePrice) {
        this.itemId = itemId;
        UserData = userData;
        OriginalData = originalData;
        UpdateQty = updateQty;
        UpdatePrice = updatePrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserData() {
        return UserData;
    }

    public void setUserData(String userData) {
        UserData = userData;
    }

    public String getOriginalData() {
        return OriginalData;
    }

    public void setOriginalData(String originalData) {
        OriginalData = originalData;
    }

    public String getUpdateQty() {
        return UpdateQty;
    }

    public void setUpdateQty(String updateQty) {
        UpdateQty = updateQty;
    }

    public String getUpdatePrice() {
        return UpdatePrice;
    }

    public void setUpdatePrice(String updatePrice) {
        UpdatePrice = updatePrice;
    }
}
