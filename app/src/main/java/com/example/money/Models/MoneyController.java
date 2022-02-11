package com.example.money.Models;
import java.util.ArrayList;

//pattern Singleton
public class MoneyController {
    //region params
    private DataBaseHelper databaseHelper;
    static private MoneyController moneyController;
    //endregion

    //region pattern realization
    private MoneyController() {}
    public void setDatabaseHelper(DataBaseHelper db)
    {
        databaseHelper = db;
    }
    static public MoneyController getInstance(){
        if (moneyController == null){
            moneyController = new MoneyController();
        }
        return moneyController;
    }
    //endregion

    //region getters
    public int getTotalIncome(){
        int total = 0;
        ArrayList<Transaction> transactions = databaseHelper.getAllTransactions();
        for (Transaction t : transactions){
            if (t.getValue() > 0){
                total += t.getValue();
            }
        }
        return total;
    }
    public int getTotalExpenses(){
        int total = 0;
        ArrayList<Transaction> transactions = databaseHelper.getAllTransactions();
        for (Transaction t : transactions){
            if (t.getValue() < 0){
                total -= t.getValue();
            }
        }
        return total;
    }
    public int getBalance(){
        return getTotalIncome() - getTotalExpenses();
    }
    public int getCount(){
        return databaseHelper.getCountOfTransactions();
    }
    public boolean isDroppedDatabase(){
        return databaseHelper.isDropped();
    }
    //endregion

    //region Interact
    public void writeNewTransaction(Transaction transaction){
        if ((transaction == null)||(transaction.getValue() == 0)) {
            return;
        }
        databaseHelper.writeNewTransaction(transaction);
    }
    public void dropDataBase(){
        databaseHelper.dropDataBase();
    }
    public ArrayList<Transaction> getArrayOfTransactions(){
        return databaseHelper.getAllTransactions();
    }
    //TODO: Search by Category_id
    public void deleteTransaction(Transaction transaction){
        databaseHelper.deleteTransaction(transaction);
    }
    //endregion

}
