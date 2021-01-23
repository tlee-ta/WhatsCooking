package com.example.android.whatscooking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JsonCategory {
    @SerializedName("meals")
    @Expose
    private List<Category> fullCategoryList = null;

    public List<String> getFullCategoryList() {
        List<String> strCategories = new ArrayList<>();
        for (int i=0; i < fullCategoryList.size(); i++) {
            strCategories.add(i, fullCategoryList.get(i).getName());
        }
        return strCategories;
    }

    public void setFullCategoryList(List<Category> fullCategoryList) {
        this.fullCategoryList = fullCategoryList;
    }
}
