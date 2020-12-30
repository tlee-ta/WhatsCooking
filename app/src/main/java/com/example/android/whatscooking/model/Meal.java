package com.example.android.whatscooking.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "favMeals")
public class Meal implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("idMeal")
    private int id;
    @SerializedName("strMeal")
    private String name;
    @SerializedName("strMealThumb")
    private String thumbnail;
    @SerializedName("strInstructions")
    private String instructions;
    @SerializedName("strYoutube")
    private String youtube;
    @SerializedName("strIngredient1")
    private String ingredient1;
    @SerializedName("strMeasure1")
    private String measure1;
    @SerializedName("strIngredient2")
    private String ingredient2;
    @SerializedName("strMeasure2")
    private String measure2;
    @SerializedName("strIngredient3")
    private String ingredient3;
    @SerializedName("strMeasure3")
    private String measure3;
    @SerializedName("strIngredient4")
    private String ingredient4;
    @SerializedName("strMeasure4")
    private String measure4;
    @SerializedName("strIngredient5")
    private String ingredient5;
    @SerializedName("strMeasure5")
    private String measure5;
    @SerializedName("strIngredient6")
    private String ingredient6;
    @SerializedName("strMeasure6")
    private String measure6;
    @SerializedName("strIngredient7")
    private String ingredient7;
    @SerializedName("strMeasure7")
    private String measure7;
    @SerializedName("strIngredient8")
    private String ingredient8;
    @SerializedName("strMeasure8")
    private String measure8;
    @SerializedName("strIngredient9")
    private String ingredient9;
    @SerializedName("strMeasure9")
    private String measure9;
    @SerializedName("strIngredient10")
    private String ingredient10;
    @SerializedName("strMeasure10")
    private String measure10;
    @SerializedName("strIngredient11")
    private String ingredient11;
    @SerializedName("strMeasure11")
    private String measure11;
    @SerializedName("strIngredient12")
    private String ingredient12;
    @SerializedName("strMeasure12")
    private String measure12;
    @SerializedName("strIngredient13")
    private String ingredient13;
    @SerializedName("strMeasure13")
    private String measure13;
    @SerializedName("strIngredient14")
    private String ingredient14;
    @SerializedName("strMeasure14")
    private String measure14;
    @SerializedName("strIngredient15")
    private String ingredient15;
    @SerializedName("strMeasure15")
    private String measure15;
    @SerializedName("strIngredient16")
    private String ingredient16;
    @SerializedName("strMeasure16")
    private String measure16;
    @SerializedName("strIngredient17")
    private String ingredient17;
    @SerializedName("strMeasure17")
    private String measure17;
    @SerializedName("strIngredient18")
    private String ingredient18;
    @SerializedName("strMeasure18")
    private String measure18;
    @SerializedName("strIngredient19")
    private String ingredient19;
    @SerializedName("strMeasure19")
    private String measure19;
    @SerializedName("strIngredient20")
    private String ingredient20;
    @SerializedName("strMeasure20")
    private String measure20;

    private String ingredients;

    public Meal(int id, String name, String thumbnail, String instructions, String youtube, String ingredients){
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.instructions = instructions;
        this.youtube = youtube;
        this.ingredients = ingredients;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getThumbnail() {return thumbnail;}

    public void setThumbnail(String thumbnail) {this.thumbnail = thumbnail;}

    public String getInstructions() {return instructions;}

    public void setInstructions(String instructions) {this.instructions = instructions;}

    public String getYoutube() {return youtube;}

    public void setYoutube(String youtube) {this.youtube = youtube;}

    public String getIngredients() {
        List<String> ingredientList = Arrays.asList(ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, ingredient6, ingredient7, ingredient8, ingredient9, ingredient10, ingredient11, ingredient12, ingredient13, ingredient14, ingredient15, ingredient16, ingredient17, ingredient18, ingredient19, ingredient20);
        List<String> measureList = Arrays.asList(measure1, measure2, measure3, measure4, measure5, measure6, measure7, measure8, measure9, measure10, measure11, measure12, measure13, measure14, measure15, measure16, measure17, measure18, measure19, measure20);

        for (int i=0; i < ingredientList.size(); i++) {
            if (ingredientList.get(i) != null) {
                if (ingredientList.get(i).isEmpty()) {
                    break;
                }
                if (i == 0) {
                    ingredients = measureList.get(i) + " " + ingredientList.get(i);
                }
                else {
                    ingredients += "\r\n" + measureList.get(i) + " " + ingredientList.get(i);
                }
            }
            else {
                break;
            }
        }
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredient1() {return ingredient1;}

    public void setIngredient1(String ingredient1) {this.ingredient1 = ingredient1;}

    public String getIngredient2() {return ingredient2;}

    public void setIngredient2(String ingredient2) {this.ingredient2 = ingredient2;}

    public String getIngredient3() {return ingredient3;}

    public void setIngredient3(String ingredient3) {this.ingredient3 = ingredient3;}

    public String getIngredient4() {return ingredient4;}

    public void setIngredient4(String ingredient4) {this.ingredient4 = ingredient4;}

    public String getIngredient5() {return ingredient5;}

    public void setIngredient5(String ingredient5) {this.ingredient5 = ingredient5;}

    public String getIngredient6() {return ingredient6;}

    public void setIngredient6(String ingredient6) {this.ingredient6 = ingredient6;}

    public String getIngredient7() {return ingredient7;}

    public void setIngredient7(String ingredient7) {this.ingredient7 = ingredient7;}

    public String getIngredient8() {return ingredient8;}

    public void setIngredient8(String ingredient8) {this.ingredient8 = ingredient8;}

    public String getIngredient9() {return ingredient9;}

    public void setIngredient9(String ingredient9) {this.ingredient9 = ingredient9;}

    public String getIngredient10() {return ingredient10;}

    public void setIngredient10(String ingredient10) {this.ingredient10 = ingredient10;}

    public String getIngredient11() {return ingredient11;}

    public void setIngredient11(String ingredient11) {this.ingredient11 = ingredient11;}

    public String getIngredient12() {return ingredient12;}

    public void setIngredient12(String ingredient12) {this.ingredient12 = ingredient12;}

    public String getIngredient13() {return ingredient13;}

    public void setIngredient13(String ingredient13) {this.ingredient13 = ingredient13;}

    public String getIngredient14() {return ingredient14;}

    public void setIngredient14(String ingredient14) {this.ingredient14 = ingredient14;}

    public String getIngredient15() {return ingredient15;}

    public void setIngredient15(String ingredient15) {this.ingredient15 = ingredient15;}

    public String getIngredient16() {return ingredient16;}

    public void setIngredient16(String ingredient16) {this.ingredient16 = ingredient16;}

    public String getIngredient17() {return ingredient17;}

    public void setIngredient17(String ingredient17) {this.ingredient17 = ingredient17;}

    public String getIngredient18() {return ingredient18;}

    public void setIngredient18(String ingredient18) {this.ingredient18 = ingredient18;}

    public String getIngredient19() {return ingredient19;}

    public void setIngredient19(String ingredient19) {this.ingredient19 = ingredient19;}

    public String getIngredient20() {return ingredient20;}

    public void setIngredient20(String ingredient20) {this.ingredient20 = ingredient20;}

    public String getMeasure1() {return measure1;}

    public void setMeasure1(String measure1) {this.measure1 = measure1;}

    public String getMeasure2() {return measure2;}

    public void setMeasure2(String measure2) {this.measure2 = measure2;}

    public String getMeasure3() {return measure3;}

    public void setMeasure3(String measure3) {this.measure3 = measure3;}

    public String getMeasure4() {return measure4;}

    public void setMeasure4(String measure4) {this.measure4 = measure4;}

    public String getMeasure5() {return measure5;}

    public void setMeasure5(String measure5) {this.measure5 = measure5;}

    public String getMeasure6() {return measure6;}

    public void setMeasure6(String measure6) {this.measure6 = measure6;}

    public String getMeasure7() {return measure7;}

    public void setMeasure7(String measure7) {this.measure7 = measure7;}

    public String getMeasure8() {return measure8;}

    public void setMeasure8(String measure8) {this.measure8 = measure8;}

    public String getMeasure9() {return measure9;}

    public void setMeasure9(String measure9) {this.measure9 = measure9;}

    public String getMeasure10() {return measure10;}

    public void setMeasure10(String measure10) {this.measure10 = measure10;}

    public String getMeasure11() {return measure11;}

    public void setMeasure11(String measure11) {this.measure11 = measure11;}

    public String getMeasure12() {return measure12;}

    public void setMeasure12(String measure12) {this.measure12 = measure12;}

    public String getMeasure13() {return measure13;}

    public void setMeasure13(String measure13) {this.measure13 = measure13;}

    public String getMeasure14() {return measure14;}

    public void setMeasure14(String measure14) {this.measure14 = measure14;}

    public String getMeasure15() {return measure15;}

    public void setMeasure15(String measure15) {this.measure15 = measure15;}

    public String getMeasure16() {return measure16;}

    public void setMeasure16(String measure16) {this.measure16 = measure16;}

    public String getMeasure17() {return measure17;}

    public void setMeasure17(String measure17) {this.measure17 = measure17;}

    public String getMeasure18() {return measure18;}

    public void setMeasure18(String measure18) {this.measure18 = measure18;}

    public String getMeasure19() {return measure19;}

    public void setMeasure19(String measure19) {this.measure19 = measure19;}

    public String getMeasure20() {return measure20;}

    public void setMeasure20(String measure20) {this.measure20 = measure20;}
}
