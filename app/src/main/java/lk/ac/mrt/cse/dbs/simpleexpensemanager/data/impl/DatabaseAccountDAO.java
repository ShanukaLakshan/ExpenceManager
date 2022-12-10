package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DatabaseController;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DatabaseAccountDAO implements AccountDAO {


    private Context context;

    public DatabaseAccountDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        DatabaseController databaseeditor = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = databaseeditor.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME1+";" , null);

        if(cursor.getCount()>0){

            List<String> accountsNumberList = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));

                accountsNumberList.add(acc_no);

            }
            return accountsNumberList;

        }else {
            return new ArrayList<String>();
        }
    }

    @Override
    public List<Account> getAccountsList() {
        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();


        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME1+";" , null);

        if(cursor.getCount()>0){

            List<Account> accounts = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));
                String bank_name = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_BANK_NAME));
                String acc_holder = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_HOLDER));
                double balance = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_BALANCE));

                accounts.add(new Account(acc_no , bank_name , acc_holder , balance));

            }
            return accounts;

        }else {
            return new ArrayList<Account>();
        }
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME1+" WHERE "+ DatabaseController.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){

            Account account = null;

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));
                String bank_name = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_BANK_NAME));
                String acc_holder = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_HOLDER));
                double balance = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_BALANCE));

                account = new Account(acc_no , bank_name , acc_holder , balance);
                break;
            }
            return account;

        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }
    }

    @Override
    public void addAccount(Account account) {

        try{
            getAccount(account.getAccountNo());
        } catch(InvalidAccountException e){

            DatabaseController DBH = DatabaseController.getInstanceDB(context);

            SQLiteDatabase DB = DBH.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("acc_no", account.getAccountNo());
            contentValues.put("bank_name", account.getBankName());
            contentValues.put("acc_holder", account.getAccountHolderName());
            contentValues.put("balance", account.getBalance());

            long result = DB.insert(DatabaseController.TABLE_NAME1 , null , contentValues);
        }

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME1+" WHERE "+ DatabaseController.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){
            DB.delete(DatabaseController.TABLE_NAME1 , "acc_no=?" , new String[]{accountNo});
        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME1+" WHERE "+ DatabaseController.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){
            double pre_balance = 0;
            while (cursor.moveToNext()){
                pre_balance = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_BALANCE));
                break;
            }

            double new_balance = -1;

            switch (expenseType) {
                case EXPENSE:
                    new_balance = pre_balance - amount;
                    break;
                case INCOME:
                    new_balance = pre_balance + amount;
                    break;
            }

            contentValues.put("balance" , new_balance);
            DB.update(DatabaseController.TABLE_NAME1 , contentValues , "acc_no=?" , new String[]{accountNo});

        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.Please check and try again");
        }

    }
}
