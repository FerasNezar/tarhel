package com.almusand.aaber.ui.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.custom.AddRateDialog;
import com.almusand.aaber.custom.BarcodeDialog;
import com.almusand.aaber.custom.DialogImage;
import com.almusand.aaber.model.MessageModel;
import com.almusand.aaber.model.NotifyFcm;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.ui.contactUs.ContactUsActivity;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.ui.myOrders.OrdersViewModel;
import com.almusand.aaber.ui.offerDetails.OfferDetailsViewModel;
import com.almusand.aaber.ui.orderDetails.OrderDetailsActivity;
import com.almusand.aaber.ui.payment.PaymentActivity;
import com.almusand.aaber.ui.scanQrCode.ScanQrCodeActivity;
import com.almusand.aaber.ui.sendOffer.SendOfferActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.varunjohn1990.audio_record_view.AttachmentOption;
import com.varunjohn1990.audio_record_view.AttachmentOptionsListener;
import com.varunjohn1990.audio_record_view.AudioRecordView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import timber.log.Timber;

import static com.almusand.aaber.utils.Constants.PICK_Camera_IMAGE;
import static com.almusand.aaber.utils.Constants.SELECT_IMAGE;
import static com.varunjohn1990.audio_record_view.AttachmentOption.CAMERA_ID;
import static com.varunjohn1990.audio_record_view.AttachmentOption.GALLERY_ID;

public class ChatActivity extends BaseActivity implements View.OnClickListener, AttachmentOptionsListener, AudioRecordView.RecordingListener {

    private static final int RECORD_AUDIO_CONSTANT = 10;
    private TextView tvUsername;
    private ImageView imgBack;
    private RecyclerView rvMsgList;
    private TextView tvOptions;
    private NestedScrollView nestedScrollViewChat;

    private TextView tvModifyPrice;
    private TextView tvMarkDelivered;
    private TextView tvCheckout;
    private TextView tvQr;
    private TextView tvRate;
    private TextView tvOrderDetails;
    private TextView tvSendProblem;
    private TextView tvCancel;
    private View viewCheckout;

    private AudioRecordView audioRecordView;
    private LinearLayoutManager layoutManager;
    private ChatAdapter mAdapter;

    private Uri imageUri;
    private String imagePath;
    private HashMap<String, File> mapFiles;
    private HashMap<String, String> map;
    private ChatViewModel viewModel;

    private User currentUser;
    private String conversationId;
    private String providerName;
    private String clientName;

    private Pusher pusher;
    private List<MessageModel> conversationList;
    private Offer offer;
    private Order order;
    private Integer orderId;
    private Integer offerId;

    private File audiofile;
    private File photoFile;
    private NotifyFcm notifyFcm;
    private long time;
    private Boolean havePagination = false;
    private MediaRecorder mediaRecorder;
    private MediaRecorder recorder;
    private String outputRecorder;
    private String imageFilePath;
    private View sheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        audioRecordView = new AudioRecordView();
        audioRecordView.initView((FrameLayout) findViewById(R.id.layoutMain));
        View containerView = audioRecordView.setContainerView(R.layout.layout_chatting);
        audioRecordView.setRecordingListener(this);

        audioRecordView.setHintToEditTextMessage(getString(R.string.write_some_thing));

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        currentUser = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        prpareDataFromIntent();

        if (offer == null && offerId != null) {
            getOffer(offerId);
        }

        initialViews(containerView);

