package com.example.money.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.money.R;

public class MoreCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_categories);
        setSupportActionBar(findViewById(R.id.toolbarCategories));
    }
    @Override
    public void onBackPressed(){
        finish();
    }

}