package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotifyFcm implements Serializable {

    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("db_notification_id")
    @Expose
    private String dbNotificationId;
    @SerializedName("conversation_id")
    @Expose
    private Integer conversationId;
    @SerializedName("offer_id")
    @Expose
    private Integer offerId;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("chat_from")
    @Expose
    private String chatFrom;
    @SerializedName("chat_to")
    @Expose
    private String chatTo;

    public String getChatFrom() {
        return chatFrom;
    }

    public void setChatFrom(String chatFrom) {
        this.chatFrom = chatFrom;
    }

    public String getChatTo() {
        return chatTo;
    }

    public void setChatTo(String chatTo) {
        this.chatTo = chatTo;
    }

    public NotifyFcm() {
    }

    public String getDbNotificationId() {
        return dbNotificationId;
    }

    public void setDbNotificationId(String dbNotificationId) {
        this.dbNotificationId = dbNotificationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
