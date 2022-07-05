package com.almusand.aaber.ui.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.chat.ChatActivity;
import com.almusand.aaber.ui.myOffer.MyOffersViewModel;
import com.almusand.aaber.ui.notification.NotificationActivity;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.ui.orderOffers.OrderOfferViewModel;
import com.almusand.aaber.ui.orderOffers.OrderOffersActivity;
import com.almusand.aaber.ui.sendOffer.SendOfferViewModel;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.almusand.aaber.utils.SettingsManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import company.tap.gosellapi.GoSellSDK;
import company.tap.gosellapi.internal.api.callbacks.GoSellError;
import company.tap.gosellapi.internal.api.models.Authorize;
import company.tap.gosellapi.internal.api.models.Charge;
import company.tap.gosellapi.internal.api.models.PhoneNumber;
import company.tap.gosellapi.internal.api.models.Token;
import company.tap.gosellapi.open.controllers.SDKSession;
import company.tap.gosellapi.open.controllers.ThemeObject;
import company.tap.gosellapi.open.delegate.SessionDelegate;
import company.tap.gosellapi.open.enums.AppearanceMode;
import company.tap.gosellapi.open.enums.TransactionMode;
import company.tap.gosellapi.open.models.CardsList;
import company.tap.gosellapi.open.models.Customer;
import company.tap.gosellapi.open.models.PaymentItem;
import company.tap.gosellapi.open.models.TapCurrency;
import company.tap.gosellapi.open.models.Tax;
import es.dmoral.toasty.Toasty;

public class PaymentActivity extends BaseActivity implements View.OnClickListener, SessionDelegate {

    private View view;
    private ImageView imgBack;
    private RoundedImageView imgStore;
    private TextView tvOrderDetails;
    private TextView tvOrderAddress;
    private TextView tvOrderCost;
    private RadioGroup radioGroup;
    private AppCompatRadioButton rbCashOnDelivery;
    private AppCompatRadioButton rbCreditCard;
    private ProgressButton progressButton;

    private String paymentType;
    private int totalCost = 0;

    private Offer offer;
    private Order order;

    private PaymentViewModel viewModel;
    private HashMap<String, String> map;

    private SDKSession sdkSession;
    private SettingsManager settingsManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        settingsManager = SettingsManager.getInstance();
        settingsManager.setPref(this);

        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        offer = (Offer) getIntent().getExtras().get("offer");
        order = (Order) getIntent().getExtras().get("order");
        user = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        initialViews();

