package com.jevin.jmart.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("code")
    private String Code;
    @SerializedName("imageUrl")
    private String imageUrl;

    public Category() {
    }

    public Category(int id, String name, String code, String imageUrl) {
        this.id = id;
        this.name = name;
        Code = code;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