        setUserNameText();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_CONSTANT);
        } else {
            recordAudio();
        }

        setListener();

        List<AttachmentOption> attachmentOptions = new ArrayList<>();
        attachmentOptions.add(new AttachmentOption(CAMERA_ID, "Camera", R.drawable.ic_attachment_camera));
        attachmentOptions.add(new AttachmentOption(GALLERY_ID, "Gallery", R.drawable.ic_attachment_gallery));

        audioRecordView.setAttachmentOptions(attachmentOptions, this);
        audioRecordView.showEmojiIcon(false);
        audioRecordView.removeAttachmentOptionAnimation(false);

        setupRecyclerViewChat();

        getConversation();

        setUpPusherConfig();

        getOrderDetails();

        if (offer != null && offer.getStatus().equals("cancelled")) {
            tvOptions.setVisibility(View.GONE);
            audioRecordView.getSendView().setVisibility(View.GONE);
            audioRecordView.getMessageView().setVisibility(View.GONE);
        }

    }

    private void setUserNameText() {
        if (currentUser.getType().equals("user") && providerName != null) {
            tvUsername.setText(providerName);
        } else if (clientName != null) {
            tvUsername.setText(clientName);
        }
    }

    private void recordAudio() {
        outputRecorder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputRecorder);
    }

    private void getOffer(Integer offerId) {
        OfferDetailsViewModel offerDetailsViewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);
        offerDetailsViewModel.getOffer(ChatActivity.this, String.valueOf(offerId));
        offerDetailsViewModel.mutableLiveData.observe(this, new Observer<Offer>() {
            @Override
            public void onChanged(Offer mOffer) {
                if (offer != null) {
                    offer = mOffer;
                }
            }
        });
    }

    private void getOrderDetails() {
        if (orderId == null) {
            orderId = order.getId();
        }
        OrdersViewModel viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        viewModel.getSingleOrder(this, orderId);
        viewModel.mutableLiveDataSingleOrder.observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order mOrder) {
                order = mOrder;
                tvOptions.setEnabled(true);
            }
        });

    }

    private void prpareDataFromIntent() {
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("notifyFcm")) {
                    notifyFcm = (NotifyFcm) getIntent().getExtras().get(key);
                    providerName = notifyFcm.getChatTo().toString();
                    clientName = notifyFcm.getChatFrom().toString();
                    conversationId = String.valueOf(notifyFcm.getConversationId());
                    orderId = notifyFcm.getOrderId();
                    offerId = notifyFcm.getOfferId();

                } else if (key.equals("offer")) {
                    offer = (Offer) getIntent().getExtras().getSerializable("offer");
                    conversationId = String.valueOf(offer.getConversation().getId());
                    providerName = offer.getOwnerOfferDetails().getUserName();
                    clientName = offer.getOrderDetails().getUser().getUserName();
                } else if (key.equals("order")) {
                    order = (Order) getIntent().getExtras().getSerializable("order");
                    orderId = order.getId();
                } else if (key.equals("model")) {
                    try {
                        JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString(key));
                        notifyFcm = new Gson().fromJson(jsonObject.toString(), NotifyFcm.class);
                        conversationId = String.valueOf(notifyFcm.getConversationId());
                        providerName = notifyFcm.getUserName();
                        conversationId = String.valueOf(notifyFcm.getConversationId());
                        orderId = notifyFcm.getOrderId();
                        offerId = notifyFcm.getOfferId();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (key.equals("order_id")) {
                    orderId = getIntent().getExtras().getInt(key);
                }
            }

        }
    }

    private void setUpPusherConfig() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher("17ace750a3c78faf76b1", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Timber.e("State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Timber.e("There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e);
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("conversation." + conversationId);

        channel.bind("message-posted", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, String data) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(data);
                            Timber.e(object.toString());
                            MessageModel messageModel = new Gson().fromJson(object.getJSONObject("message").toString(), MessageModel.class);
                            audioRecordView.hideLoading(getString(R.string.write_some_thing));
                            mAdapter.add(messageModel);
//                            conversationList.add(messageModel);
//                            mAdapter.notifyItemRangeChanged(0,conversationList.size());
                            rvMsgList.smoothScrollToPosition(rvMsgList.getAdapter().getItemCount() - 1);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    private void getConversation() {
        audioRecordView.showLoading(getString(R.string.loading));
        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });
        viewModel.getConversation(this, conversationId, 1);

        viewModel.mutableLiveData.observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> conversations) {
                if (conversations != null) {
                    audioRecordView.hideLoading(getString(R.string.write_some_thing));
                    conversationList = conversations;
                    mAdapter.setList(conversations);
//                    rvMsgList.setAdapter(mAdapter);

                    rvMsgList.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreMessage(page + 1);
                            }
                        }
                    });
                } else {
                    audioRecordView.hideLoading(getString(R.string.write_some_thing));
                }

            }
        });


