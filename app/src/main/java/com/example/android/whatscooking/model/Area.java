package com.example.android.whatscooking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Area implements Serializable {

    @SerializedName("strArea")
    private String name;

    public Area(String name){
        this.name = name;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

}
