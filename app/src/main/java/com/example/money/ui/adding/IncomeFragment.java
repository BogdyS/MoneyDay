package com.example.money.ui.adding;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.money.Models.DataBaseHelper;
import com.example.money.Models.MoneyController;
import com.example.money.Models.Transaction;
import com.example.money.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {
    //region params
    private ImageButton applyButton;
    private static String input;
    private EditText num;
    private View lastSelected;
    private static String date;
    //endregion
    public IncomeFragment() {
        super(R.layout.fragment_income);
    }

    public static IncomeFragment newInstance() {
        IncomeFragment fragment = new IncomeFragment();
        return fragment;
    }
    public static void clearInstance() {
        input = null;
        date = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }
    @Override
    public void onDestroy() {
        if (num != null){
            input = num.getText().toString();
        }
        clearInstance();
        super.onDestroy();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        //region InitView
        applyButton = view.findViewById(R.id.apply_button);
        num = view.findViewById(R.id.editTextNumber1);
        TableLayout tableCategories = view.findViewById(R.id.table_categories);
        //endregion
        //region Categories
        ArrayList<String> incomeList = (new DataBaseHelper(getContext())).incomeCategories();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < incomeList.size(); i+=3){
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < 3; j++){
                if (i + j >= incomeList.size()) break;
                View v = inflater.inflate(R.layout.table_cell, tableRow, false);
                TextView textView = v.findViewById(R.id.category_text);
                textView.setText(incomeList.get(i+j));
                ImageButton imageButton = v.findViewById(R.id.category_button);
                imageButton.setOnClickListener(categoryButton -> {
                    if (lastSelected != null){
                        lastSelected.setBackgroundColor(Color.TRANSPARENT);
                    }
                    lastSelected = (View) categoryButton.getParent();
                    categoryButton.setPressed(true);
                    lastSelected.setBackgroundColor(Color.GRAY);
                });
                v.setOnClickListener(cell -> {
                    if (lastSelected != null){
                        lastSelected.setBackgroundColor(Color.TRANSPARENT);
                    }
                    lastSelected = cell;
                    lastSelected.setBackgroundColor(Color.GRAY);
                });
                tableRow.addView(v);
            }
            tableCategories.addView(tableRow, i / 3);
        }
        //endregion
        //region EditText
        if (input!= null) {
            applyButton.setClickable(false);
            num.setText(input);
            input = null;
        }
        if (num.getText() != null){
            if ((num.getText().toString().length() != 0) && (num.getText().toString().charAt(0) == '0'))
                applyButton.setClickable(false);
        }
        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout inputLayout = view.findViewById(R.id.textInputLayout);
                inputLayout.setError("Некорректная сумма");
                if (num.getText().length() == 0){
                    inputLayout.setErrorEnabled(false);
                }
                else if ((num.getText().toString().charAt(0) == '0')) {
                    applyButton.setClickable(false);
                    try{
                    if ((num.getText().toString().charAt(0) == '0'))
                    {
                    inputLayout.setErrorEnabled(true);
                    }}
                    catch (Exception e) {inputLayout.setErrorEnabled(true);}
                }
                else {
                    try{
                        Integer.parseInt(num.getText().toString());
                        applyButton.setClickable(true);
                        inputLayout.setErrorEnabled(false);

                    }
                    catch (Throwable e){
                        applyButton.setClickable(false);
                    }
                }
            }
        });
        //endregion
        //region ApplyButton
        applyButton.setClickable(false);
        applyButton.setOnClickListener(v -> {
            if (lastSelected != null){
                int value;
                Transaction transaction;
                if (date != null){
                    transaction = new Transaction();
                    transaction.setDate(date);
                }
                else{
                    String date = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH).format(new Date());
                    transaction = new Transaction();
                    transaction.setDate(date);
                }
                try {
                    value = Integer.parseInt(num.getText().toString());
                }
                catch(Exception e) {return;}
                finally {
                    num.getText().clear();
                    input = "";
                }
                if (AddingFragment.getSelectedDate() == null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    date = dateFormat.format(Calendar.getInstance().getTime());
                }
                else{
                    date = AddingFragment.getSelectedDate();
                }
                transaction.setDate(date);
                transaction.setValue(value);
                transaction.setCategory(((TextView)(lastSelected.findViewById(R.id.category_text))).getText().toString());
                MoneyController.getInstance().writeNewTransaction(transaction);
                lastSelected.setBackgroundColor(Color.TRANSPARENT);
                lastSelected = null;
            }
        });
        //endregion
    }
}