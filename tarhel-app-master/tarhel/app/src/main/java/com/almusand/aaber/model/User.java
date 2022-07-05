package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class User implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("personal_id")
    @Expose
    private String personalId;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("total_balance")
    @Expose
    private Integer totalBalance;
    @SerializedName("total_cash")
    @Expose
    private Integer totalCash;
    @SerializedName("total_online")
    @Expose
    private Integer totalOnline;
    @SerializedName("enable_notify")
    @Expose
    private Integer enableNotify;
    @SerializedName("categories")
    @Expose
    private List<Categories> categories = null;
    @SerializedName("averageRating")
    @Expose
    private Object averageRating;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("address")
    @Expose
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Integer totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Integer getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(Integer totalCash) {
        this.totalCash = totalCash;
    }

    public Integer getTotalOnline() {
        return totalOnline;
    }

    public void setTotalOnline(Integer totalOnline) {
        this.totalOnline = totalOnline;
    }

    public Integer getEnableNotify() {
        return enableNotify;
    }

    public void setEnableNotify(Integer enableNotify) {
        this.enableNotify = enableNotify;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    public Object getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Object averageRating) {
        this.averageRating = averageRating;
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

    public class Name {

        @SerializedName("ar")
        @Expose
        private String ar;
        @SerializedName("en")
        @Expose
        private String en;

        public String getAr() {
            return ar;
        }

        public void setAr(String ar) {
            this.ar = ar;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

    }

    public class Pivot {

        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("category_id")
        @Expose
        private Integer categoryId;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

    }

}