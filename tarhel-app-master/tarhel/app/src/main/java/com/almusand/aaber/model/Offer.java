package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Offer implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("order_details")
    @Expose
    private OrderDetails orderDetails;
    @SerializedName("owner_order_details")
    @Expose
    private OwnerOrderDetails ownerOrderDetails;
    @SerializedName("owner_offer_details")
    @Expose
    private OwnerOfferDetails ownerOfferDetails;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("conversation")
    @Expose
    private Conversation conversation;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OwnerOrderDetails getOwnerOrderDetails() {
        return ownerOrderDetails;
    }

    public void setOwnerOrderDetails(OwnerOrderDetails ownerOrderDetails) {
        this.ownerOrderDetails = ownerOrderDetails;
    }

    public OwnerOfferDetails getOwnerOfferDetails() {
        return ownerOfferDetails;
    }

    public void setOwnerOfferDetails(OwnerOfferDetails ownerOfferDetails) {
        this.ownerOfferDetails = ownerOfferDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public class OrderDetails implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("note")
        @Expose
        private String note;
        @SerializedName("pickup_lat")
        @Expose
        private String pickupLat;
        @SerializedName("pickup_lng")
        @Expose
        private String pickupLng;
        @SerializedName("dropoff_lat")
        @Expose
        private Object dropoffLat;
        @SerializedName("dropoff_lng")
        @Expose
        private Object dropoffLng;
        @SerializedName("weight")
        @Expose
        private String weight;
        @SerializedName("time")
        @Expose
        private String time;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("order_code")
        @Expose
        private Integer orderCode;
        @SerializedName("qr_code")
        @Expose
        private String qrCode;
        @SerializedName("arrival_date")
        @Expose
        private String arrivalDate;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("location_dropoff")
        @Expose
        private Object locationDropoff;
        @SerializedName("category_id")
        @Expose
        private Object categoryId;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("user")
        @Expose
        private User user;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getPickupLat() {
            return pickupLat;
        }

        public void setPickupLat(String pickupLat) {
            this.pickupLat = pickupLat;
        }

        public String getPickupLng() {
            return pickupLng;
        }

        public void setPickupLng(String pickupLng) {
            this.pickupLng = pickupLng;
        }

        public Object getDropoffLat() {
            return dropoffLat;
        }

        public void setDropoffLat(Object dropoffLat) {
            this.dropoffLat = dropoffLat;
        }

        public Object getDropoffLng() {
            return dropoffLng;
        }

        public void setDropoffLng(Object dropoffLng) {
            this.dropoffLng = dropoffLng;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Integer getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(Integer orderCode) {
            this.orderCode = orderCode;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getArrivalDate() {
            return arrivalDate;
        }

        public void setArrivalDate(String arrivalDate) {
            this.arrivalDate = arrivalDate;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Object getLocationDropoff() {
            return locationDropoff;
        }

        public void setLocationDropoff(Object locationDropoff) {
            this.locationDropoff = locationDropoff;
        }

        public Object getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Object categoryId) {
            this.categoryId = categoryId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }

    public class Category implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private Name name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("parent_id")
        @Expose
        private Object parentId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("pivot")
        @Expose
        private Pivot pivot;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Name getName() {
            return name;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getParentId() {
            return parentId;
        }

        public void setParentId(Object parentId) {
            this.parentId = parentId;
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

        public Pivot getPivot() {
            return pivot;
        }

        public void setPivot(Pivot pivot) {
            this.pivot = pivot;
        }

    }

}