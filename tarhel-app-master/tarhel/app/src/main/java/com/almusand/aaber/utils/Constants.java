package com.almusand.aaber.utils;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class Constants {

    public static final int SELECT_IMAGE = 120;
    public static final int INTENT_QR_CODE = 110;
    public static final int SELECT_PERSONALID = 104;
    public static final int PICK_Camera_IMAGE = 100;
    public static final int PICKUP_LOCATION = 102;
    public static final int PICKOFF_LOCATION = 105;

    public static final String MAPVIEW_BUNDLE_KEY = "AIzaSyASQqlYhEnTRS0-vB_8s1F5IsQLI6YR7fw";
    public static final String Routing_KEY = "AIzaSyC2uAoVMEsYp2MkrW15NCCyIsS1Oe-Rycw";

    public static final  String payToken="sk_test_O1ap7Sml2yZnYbrtHgevNqGK";
    public static final  String PackageId="com.almusand.aaber";

    public static final String baseUrl = "https://backend-app.tarhel.com/api/";
    public static final String RegisterUrl = baseUrl.concat("register");
    public static final String CategoriesUrl = baseUrl.concat("categories");
    public static final String LoginUrl = baseUrl.concat("login");
    public static final String UpdateProfileUrl = baseUrl.concat("user/update");
    public static final String OrderrUrl = baseUrl.concat("orders");
    public static final String ContactUsUrl = baseUrl.concat("contacts");
    public static final String AcceptOfferrUrl = baseUrl.concat("accept-offer-from-provider");

    public static final String UserUrl = baseUrl.concat("user");
    public static final String OrdersProviderrUrl = baseUrl.concat("orders/nearby");
    public static final String myOrdersrUrl = baseUrl.concat("orders");
    public static final String cancelorderUrl = baseUrl.concat("orders/canceled");
    public static final String deliverorderUrl = baseUrl.concat("orders/delivered");
    public static final String OffersrUrl = baseUrl.concat("offers?page=");
    public static final String sendOfferUrl = baseUrl.concat("offers");
    public static final String acceptOfferUrl = baseUrl.concat("accept-offer-from-provider");
    public static final String conversationsUrl = baseUrl.concat("andriod/conversations");
    public static final String SendMessageUrl = baseUrl.concat("messages");
    public static final String UpdateTokenUrl = baseUrl.concat("user/update/device_token");

    public static final String userNotificationsUrl = baseUrl.concat("user/notifications");

    public static final String RateUserUrl = baseUrl.concat("rate");
    public static final String ComplaintsUrl = baseUrl.concat("complaints");
    public static final String MyComplaintsUrl = baseUrl.concat("complaints?page=");

    public static final String BalancesUrl = baseUrl.concat("balances");
    public static final String PayFromBalanceUrl = baseUrl.concat("balances/pay-from-balance");
    public static final String ReedemPointsUrl = baseUrl.concat("user/reedem-points");
    public static final String subscriptionsUrl = baseUrl.concat("subscriptions");

    public static final String SettingUrl = baseUrl.concat("settings");

    public static final String ForgetPassUrl = baseUrl.concat("user/generate-newPassword");
}
