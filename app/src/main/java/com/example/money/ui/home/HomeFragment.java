package com.example.money.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.money.Models.MoneyController;
import com.example.money.R;
import com.example.money.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        MoneyController moneyController = MoneyController.getInstance();
        //region setTextViews
        TextView income = view.findViewById(R.id.incomeTV);
        income.setText(Integer.toString(moneyController.getTotalIncome()));
        TextView expenses = view.findViewById(R.id.expencesTV);
        expenses.setText(Integer.toString(moneyController.getTotalExpenses()));
        TextView balance = view.findViewById(R.id.balanceTV);
        balance.setText(Integer.toString(moneyController.getBalance()));
        //endregion
    }
}