//        viewModel.mutableLiveData.observe(this, new Observer<Conversation>() {
//            @Override
//            public void onChanged(Conversation conversation) {
//                offer = conversation.getOffer();
//                order = conversation.getOrder();
//                getTotalPrice(offer);
//                conversationList = conversation.getMessages();
//                mAdapter.setList(conversation.getMessages());
//                rvMsgList.setAdapter(mAdapter);
//                rvMsgList.scrollToPosition(conversationList.size() - 1);
//                if (currentUser.getId().equals(conversation.getFromId())) {
//                    userId = conversation.getToId();
//                } else {
//                    userId = conversation.getFromId();
//                }
//
//                                                       audioRecordView.hideLoading(getString(R.string.write_some_thing));

//            }
//        });
    }

    private void loadMoreMessage(int page) {

        viewModel.getConversation(this, conversationId, page);
        viewModel.mutableLiveData.observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> conversations) {
                conversationList.addAll(conversations);
                mAdapter.setList(conversationList);
                rvMsgList.scrollToPosition(conversationList.size() - 1);
            }
        });
    }

    private void initialViews(View containerView) {

        rvMsgList = containerView.findViewById(R.id.rvMsgList);
        imgBack = containerView.findViewById(R.id.img_back);
        tvUsername = containerView.findViewById(R.id.tv_username);
        tvOptions = containerView.findViewById(R.id.tv_options);
        nestedScrollViewChat = containerView.findViewById(R.id.nestedScrollView_chat);

        conversationList = new ArrayList<>();
        imgBack.setOnClickListener(this);
        tvOptions.setOnClickListener(this);
        tvUsername.setOnClickListener(this);

    }

    private void setListener() {

        audioRecordView.getSendView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = audioRecordView.getMessageView().getText().toString().trim();
                if (!msg.isEmpty()) {
                    map = new HashMap<>();
                    map.put("conversation_id", conversationId);
                    map.put("content", msg);
                    map.put("type", "text");
                    viewModel.sendMessage(ChatActivity.this, map, null);
                    audioRecordView.getMessageView().setText("");
                }
            }
        });

        audioRecordView.getCameraView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission(1);
            }
        });
    }

    private void setupRecyclerViewChat() {

        mAdapter = new ChatAdapter(ChatActivity.this, currentUser, new ChatAdapter.onItemClick() {
            @Override
            public void onItemClick(MessageModel messageModel, ImageView imageView) {
                new DialogImage(ChatActivity.this, messageModel.getContent()).show();
            }
        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMsgList.setHasFixedSize(false);
        rvMsgList.setLayoutManager(layoutManager);
        rvMsgList.setAdapter(mAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_QR_CODE) {
            if (data != null) {
                makeOrderDelivered(data.getStringExtra("qrCode"));
            }
        }

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                mapFiles = new HashMap<String, File>();
                map = new HashMap<>();
                imagePath = getActualPath(ChatActivity.this, imageUri);

                Luban.compress(this, new File(imagePath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                map.put("conversation_id", conversationId);
                                map.put("type", "image");
                                mapFiles.put("content", file);
                                audioRecordView.showLoading(getString(R.string.loading));
                                viewModel.sendMessage(ChatActivity.this, map, mapFiles);
                                viewModel.mutableLiveMessage.observe(ChatActivity.this, new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        if (s == null) {
                                            audioRecordView.hideLoading(getString(R.string.write_some_thing));

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });


            }
        } else if (requestCode == PICK_Camera_IMAGE && resultCode == RESULT_OK) {

            Luban.compress(this, photoFile)
                    .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                    .launch(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            mapFiles = new HashMap<String, File>();
                            map = new HashMap<>();
                            map.put("conversation_id", conversationId);
                            map.put("type", "image");
                            mapFiles.put("content", file);
                            audioRecordView.showLoading(getString(R.string.loading));
                            viewModel.sendMessage(ChatActivity.this, map, mapFiles);
                            viewModel.mutableLiveMessage.observe(ChatActivity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    if (s == null) {
                                        audioRecordView.hideLoading(getString(R.string.write_some_thing));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        } else if (requestCode == 202) {
            if (resultCode == RESULT_OK) {
                String s = currentUser.getUserName() + " " + getString(R.string.has_sent_complain_to_murad_support);
                sendMessageOptions(s);
            }
        } else if (requestCode == 203) {
            if (resultCode == RESULT_OK) {
                String s = getString(R.string.order_has_been_paid) + " " + data.getStringExtra("paymentType");
                sendMessageOptions(s);
                getOrderDetails();
            }
        } else if (requestCode == 12) {
            if (data != null && data.getStringExtra("new_price") != null) {
                String newPrice = data.getStringExtra("new_price");
                offer = (Offer) data.getSerializableExtra("offer");
                String s = new StringBuilder().append(ChatActivity.this.getResources().getString(R.string.msg_price_updated)).append(" ").append(newPrice).append(" ")
                        .append(ChatActivity.this.getResources().getString(R.string.sar)).toString();
                sendMessageOptions(s);
            }
        }
    }

    private void makeOrderDelivered(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("qr_code", code);
        viewModel.makeOrderDelivered(ChatActivity.this, order.getId(), map);
        viewModel.mutableLiveDataOrder.observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order mOrder) {
                if (order != null) {
                    order = mOrder;
                    startActivity(new Intent(ChatActivity.this, MainActivityProvider.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.img_back:
                finish();
                break;

            case R.id.tv_options:
                showBottomSheetOptions();
                break;
        }

    }

    private void showBottomSheetOptions() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
         sheetView = getLayoutInflater().inflate(R.layout.layout_chat_options, null);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(Color.TRANSPARENT);

        initBottomSheetViews(sheetView);

        tvSendProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChatActivity.this, ContactUsActivity.class)
                        .putExtra("flag", "chat"), 202);
                mBottomSheetDialog.dismiss();
            }
        });

        tvMarkDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
                mBottomSheetDialog.dismiss();
            }
        });

        tvQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BarcodeDialog(ChatActivity.this, order.getQrCode()).show();
                mBottomSheetDialog.dismiss();
            }
        });

        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddRateDialog(ChatActivity.this, currentUser, new AddRateDialog.OnAddRate() {
                    @Override
                    public void OnAddRate(String rate) {
                        String s = currentUser.getUserName() + " " + getString(R.string.has_rated_you_with_optional)
                                + rate + " " + getString(R.string.stars);
                        sendMessageOptions(s);
                    }
                }).show();
                mBottomSheetDialog.dismiss();

            }
        });

        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChatActivity.this, PaymentActivity.class)
                        .putExtra("order", order)
                        .putExtra("offer", offer), 203);
                mBottomSheetDialog.dismiss();
            }
        });

        tvModifyPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChatActivity.this, SendOfferActivity.class)
                        .putExtra("offer", offer).putExtra("order", order)
                        .putExtra("modify", "yes"), 12);
                mBottomSheetDialog.dismiss();
            }
        });

        tvOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, OrderDetailsActivity.class)
                        .putExtra("order", order).putExtra("chat", "chat"));
                mBottomSheetDialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.show();

    }

    private void openScanner() {
//        new IntentIntegrator(ChatActivity.this).initiateScan();
        startActivityForResult(new Intent(ChatActivity.this, ScanQrCodeActivity.class), Constants.INTENT_QR_CODE);
    }

    private void initBottomSheetViews(View sheetView) {

        tvModifyPrice = sheetView.findViewById(R.id.tv_modify_price);
        tvMarkDelivered = sheetView.findViewById(R.id.tv_mark_delivered);
        tvCheckout = sheetView.findViewById(R.id.tv_checkout);
        tvQr = sheetView.findViewById(R.id.tv_qr);
        tvRate = sheetView.findViewById(R.id.tv_rate);
        tvOrderDetails = sheetView.findViewById(R.id.tv_order_details);
        tvSendProblem = sheetView.findViewById(R.id.tv_send_problem);
        tvCancel = sheetView.findViewById(R.id.tv_cancel);
        viewCheckout = sheetView.findViewById(R.id.view_checkout);

        if (offer != null && offer.getStatus() != null)
            switch (offer.getStatus()) {

                case "confirmed":
                    if (currentUser.getType().equals("user")) {
                        tvQr.setVisibility(View.VISIBLE);
                        tvCheckout.setVisibility(View.VISIBLE);
                        viewCheckout.setVisibility(View.VISIBLE);
                    } else {
                        tvMarkDelivered.setVisibility(View.VISIBLE);
                        tvModifyPrice.setVisibility(View.VISIBLE);
                    }
                    break;

                case "delivered":
                    tvRate.setVisibility(View.VISIBLE);
                    if (currentUser.getType().equals("user")) {
                        tvRate.append(ChatActivity.this.getResources().getString(R.string.client));
                    } else {
                        tvRate.append(ChatActivity.this.getResources().getString(R.string.provider));
                    }
                    break;

            }

        if (order != null && order.getStatus().equals("paid")) tvCheckout.setVisibility(View.GONE);

    }

    private void sendMessageOptions(String s) {
        map = new HashMap<>();
        map.put("conversation_id", conversationId);
        map.put("content", s);
        map.put("type", "text");
        viewModel.sendMessage(ChatActivity.this, map, null);
    }

    @Override
    public void onClick(AttachmentOption attachmentOption) {
        switch (attachmentOption.getId()) {

            case CAMERA_ID:
                checkStoragePermission(1);
                break;
            case GALLERY_ID:
                checkStoragePermission(0);
                break;

        }
    }

    public void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture To Send"), Constants.SELECT_IMAGE);
    }

    public void OpenCamera() {
        Uri fileUri = null;

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a fileUri to store the image
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                fileUri = FileProvider.getUriForFile(this, "com.almusand.aaber.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(pictureIntent,
                        PICK_Camera_IMAGE);
            }
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferences.setIsUserInConversation(ChatActivity.this, "yes");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppPreferences.setIsUserInConversation(ChatActivity.this, "no");
    }

    private void checkStoragePermission(int type) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (type == 0) {
                    OpenGallery();
                } else {
                    OpenCamera();
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(ChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(ChatActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onRecordingStarted() {
        time = System.currentTimeMillis() / (1000);

        try {
            startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRecordingLocked() {
        stopRecording();
    }

    @Override
    public void onRecordingCompleted() {
        stopRecording();
        recorder.release();
        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);

        if (recordTime > 1) {
            sendVoice(audiofile.getAbsolutePath());
        }

    }

    public void startRecording() throws IOException {

        //Creating file
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".mp4", dir);
        } catch (IOException e) {
            return;
        }
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    private void sendVoice(String lastRecordedOutputFile) {
        audioRecordView.showLoading(getString(R.string.loading));
        mapFiles = new HashMap<String, File>();
        map = new HashMap<>();
        map.put("conversation_id", conversationId);
        map.put("type", "voice");
        mapFiles.put("content", new File(lastRecordedOutputFile));
        viewModel.sendMessage(this, map, mapFiles);
    }

    @Override
    public void onRecordingCanceled() {
        stopRecording();
    }

    @Override
    public void onDestroy() {
        stopRecording();
        AppPreferences.setIsUserInConversation(ChatActivity.this, null);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopRecording();
        super.onStop();
    }

    private void stopRecording() {
        try {
            recorder.stop();
        } catch (RuntimeException stopException) {
            // handle cleanup here
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_CONSTANT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audioRecordView.makeAudioEnabled();
                } else {
                    audioRecordView.makeAudioDisabled();
                }
                return;
            }
        }
    }

}
