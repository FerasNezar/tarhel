package com.almusand.aaber.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Setting implements Serializable {

    @SerializedName("contact_phone")
    @Expose
    private String contactPhone;
    @SerializedName("contact_address")
    @Expose
    private String contactAddress;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("contact_email")
    @Expose
    private String contactEmail;
    @SerializedName("intro_2_en")
    @Expose
    private String intro2En;
    @SerializedName("intro_2_ar")
    @Expose
    private String intro2Ar;
    @SerializedName("intro_3_en")
    @Expose
    private String intro3En;
    @SerializedName("intro_3_ar")
    @Expose
    private String intro3Ar;
    @SerializedName("intro_en")
    @Expose
    private String introEn;
    @SerializedName("intro_ar")
    @Expose
    private String introAr;
    @SerializedName("privacy_en")
    @Expose
    private String privacyEn;
    @SerializedName("privacy_ar")
    @Expose
    private String privacyAr;
  @SerializedName("undreadnotifications")
    @Expose
    private int undreadnotifications;

    public int getUndreadnotifications() {
        return undreadnotifications;
    }

    public void setUndreadnotifications(int undreadnotifications) {
        this.undreadnotifications = undreadnotifications;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getIntro2En() {
        return intro2En;
    }

    public void setIntro2En(String intro2En) {
        this.intro2En = intro2En;
    }

    public String getIntro2Ar() {
        return intro2Ar;
    }

    public void setIntro2Ar(String intro2Ar) {
        this.intro2Ar = intro2Ar;
    }

    public String getIntro3En() {
        return intro3En;
    }

    public void setIntro3En(String intro3En) {
        this.intro3En = intro3En;
    }

    public String getIntro3Ar() {
        return intro3Ar;
    }

    public void setIntro3Ar(String intro3Ar) {
        this.intro3Ar = intro3Ar;
    }

    public String getIntroEn() {
        return introEn;
    }

    public void setIntroEn(String introEn) {
        this.introEn = introEn;
    }

    public String getIntroAr() {
        return introAr;
    }

    public void setIntroAr(String introAr) {
        this.introAr = introAr;
    }

    public String getPrivacyEn() {
        return privacyEn;
    }

    public void setPrivacyEn(String privacyEn) {
        this.privacyEn = privacyEn;
    }

    public String getPrivacyAr() {
        return privacyAr;
    }

    public void setPrivacyAr(String privacyAr) {
        this.privacyAr = privacyAr;
    }

}
