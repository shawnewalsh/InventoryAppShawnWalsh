package com.example.android.inventoryappshawnwalsh.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class InvProvider extends ContentProvider {

    // for debugging
    public static final String LOG_TAG = InvProvider.class.getSimpleName();

    /** URI matcher code --> content URI for the products/inventory table (list) */
    private static final int PRODS = 100;

    /** single product */
    private static final int PROD_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, PRODS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", PROD_ID);
    }

    /** Database helper object */
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor curs;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODS:
                curs = database.query(InventoryContract.inventoryItem.TABLE_NAME, proj, sel, selArgs,
                        null, null, sortOrder);
                break;
            case PROD_ID:
                sel = InventoryContract.inventoryItem._ID + "=?";
                selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                curs = database.query(InventoryContract.inventoryItem.TABLE_NAME, proj, sel, selArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        curs.setNotificationUri(getContext().getContentResolver(), uri);
        return curs;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Please enter a product name...");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryContract.inventoryItem.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insert did not work... " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        System.out.println(values.toString());
        System.out.println(uri.toString());
        System.out.println(selectionArgs.toString());
        System.out.println(selection.toString());
        System.out.println(selectionArgs.toString());
        System.out.println("Uri = " + uri.toString() + " , ContentValues = " + values.toString() + " ,selection = " + selection.toString() + " selectionArgs = " + selectionArgs);
        if (values.containsKey(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Please enter a product name...");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            int rowsUpdated = database.update(InventoryContract.inventoryItem.TABLE_NAME, values, selection, selectionArgs);
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
        }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODS:
                return InventoryContract.inventoryItem.CONTENT_LIST_TYPE;
            case PROD_ID:
                return InventoryContract.inventoryItem.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(Uri uri, String sel, String[] selArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODS:
                rowsDeleted = database.delete(InventoryContract.inventoryItem.TABLE_NAME, sel, selArgs);
                break;
            case PROD_ID:
                sel = InventoryContract.inventoryItem._ID + "=?";
                selArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.inventoryItem.TABLE_NAME, sel, selArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete didn't work..." + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String sel,
                      String[] selArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODS:
                return updateProduct(uri, contentValues, sel, selArgs);
            case PROD_ID:
                sel = InventoryContract.inventoryItem._ID + "=?";
                selArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, sel, selArgs);
            default:
                throw new IllegalArgumentException("Update did not work... " + uri);
        }
    }

    public int updateInventory(Uri uri, ContentValues contentValues, String sel, String[] selArgs) {
                sel = InventoryContract.inventoryItem._ID + "=?";
                selArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, sel, selArgs);
        }
    }

