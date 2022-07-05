package com.almusand.aaber.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.almusand.aaber.R;
import com.almusand.aaber.custom.DialogImage;
import com.almusand.aaber.model.MessageModel;
import com.almusand.aaber.model.NotifyFcm;
import com.almusand.aaber.model.Offer;
import com.almusand.aaber.model.Order;
import com.almusand.aaber.model.User;
import com.almusand.aaber.utils.AppPreferences;
import com.almusand.aaber.utils.EndlessRecyclerViewScrollListener;
import com.almusand.aaber.utils.LocaleManager;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.pusher.client.Pusher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private User currentUser;
    private String conversationId;
    private String providerName;
    private ChatViewModel viewModel;

    private TextView tvUsername;
    private ImageView imgBack;

    private Offer offer;
    private Order order;
    private Integer orderId;
    private Integer offerId;

    private List<MessageModel> conversationList;
    private NotifyFcm notifyFcm;
    private Boolean havePagination = false;
    private ShimmerRecyclerView rvMsgList;
    private LinearLayoutManager layoutManager;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        currentUser = new Gson().fromJson(AppPreferences.getUser(this), User.class);

        prpareDataFromIntent();
        initialViews();

        setupRecyclerViewChat();
        getConversation();

    }

    private void prpareDataFromIntent() {
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("notifyFcm")) {
                    notifyFcm = (NotifyFcm) getIntent().getExtras().get(key);
                    providerName = notifyFcm.getUserName();
                    conversationId = String.valueOf(notifyFcm.getConversationId());
                    orderId = notifyFcm.getOrderId();
                    offerId = notifyFcm.getOfferId();
                } else if (key.equals("offer")) {
                    offer = (Offer) getIntent().getExtras().getSerializable("offer");
                    conversationId = String.valueOf(offer.getConversation().getId());
                    providerName = offer.getOwnerOfferDetails().getUserName();
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

    private void getConversation() {
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
                    conversationList = conversations;
                    mAdapter.setList(conversationList);
                    rvMsgList.setAdapter(mAdapter);
                    rvMsgList.scrollToPosition(conversationList.size() - 1);

                    rvMsgList.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreMessage(page + 1);
                            }
                        }
                    });
                } else {
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
//                audioRecordView.hideLoading();
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

    private void initialViews() {

        rvMsgList = findViewById(R.id.rvMsgList);
        imgBack = findViewById(R.id.img_back);
        tvUsername = findViewById(R.id.tv_username);

        if (currentUser.getType().equals("user")) {
            tvUsername.setText(providerName);
        } else {
            tvUsername.setText(MessagesActivity.this.getResources().getText(R.string.client));
        }
        conversationList = new ArrayList<>();
        imgBack.setOnClickListener(this);
        tvUsername.setOnClickListener(this);

    }


    private void setupRecyclerViewChat() {
        mAdapter = new ChatAdapter(MessagesActivity.this, currentUser, new ChatAdapter.onItemClick() {
            @Override
            public void onItemClick(MessageModel messageModel, ImageView imageView) {
                new DialogImage(MessagesActivity.this, messageModel.getContent()).show();
            }
        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMsgList.setHasFixedSize(true);
        rvMsgList.setLayoutManager(layoutManager);
        rvMsgList.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}