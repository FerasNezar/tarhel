package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class Order implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("order_code")
    @Expose
    private Integer orderCode;
    @SerializedName("qr_code")
    @Expose
    private String qrCode;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("location_dropoff")
    @Expose
    private String locationDropoff;
    @SerializedName("pickup_lat")
    @Expose
    private String pickupLat;
    @SerializedName("pickup_lng")
    @Expose
    private String pickupLng;
    @SerializedName("dropoff_lat")
    @Expose
    private String dropoffLat;
    @SerializedName("dropoff_lng")
    @Expose
    private String dropoffLng;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("category")
    @Expose
    private Categories category;
    @SerializedName("conversation")
    @Expose
    private Conversation conversation;
    @SerializedName("offers")
    @Expose
    private List<Offer> offers = null;
    @SerializedName("arrival_date")
    @Expose
    private String arrivalDate;
    @SerializedName("status")
    @Expose
    private String status;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationDropoff() {
        return locationDropoff;
    }

    public void setLocationDropoff(String locationDropoff) {
        this.locationDropoff = locationDropoff;
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

    public String getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(String dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public String getDropoffLng() {
        return dropoffLng;
    }

    public void setDropoffLng(String dropoffLng) {
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

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}