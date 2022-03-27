package com.example.money.Models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.money.R;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    //region DBParams
    public static final String DATABASE_NAME = "application.db";
    public static final int DATABASE_VERSION = 6;
    //endregion

    //region DefaultParams
    private final String[] baseExpenses = {"Транспорт", "Спорт", "Еда", "Автомобиль"};
    private final Integer[] ExpensePictureId = {R.drawable.vehicles, R.drawable.fitness, R.drawable.apple,
            R.drawable.transport, R.drawable.plus};
    private final String[] baseIncomes = {"Зарплата", "Чаевые"};
    private final Integer[] IncomePictureId = {R.drawable.moneybag, R.drawable.cashpayment};
    //endregion

    //region Default Input
    private void inputBaseExpenses(SQLiteDatabase db){
        for (int i = 0; i < baseExpenses.length ; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("value", baseExpenses[i]);
            contentValues.put("picture_id", ExpensePictureId[i]);
            Log.i("DATA",Integer.toString(ExpensePictureId[i]) + " " + baseExpenses[i]);
            db.insert("expenses", null, contentValues);
        }
    }
    private void inputBaseIncome(SQLiteDatabase db){
        for (int i = 0; i < baseIncomes.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("value", baseIncomes[i]);
            contentValues.put("picture_id", IncomePictureId[i]);
            db.insert("incomes", null, contentValues);
        }
    }
    //endregion

    //region override
    public DataBaseHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @SuppressLint("Recycle")
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Transactions table
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions " +
                "(id INTEGER PRIMARY KEY, value INTEGER, date TEXT,category_id INTEGER)");
        //ExpensesCategoryTable
        db.execSQL("CREATE TABLE IF NOT EXISTS expenses " +
                "(id INTEGER PRIMARY KEY, value TEXT, picture_id INTEGER)");
        //IncomeCategory table
        db.execSQL("CREATE TABLE IF NOT EXISTS incomes " +
                "(id INTEGER PRIMARY KEY, value TEXT, picture_id INTEGER)");
        //region DefaultInput
        //region Expenses
        @SuppressLint("Recycle") Cursor cur = db.rawQuery("SELECT * FROM expenses", null);
        if (cur == null) {
            inputBaseExpenses(db);
        }
        else {
            cur.moveToFirst();
            if (cur.getCount() == 0) {
                inputBaseExpenses(db);
            }
        }
        //endregion
        //region Incomes
        cur = db.rawQuery("SELECT * FROM incomes", null);
        if (cur == null){
            inputBaseIncome(db);
        }
        else {
            cur.moveToFirst();
            if (cur.getCount() == 0){
                inputBaseIncome(db);
            }
        }
        //endregion
        //endregion
    }
    //TODO:try to write Normal Update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("DROP TABLE IF EXISTS transactions");
          db.execSQL("DROP TABLE IF EXISTS incomes");
          db.execSQL("DROP TABLE IF EXISTS expenses");
          onCreate(db);
    }
    //endregion

    //region dataBase Interact
    @SuppressLint("Recycle")
    public void writeNewTransaction(@NonNull Transaction transaction){
        ContentValues values = new ContentValues();
        Cursor c;
        if (transaction.getValue() <= 0){
            c = getReadableDatabase().rawQuery(
                    "SELECT * FROM expenses WHERE value = ?",
                    new String[]{transaction.getCategory()});
        }
        else {
            c = getReadableDatabase().rawQuery(
                    "SELECT * FROM incomes WHERE value = ?",
                    new String[]{transaction.getCategory()});
        }
        values.put("date", transaction.getDate());
        values.put("value", transaction.getValue());
        c.moveToFirst();
        values.put("category_id", c.getInt(0));
        getWritableDatabase().insert("transactions", null, values);
    }
    public void deleteTransaction (@NonNull Transaction transaction){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (transaction.getValue() > 0){
            Log.i("!@#$", transaction.getCategory());
            c = db.rawQuery(
                    "SELECT id FROM incomes WHERE value = ?"
                    , new String[]{transaction.getCategory()});
        }
        else{
            Log.i("!@#$", transaction.getCategory());
            c = db.rawQuery(
                    "SELECT * FROM expenses WHERE value = ?"
                    , new String[]{transaction.getCategory()});
        }
        c.moveToFirst();
        int id = c.getInt(0);
        getWritableDatabase().execSQL("DELETE FROM transactions WHERE (value = " + transaction.getValue() +") " +
                "and (date = " + transaction.getDate() + ") " +
                "and (category_id = " + id + ")");
        getWritableDatabase().delete(
                "transactions", "value = ? and date = ? and category_id = ?",
                new String[]{Integer.toString(transaction.getValue()), transaction.getDate(), Integer.toString(id)});
    }
    @SuppressLint("Recycle")
    public ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM transactions", null);
        while (cursor.moveToNext()){
            Transaction transaction = new Transaction();
            transaction.setValue(cursor.getInt(1));
            transaction.setDate(cursor.getString(2));
            Cursor cursor1;
            if (transaction.getValue() <= 0){
                cursor1 = getReadableDatabase().rawQuery("SELECT * FROM expenses WHERE id = ?",
                        new String[]{cursor.getString(3)});
            }
            else{
                cursor1 = getReadableDatabase().rawQuery("SELECT * FROM incomes WHERE id = ?",
                        new String[]{cursor.getString(3)});
            }
            cursor1.moveToFirst();
            transaction.setCategory(cursor1.getString(1));
            transactions.add(transaction);
        }
        return transactions;
    }
    public int getCountOfTransactions(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM transactions", null);
        return cursor.getCount();
    }
    public void dropDataBase(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }
    public void dropAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS incomes");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }
    public int getExpensePictureId(String categoryName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT picture_id FROM expenses WHERE value = ?", new String[]{categoryName});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public int getIncomePictureId(String categoryName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT picture_id FROM incomes WHERE value = ?", new String[]{categoryName});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    //endregion

    //region getters
    //Is Transactions DB dropped
    public boolean isDropped() {
        @SuppressLint("Recycle") Cursor cur = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM transactions", null);
        if (cur != null) {
            cur.moveToFirst();
            return cur.getInt(0) == 0;
        }
        return true;
    }

    //Get all Expenses from DB
    public ArrayList<String> expensesCategories(){
        @SuppressLint("Recycle") Cursor cursor = getWritableDatabase().rawQuery(
                "SELECT * FROM expenses", null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String data = cursor.getString(1);
            list.add(data);
        }
        return list;
    }

    //Get all Incomes from DB
    public ArrayList<String> incomeCategories(){
        @SuppressLint("Recycle") Cursor cursor = getWritableDatabase().rawQuery(
                "SELECT * FROM incomes", null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String data = cursor.getString(1);
            list.add(data);
        }
        return list;
    }
    //endregion

}
