package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class Notification implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("notifiable_type")
    @Expose
    private String notifiableType;
    @SerializedName("notifiable_id")
    @Expose
    private Integer notifiableId;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("read_at")
    @Expose
    private Object readAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotifiableType() {
        return notifiableType;
    }

    public void setNotifiableType(String notifiableType) {
        this.notifiableType = notifiableType;
    }

    public Integer getNotifiableId() {
        return notifiableId;
    }

    public void setNotifiableId(Integer notifiableId) {
        this.notifiableId = notifiableId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Object getReadAt() {
        return readAt;
    }

    public void setReadAt(Object readAt) {
        this.readAt = readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public class Data implements Serializable {

        @SerializedName("message_en")
        @Expose
        private String messageEn;
        @SerializedName("message_ar")
        @Expose
        private String messageAr;
        @SerializedName("offer_id")
        @Expose
        private Integer offerId;
        @SerializedName("order_id")
        @Expose
        private Integer orderId;

        @SerializedName("message_id")
        @Expose
        private Integer messageId;

        @SerializedName("conversation_id")
        @Expose
        private Integer conversationId;

          @SerializedName("user_name")
        @Expose
        private String userName;

        public Integer getMessageId() {
            return messageId;
        }

        public void setMessageId(Integer messageId) {
            this.messageId = messageId;
        }

        public Integer getConversationId() {
            return conversationId;
        }

        public void setConversationId(Integer conversationId) {
            this.conversationId = conversationId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMessageEn() {
            return messageEn;
        }

        public void setMessageEn(String messageEn) {
            this.messageEn = messageEn;
        }

        public String getMessageAr() {
            return messageAr;
        }

        public void setMessageAr(String messageAr) {
            this.messageAr = messageAr;
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
}