        getSingleOffer(offer.getId());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {

                    case R.id.rb_cash_on_delivery:
                        paymentType = "cash";
                        break;


                    case R.id.rb_credit_card:
                        paymentType = "online";
                        break;

                }

            }
        });

    }

    private void initialViews() {
        imgBack = findViewById(R.id.img_back);
        imgStore = findViewById(R.id.img_store);
        tvOrderDetails = findViewById(R.id.tv_order_details);
        tvOrderAddress = findViewById(R.id.tv_order_address);
        tvOrderCost = findViewById(R.id.tv_order_cost);
        radioGroup = findViewById(R.id.radioGroup);
        rbCashOnDelivery = findViewById(R.id.rb_cash_on_delivery);
        rbCreditCard = findViewById(R.id.rb_credit_card);

        view = findViewById(R.id.bt_payment);
        progressButton = new ProgressButton(PaymentActivity.this, view);
        progressButton.setButtonTittle(PaymentActivity.this.getResources().getString(R.string.processed_payment));

        tvOrderAddress.append(" " + order.getLocation());
        tvOrderDetails.setText(order.getNote());
        tvOrderCost.append(" " + totalCost + " " + getString(R.string.sar));

        if (order.getImage() != null)
            Glide.with(PaymentActivity.this)
                    .load(order.getImage())
                    .error(R.drawable.aaber_logo)
                    .into(imgStore);

        view.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_payment:
                if (paymentType != null)
                    switch (paymentType) {

                        case "cash":
                            payWithCashOrOnline("cash", null);
                            break;

                        case "online":
                            view.setEnabled(false);
                            progressButton.buttonActivated();
                            startSDK();

                            break;
                    }
                break;

            case R.id.img_back:
                finish();
                break;

        }

    }

    private void getSingleOffer(int offerId) {
        MyOffersViewModel viewModel = new ViewModelProvider(this).get(MyOffersViewModel.class);
        viewModel.getOfferDetails(PaymentActivity.this, offerId);
        viewModel.mutableLiveDataOffer.observe(this, new Observer<Offer>() {
            @Override
            public void onChanged(Offer offer) {
                tvOrderCost.append(offer.getPrice() + " " + getString(R.string.sar));
            }
        });
    }


    private void payWithCashOrOnline(String type, String paymentId) {
        view.setEnabled(false);
        progressButton.buttonActivated();

        map = new HashMap<>();
        map.put("payment_method", type);
        map.put("order_id", String.valueOf(order.getId()));
        map.put("offer_id", String.valueOf(offer.getId()));
        map.put("delivery_cost", String.valueOf(offer.getPrice()));
        map.put("extra_cost", "0");
        if (type.equals("online")) {
            map.put("payment_id", paymentId);
            map.put("status", "pending");
        } else {
            map.put("status", "cash");
        }

        viewModel.payOnlineOrCash(this, map);
        viewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("success")) {
                    Intent returnIntent = new Intent();
                    switch (paymentType) {

                        case "cash":
                            if (LocaleManager.getLocale(PaymentActivity.this.getResources()).getLanguage().equals("ar")) {
                                returnIntent.putExtra("paymentType", "الدفع عند الاستلام");

                            } else {
                                returnIntent.putExtra("paymentType", paymentType);
                            }
                            break;

                        case "online":
                            if (LocaleManager.getLocale(PaymentActivity.this.getResources()).getLanguage().equals("ar")) {
                                returnIntent.putExtra("paymentType", " عن طريق الفيزا");

                            } else {
                                returnIntent.putExtra("paymentType", paymentType);
                            }
                            break;

                    }
                    setResult(Activity.RESULT_OK, returnIntent);

//                    startActivity(new Intent(PaymentActivity.this, MainActivityClient.class)
//                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    view.setEnabled(true);
                    progressButton.buttonDactivated();
                }

            }
        });

    }

    private void configureApp() {
        GoSellSDK.init(this, Constants.payToken, Constants.PackageId);  // to be replaced by merchant
        GoSellSDK.setLocale("en");//  language to be set by merchant
    }

    private void startSDK() {
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        configureApp();

        /**
         * Optional step
         * Here you can configure your app theme (Look and Feel).
         */
        configureSDKThemeObject();

        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession();

        /**
         * Required step.
         * Choose between different SDK modes
         */
        configureSDKMode();

        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
//        initPayButton();

        listSavedCards();
    }

    private void configureSDKThemeObject() {

        ThemeObject.getInstance()

                // set Appearance mode [Full Screen Mode - Windowed Mode]
                .setAppearanceMode(AppearanceMode.WINDOWED_MODE) // **Required**

                .setSdkLanguage(LocaleManager.getLocale(PaymentActivity.this.getResources()).getLanguage()) //if you dont pass locale then default locale EN will be used

                // Setup header font type face **Make sure that you already have asset folder with required fonts**
//                .setHeaderFont(Typeface.createFromAsset(getAssets(), "font/tajawal_medium.ttf"))//**Optional**

                //Setup header text color
                .setHeaderTextColor(getResources().getColor(R.color.black1))  // **Optional**

                // Setup header text size
                .setHeaderTextSize(17) // **Optional**

                // setup header background
                .setHeaderBackgroundColor(getResources().getColor(R.color.french_gray_new))//**Optional**

                // setup card form input font type
//                .setCardInputFont(Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf"))//**Optional**

                // setup card input field text color
                .setCardInputTextColor(getResources().getColor(R.color.black))//**Optional**

                // setup card input field text color in case of invalid input
                .setCardInputInvalidTextColor(getResources().getColor(R.color.red))//**Optional**

                // setup card input hint text color
                .setCardInputPlaceholderTextColor(getResources().getColor(R.color.black))//**Optional**

                // setup Switch button Thumb Tint Color in case of Off State
                .setSaveCardSwitchOffThumbTint(getResources().getColor(R.color.gray)) // **Optional**

                // setup Switch button Thumb Tint Color in case of On State
                .setSaveCardSwitchOnThumbTint(getResources().getColor(R.color.vibrant_green)) // **Optional**

                // setup Switch button Track Tint Color in case of Off State
                .setSaveCardSwitchOffTrackTint(getResources().getColor(R.color.gray)) // **Optional**

                // setup Switch button Track Tint Color in case of On State
                .setSaveCardSwitchOnTrackTint(getResources().getColor(R.color.green)) // **Optional**

                // change scan icon
                .setScanIconDrawable(getResources().getDrawable(R.drawable.btn_card_scanner_normal)) // **Optional**

                // setup pay button selector [ background - round corner ]
                .setPayButtonResourceId(R.drawable.btn_pay_selector)

                // setup pay button font type face
//                .setPayButtonFont(Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf")) // **Optional**

                // setup pay button disable title color
                .setPayButtonDisabledTitleColor(getResources().getColor(R.color.black)) // **Optional**

                // setup pay button enable title color
                .setPayButtonEnabledTitleColor(getResources().getColor(R.color.white)) // **Optional**

                //setup pay button text size
                .setPayButtonTextSize(14) // **Optional**

                // show/hide pay button loader
                .setPayButtonLoaderVisible(true) // **Optional**

                // show/hide pay button security icon
                .setPayButtonSecurityIconVisible(true) // **Optional**

                // setup dialog textcolor and textsize
                .setDialogTextColor(getResources().getColor(R.color.black1))     // **Optional**
                .setDialogTextSize(17)                // **Optional**
        ;

    }

    private void configureSDKSession() {

        // Instantiate SDK Session
        if (sdkSession == null) sdkSession = new SDKSession();   //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this);    //** Required **

        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource();    //** Required **

        // set transaction currency associated to your account
        sdkSession.setTransactionCurrency(new TapCurrency("SAR"));    //** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession.setCustomer(getCustomer());    //** Required **

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(new BigDecimal(totalCost));  //** Required **

        // Set Payment Items array list
        sdkSession.setPaymentItems(new ArrayList<PaymentItem>());// ** Optional ** you can pass empty array list

        // Set Taxes array list
        sdkSession.setTaxes(new ArrayList<Tax>());// ** Optional ** you can pass empty array list

        // Set Shipping array list
        sdkSession.setShipping(new ArrayList<>());// ** Optional ** you can pass empty array list

        // Post URL
        sdkSession.setPostURL(""); // ** Optional **

        // Payment Description
        sdkSession.setPaymentDescription(""); //** Optional **

        // Payment Extra Info
        sdkSession.setPaymentMetadata(new HashMap<>());// ** Optional ** you can pass empty array hash map

        // Payment Reference
        sdkSession.setPaymentReference(null); // ** Optional ** you can pass null

        // Payment Statement Descriptor
        sdkSession.setPaymentStatementDescriptor(""); // ** Optional **

        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true); //  ** Required ** you can pass boolean

        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true);

        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(null); // ** Optional ** you can pass Receipt object or null

        // Set Authorize Action
        sdkSession.setAuthorizeAction(null); // ** Optional ** you can pass AuthorizeAction object or null

        sdkSession.setDestination(null); // ** Optional ** you can pass Destinations object or null

        sdkSession.setMerchantID(null); // ** Optional ** you can pass merchant id or null

        sdkSession.setPaymentType("CARD");   //** Merchant can customize payment options [WEB/CARD] for each transaction or it will show all payment options granted to him.

        sdkSession.setDefaultCardHolderName("Moraad TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.

        /**
         * Use this method where ever you want to show TAP SDK Main Screen.
         * This method must be called after you configured SDK as above
         * This method will be used in case of you are not using TAP PayButton in your activity.
         */
        sdkSession.start(this);
    }

    /**
     * Configure SDK Theme
     */
    private void configureSDKMode() {

        /**
         * You have to choose only one Mode of the following modes:
         * Note:-
         *      - In case of using PayButton, then don't call sdkSession.start(this); because the SDK will start when user clicks the tap pay button.
         */
        //////////////////////////////////////////////////////    SDK with UI //////////////////////
        /**
         * 1- Start using  SDK features through SDK main activity (With Tap CARD FORM)
         */
        startSDKWithUI();

    }

    /**
     * Start using  SDK features through SDK main activity
     */
    private void startSDKWithUI() {
        if (sdkSession != null) {
            TransactionMode trx_mode = (settingsManager != null) ? settingsManager.getTransactionsMode("key_sdk_transaction_mode") : TransactionMode.PURCHASE;
            // set transaction mode [TransactionMode.PURCHASE - TransactionMode.AUTHORIZE_CAPTURE - TransactionMode.SAVE_CARD - TransactionMode.TOKENIZE_CARD ]
            sdkSession.setTransactionMode(trx_mode);    //** Required **
            // if you are not using tap button then start SDK using the following call
            //sdkSession.start(this);
        }
    }

    /**
     * retrieve list of saved cards from the backend.
     */
    private void listSavedCards() {
        if (sdkSession != null)
            sdkSession.listAllCards("cus_s4H13120191115x0R12606480", this);
    }

    private Customer getCustomer() { // test customer id cus_Kh1b4220191939i1KP2506448

        Customer customer = (settingsManager != null) ? settingsManager.getCustomer() : null;

        PhoneNumber phoneNumber = customer != null ? customer.getPhone() : new PhoneNumber("9665", user.getPhone());

        return new Customer.CustomerBuilder("").email(user.getEmail()).firstName(user.getUserName())
                .lastName("").metadata("").phone(new PhoneNumber(phoneNumber.getCountryCode(), phoneNumber.getNumber()))
                .middleName("").build();
    }

    @Override
    public void paymentSucceed(@NonNull Charge charge) {
        payWithCashOrOnline("online", charge.getId());
    }

    @Override
    public void paymentFailed(@Nullable Charge charge) {
        Toasty.error(this, PaymentActivity.this.getResources().getString(R.string.msg_payment_failed), Toasty.LENGTH_SHORT).show();
    }

    @Override
    public void authorizationSucceed(@NonNull Authorize authorize) {

    }

    @Override
    public void authorizationFailed(Authorize authorize) {
        Toasty.error(this, "Authorization Failed", Toasty.LENGTH_SHORT).show();

    }

    @Override
    public void cardSaved(@NonNull Charge charge) {

    }

    @Override
    public void cardSavingFailed(@NonNull Charge charge) {
        Toasty.error(this, "Card Saving Failed", Toasty.LENGTH_SHORT).show();

    }

    @Override
    public void cardTokenizedSuccessfully(@NonNull Token token) {

    }

    @Override
    public void savedCardsList(@NonNull CardsList cardsList) {

    }

    @Override
    public void sdkError(@Nullable GoSellError goSellError) {

    }

    @Override
    public void sessionIsStarting() {

    }

    @Override
    public void sessionHasStarted() {

    }

    @Override
    public void sessionCancelled() {
        view.setEnabled(true);
        progressButton.buttonDactivated();
    }

    @Override
    public void sessionFailedToStart() {

    }

    @Override
    public void invalidCardDetails() {

    }

    @Override
    public void backendUnknownError(String message) {

    }

    @Override
    public void invalidTransactionMode() {

    }

    @Override
    public void invalidCustomerID() {

    }

    @Override
    public void userEnabledSaveCardOption(boolean saveCardEnabled) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
