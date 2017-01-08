package com.example.android.inventoryappshawnwalsh.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class InventoryDbHelper extends SQLiteOpenHelper {
    // for debugging later if needed
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    // name of the database
    private static final String DATABASE_NAME = "inventory.db";
    // may need to write updates, so lets start versioning the database
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // get string for the activities table
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + InventoryContract.inventoryItem.TABLE_NAME + " ("
                + InventoryContract.inventoryItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + InventoryContract.inventoryItem.COLUMN_PRICE + " TEXT, "
                + InventoryContract.inventoryItem.COLUMN_QTY + " INTEGER DEFAULT 0, "
                + InventoryContract.inventoryItem.COLUMN_COST + " TEXT);";
        // run the DDL
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // not implemented, still at first version
    }
}
