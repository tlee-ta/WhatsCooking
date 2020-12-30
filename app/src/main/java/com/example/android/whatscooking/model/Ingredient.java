package com.example.android.whatscooking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Ingredient implements Serializable {

    @SerializedName("idIngredient")
    private int id;
    @SerializedName("strIngredient")
    private String name;

    public Ingredient(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

}
