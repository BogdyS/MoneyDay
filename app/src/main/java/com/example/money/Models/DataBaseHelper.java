package com.example.money.Models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    //region DBParams
    public static final String DATABASE_NAME = "application.db";
    public static final String TABLE_NAME = "transactions";
    public static final String KEY_DATE = "date";
    public static final String KEY_CATEGORY = "category_id";
    public static final int DATABASE_VERSION = 4;
    public static final String KEY_ID = "_id";
    public static final String KEY_VALUE = "value";
    public static final String CATEGORY_TABLE = "categories";
    public static final String INCOME_TABLE = "income";
    //endregion

    //region DefaultParams
    private final String[] baseCategory = {"Транспорт", "Спорт", "Еда", "Автомобиль", "<Неизвестная категория>"};
    private final String[] baseIncome = {"Зарплата", "Чаевые", "<Неизвестная категория>"};
    //endregion

    //region Default Input
    private void inputBasedCategories(SQLiteDatabase db){
        for (String s : baseCategory) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_VALUE, s);
            db.insert(CATEGORY_TABLE, null, contentValues);
        }
    }
    private void inputBasedIncome(SQLiteDatabase db){
        for (String s : baseIncome) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_VALUE, s);
            db.insert(INCOME_TABLE, null, contentValues);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_VALUE + " INTEGER, " + KEY_DATE + " TEXT, " + KEY_CATEGORY + " INTEGER)");
        //ExpensesCategoryTable
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
         + KEY_VALUE + " TEXT)");
        //IncomeCategory table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + INCOME_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_VALUE + " TEXT)");
        //region DefaultInput
        @SuppressLint("Recycle") Cursor cur = db.rawQuery("SELECT * FROM " + CATEGORY_TABLE, null);
        if (cur == null) {
            inputBasedCategories(db);
        }
        else {
            cur.moveToFirst();
            if (cur.getCount() == 0) {
                inputBasedCategories(db);
            }
        }
        cur = db.rawQuery("SELECT * FROM " + INCOME_TABLE, null);
        if (cur == null){
            inputBasedIncome(db);
        }
        else {
            cur.moveToFirst();
            if (cur.getCount() == 0){
                inputBasedIncome(db);
            }
        }
        //endregion
    }
    //TODO:try to write Normal Update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
          db.execSQL("DROP TABLE IF EXISTS " + INCOME_TABLE);
          db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
          onCreate(db);
    }
    //endregion

    //region dataBase Interact
    @SuppressLint("Recycle")
    public void writeNewTransaction(@NonNull Transaction transaction){
        ContentValues values = new ContentValues();
        Cursor c;
        if (transaction.getValue() <= 0){
            c = getReadableDatabase().rawQuery("SELECT * FROM " + DataBaseHelper.CATEGORY_TABLE +
                    " WHERE " + DataBaseHelper.KEY_VALUE + " = ?", new String[]{transaction.getCategory()});
        }
        else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + DataBaseHelper.INCOME_TABLE +
                    " WHERE " + DataBaseHelper.KEY_VALUE + " = ?", new String[]{transaction.getCategory()});
        }
        values.put(DataBaseHelper.KEY_DATE, transaction.getDate());
        values.put(DataBaseHelper.KEY_VALUE, transaction.getValue());
        c.moveToFirst();
        values.put(DataBaseHelper.KEY_CATEGORY, c.getInt(0));
        getWritableDatabase().insert(DataBaseHelper.TABLE_NAME, null, values);
    }
    public void deleteTransaction (@NonNull Transaction transaction){
        getWritableDatabase().delete(DataBaseHelper.TABLE_NAME, "value = ? and date = ?",
                new String[]{Integer.toString(transaction.getValue()), transaction.getDate()});
    }
    @SuppressLint("Recycle")
    public ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM "+ DataBaseHelper.TABLE_NAME, null);
        while (cursor.moveToNext()){
            Transaction transaction = new Transaction();
            transaction.setValue(cursor.getInt(1));
            transaction.setDate(cursor.getString(2));
            Cursor cursor1;
            if (transaction.getValue() <= 0){
                cursor1 = getReadableDatabase().rawQuery("SELECT * FROM " + DataBaseHelper.CATEGORY_TABLE +
                        " WHERE " + DataBaseHelper.KEY_ID + " = ?", new String[]{cursor.getString(3)});
            }
            else{
                cursor1 = getReadableDatabase().rawQuery("SELECT * FROM " + DataBaseHelper.INCOME_TABLE +
                        " WHERE " + DataBaseHelper.KEY_ID + " = ?", new String[]{cursor.getString(3)});
            }
            cursor1.moveToFirst();
            transaction.setCategory(cursor1.getString(1));
            transactions.add(transaction);
        }
        return transactions;
    }
    public int getCountOfTransactions(){
        @SuppressLint("Recycle") Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + DataBaseHelper.TABLE_NAME, null);
        return cursor.getCount();
    }
    public void dropDataBase(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    //endregion

    //region getters
    //Is Transactions DB dropped
    public boolean isDropped() {
        @SuppressLint("Recycle") Cursor cur = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cur != null) {
            cur.moveToFirst();                       // Always one row returned.
            return cur.getInt(0) == 0;
        }
        return true;
    }

    //Get all Expenses from DB
    public ArrayList<String> expensesCategories(){
        @SuppressLint("Recycle") Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM " + CATEGORY_TABLE, null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String data = cursor.getString(1);
            list.add(data);
        }
        return list;
    }

    //Get all Incomes from DB
    public ArrayList<String> incomeCategories(){
        @SuppressLint("Recycle") Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM " + INCOME_TABLE, null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String data = cursor.getString(1);
            list.add(data);
        }
        return list;
    }
    //endregion

}
