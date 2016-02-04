package com.coruscate.centrecourt.Util;

/**
 * Created by cis on 7/25/2015.
 */
public class Constants {

    //Result Code Details
    //1==>Login From MyCart Activity
    //2==>Open AddressListActivity from Delivery Fragment For change address
    //3==>Open AddAddressActivity for add new address
    //4==>Open UpdateProfileActivity from main Activity or Profile Fragment
    //5==>Open PaymentInfoActivity from MiscellaneousActivity
    //6==>Open MiscellaneousActivity or PaymentInfoActivity from MyCart
    //7==>Open MyCart from ItemDetailActivity
    //8==>Open ItemDetailActivity from DetailAdapter
    //8==>Open ItemDetailActivity from WishListAdapter
    //8==>Open ItemDetailActivity from MainActivity
    //9==>Open AddAddressActivity from MiscellaneousActivity
    //9==>Open AddAddressActivity from MyCart
    //10==>Open AddressListActivity from ProfileFragment
    //11==>Open PaymentInfo to WebViewActivity
    //12==>change password

        public static final String api_ip = "http://192.168.0.111:1001";
    public static final String api_v1 = "http://192.168.0.111:1001/api/v1";
//    public static final String api_ip = "http://ccc.democ.in/";
//    public static final String api_v1 = "http://ccc.democ.in/api/v1";

    //    public static final String api_ip = "http://www.thecentrecourtcakes.com";
//    public static final String api_v1 = "http://www.thecentrecourtcakes.com/api/v1";
    public static final String api_get_category = "/categories";
    public static final String api_get_category_item = "/category/";
    public static final String api_send_category_item = "/category/";
    public static final String api_login = "/auth/generate-token";
    public static final String api_forgot_password = "api/v1/send-mail";
    public static final String api_logout = "/auth/logout";
    public static final String api_google_login = "/auth/google";
    public static final String api_facebook_login = "/auth/facebook";
    public static final String api_register = "/auth/user-register";
    public static final String api_update_profile = "/auth/edit-account";
    public static final String api_add_to_cart = "/cart/add-item";
    public static final String api_view_cart = "/cart/view-cart";
    public static final String api_remove_item_cart = "/cart/remove-item";
    public static final String api_update_item_cart = "/cart/update-item";
    public static final String api_order = "/order";
    public static final String api_add_to_wishlist = "/wishlist/add-item";
    public static final String api_remove_to_wishlist = "/wishlist/remove-item";
    public static final String api_view_wishlist = "/wishlist/view-wishlist";
    public static final String api_new_arrivals = "/new-arrivals";
    public static final String api_address_book = "/auth/address-book";
    public static final String api_add_address = "/auth/add-address";
    public static final String api_update_address = "/auth/update-address";
    public static final String api_delete_address = "/auth/delete-address";
    public static final String api_order_history = "/order-history";
    public static final String api_cancel_order = "/cancel-order";
    public static final String api_cancel_order_item = "/cancel-order-item";
    public static final String api_item_accessories = "/item-accessories";
    public static final String api_rating_review = "/rating-review";
    public static final String api_productwise_rating_review = "/rating-review-product";
    public static final String api_apply_coupan = "/apply-coupon";
    public static final String api_setting = "/setting-deliveries";
    public static final String api_Contact = "/contact";
    public static final String api_Change_delivery_date = "/cart/delivery-date-time-change";
    public static final String api_shipping_price = "/shipping-price";
    public static final int WEBVIEW_CODE = 11;
    public static final String api_get_rsa = "/get-rsa-key";
    public static final String api_add_gift_voucher = "/gift-voucher";
    public static final String api_news_letter_un_subscribe = "/un-subscribe";
    public static final String api_news_letter_subscribe = "/subscribe";
    public static final String api_change_password = "/auth/change-password";
    public static final String PARAMETER_SEP = "&";
    public static final String PARAMETER_EQUALS = "=";
    public static final String JSON_URL = "https://secure.ccavenue.com/transaction/transaction.do";
    public static final String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";


}
