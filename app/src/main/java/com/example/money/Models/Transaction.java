package com.example.money.Models;

import androidx.annotation.NonNull;

public class Transaction {

    //region params
    private int value;
    private String date;
    private String category;
    //endregion

    public Transaction(){}

    //region setters
    public void setValue(int v){
        value = v;
    }
    public void setDate(String d){
        date = d;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    //endregion

    //region getters
    public int getValue() {
        return value;
    }
    public String getDate(){
        return date;
    }
    public String getCategory() {
        return category;
    }
    //endregion

    @NonNull
    @Override
    public String toString() {
        return value + "\t\t\t" + date;
    }
}
