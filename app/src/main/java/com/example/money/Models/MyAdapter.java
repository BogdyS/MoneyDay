package com.example.money.Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.money.R;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Transaction> {

    private final ArrayList<Transaction> list;

    public MyAdapter(Context context, ArrayList<Transaction> dataList) {
        super(context, R.layout.element_layout, dataList);
        this.list = dataList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_layout, parent, false);
        }
        //region initColumns
        TextView columnValue = convertView.findViewById(R.id.element_value);
        TextView columnDate = convertView.findViewById(R.id.element_date);
        TextView columnCategory = convertView.findViewById(R.id.element_category);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteiItem);
        //endregion
        deleteButton.setOnClickListener( (view) -> {
                Transaction transaction = new Transaction();
                transaction.setValue(Integer.parseInt(columnValue.getText().toString()));
                transaction.setDate(columnDate.getText().toString());
                transaction.setCategory(columnCategory.getText().toString());
                MoneyController.getInstance().deleteTransaction(transaction);
                for (int i =0; i < list.size(); i++){
                    if ((list.get(i).getDate().equals(transaction.getDate()))&&
                            (list.get(i).getValue() == transaction.getValue())&&
                            (list.get(i).getCategory() == transaction.getCategory())){
                        list.remove(i);
                        notifyDataSetChanged();
                        break;
                    }
                }
        });
        //region setItem
        columnValue.setText(Integer.toString(data.getValue()));
        columnDate.setText(data.getDate());
        columnCategory.setText(data.getCategory());
        //endregion
        return convertView;
    }

}
