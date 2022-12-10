package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DatabaseController;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseTransactionDAO implements TransactionDAO {


    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseTransactionDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", accountNo);

        contentValues.put(DatabaseController.COLUMN_DATE,  dateFormat.format(date));
        contentValues.put(DatabaseController.COLUMN_TYPE, String.valueOf(expenseType));
        contentValues.put(DatabaseController.COLUMN_AMOUNT, amount);

        DB.insert(DatabaseController.TABLE_NAME2 , null , contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME2+";" , null);

        if(cursor.getCount()>0){

            List<Transaction> transactions = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_AMOUNT));

                ExpenseType expenseType = null;

                if(ExpenseType.EXPENSE.name().equals(type)){
                    expenseType = ExpenseType.EXPENSE;
                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(dateStr);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            return new ArrayList<Transaction>();
        }
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        DatabaseController DBH = DatabaseController.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        List<Transaction> transactions = new ArrayList<>();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME2+ " LIMIT "+limit+";" , null);

        if(cursor.getCount()>0){

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_AMOUNT));

                ExpenseType expenseType = ExpenseType.valueOf(type);

                try{
                    Date date = dateFormat.parse(dateStr);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            cursor = DB.rawQuery("SELECT * FROM "+ DatabaseController.TABLE_NAME2+";" , null);

            if(cursor.getCount()>0){

                while (cursor.moveToNext()){
                    String acc_no = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_ACC_NO));
                    String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_DATE));
                    String type = cursor.getString(cursor.getColumnIndex(DatabaseController.COLUMN_TYPE));
                    double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseController.COLUMN_AMOUNT));

                    ExpenseType expenseType = null;

                    if(ExpenseType.EXPENSE.name().equals(type)){
                        expenseType = ExpenseType.EXPENSE;
                    }else expenseType = ExpenseType.INCOME;

                    try{
                        Date date = dateFormat.parse(dateStr);
                        transactions.add(new Transaction(date , acc_no , expenseType , amount));
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                }
            }
            return transactions;
        }
    }
}
