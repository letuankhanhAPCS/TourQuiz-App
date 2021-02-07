package com.example.midtermmobile;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MyLocation implements java.io.Serializable {
    private String locationName, locationDescription;
    private int locationPicture;
    private double locationLat, locationLng;
    private boolean unlock;
    private String questionDescription, answer;
    private int questionPicture;

    public MyLocation(String locationName, String locationDescription, int locationPicture,
                      double locationLat, double locationLng, boolean unlock,
                      String questionDescription, String answer, int questionPicture) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationPicture = locationPicture;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.unlock = unlock;
        this.questionDescription = questionDescription;
        this.answer = answer;
        this.questionPicture = questionPicture;
    }

    public MyLocation(String locationName, String locationDescription, int locationPicture, double locationLat, double locationLng, boolean unlock) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationPicture = locationPicture;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.unlock = unlock;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public int getLocationPicture() {
        return locationPicture;
    }

    public void setLocationPicture(int locationPicture) {
        this.locationPicture = locationPicture;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
    }

    public boolean isUnlock() {
        return unlock;
    }

    public void setUnlock(boolean unlock) {
        this.unlock = unlock;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getQuestionPicture() {
        return questionPicture;
    }

    public void setQuestionPicture(int questionPicture) {
        this.questionPicture = questionPicture;
    }
}
