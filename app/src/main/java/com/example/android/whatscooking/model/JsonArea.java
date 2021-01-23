package com.example.android.whatscooking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JsonArea {
    @SerializedName("meals")
    @Expose
    private List<Area> fullAreaList = null;

    public List<String> getFullAreaList() {
        List<String> strArea = new ArrayList<>();
        for (int i = 0; i < fullAreaList.size(); i++) {
            strArea.add(i, fullAreaList.get(i).getName());
        }
        return strArea;
    }

    public void setFullAreaList(List<Area> fullAreaList) {
        this.fullAreaList = fullAreaList;
    }
}
