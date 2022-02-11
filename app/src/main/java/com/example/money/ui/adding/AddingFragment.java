package com.example.money.ui.adding;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.money.R;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lib.kingja.switchbutton.SwitchMultiButton;

@SuppressWarnings("deprecation")
public class AddingFragment extends Fragment {

    //region params
    private DatePickerDialog datePickerDialog;
    private SwitchMultiButton mSwitchMultiButton;
    private static String selectedDate;
    private IncomeFragment incomeFragment;
    private ExpensesFragment expensesFragment;
    //endregion

    public static AddingFragment newInstance() {
        return new AddingFragment();
    }
    public AddingFragment() {
        super(R.layout.fragment_adding);
    }

    @Override
    public void onDestroy(){
        selectedDate = null;
        mSwitchMultiButton.setSelectedTab(1);
        IncomeFragment.clearInstance();
        ExpensesFragment.clearInstance();
        incomeFragment = null;
        expensesFragment = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adding, container, false);
    }

    public static String getSelectedDate(){
        return selectedDate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //region init
        new Thread(() -> {
            expensesFragment = ExpensesFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.moneyFragment, expensesFragment).commit();
            incomeFragment = IncomeFragment.newInstance();
        }).start();
        mSwitchMultiButton = view.findViewById(R.id.date_selector);
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getContext(),
                (datePicker, i, i1, i2) -> {},
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));

        //endregion
        //region radioButtons
        //incomeRButton = view.findViewById(R.id.radioButton3);
        //expensesRButton = view.findViewById(R.id.radioButton2);
        //expensesRButton.setOnClickListener(radioButtonClickListener);
        //expensesRButton.setChecked(true);
        //getChildFragmentManager().beginTransaction().replace(R.id.moneyFragment, ExpensesFragment.newInstance(null)).commit();
        //incomeRButton.setOnClickListener(radioButtonClickListener);
        //endregion
        //region dateSelector
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String today = dateFormat.format(new Date());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        String yesterday = dateFormat.format(c.getTime());
        mSwitchMultiButton.setText("Вчера " + yesterday, "Сегодня " + today, "Другое...").setOnSwitchListener((position, tabText) -> {
                if (position == 0){
                    selectedDate = format.format(c.getTime());
                }
                else if (position == 1) {
                    selectedDate = format.format(new Date());
                }
                else {
                    datePickerDialog.show();
                }
        });
        mSwitchMultiButton.setSelectedTab(1);
        selectedDate = format.format(new Date());
        //endregion
        //region Calendar
        datePickerDialog.setOnDateSetListener((datePicker, year, month, day) -> {
            String formatted = dateFormat.format(new Date(year-1900, month, day));
            selectedDate = format.format(new Date(year-1900, month, day));
            if (formatted.equals(today)) {
                mSwitchMultiButton.setSelectedTab(1);
            }
            else if (formatted.equals(yesterday)) {
                mSwitchMultiButton.setSelectedTab(0);
            }
            else {
                mSwitchMultiButton.setText(yesterday, today, formatted);
            }
        });
        //endregion
        //region NavigationTabStrip
        NavigationTabStrip navigationTabStrip = view.findViewById(R.id.navtabstrip);
        navigationTabStrip.setTitles("Расходы","Доходы");
        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener()
        {
            @Override
            public void onStartTabSelected(String title, int index) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                if (index == 1) {
                    transaction.replace(R.id.moneyFragment, incomeFragment).commit();
                } else {
                    transaction.replace(R.id.moneyFragment, expensesFragment).commit();
                }
            }
            @Override
            public void onEndTabSelected(String title, int index) {
            }
        });
        navigationTabStrip.setTabIndex(0, true);
        navigationTabStrip.setStripColor(Color.parseColor("#01BCD8"));
        //endregion

    }
}