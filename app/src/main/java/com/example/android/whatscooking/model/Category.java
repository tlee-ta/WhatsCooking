package com.example.android.whatscooking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable {

    @SerializedName("strCategory")
    private String name;

    public Category(String name){
        this.name = name;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

}