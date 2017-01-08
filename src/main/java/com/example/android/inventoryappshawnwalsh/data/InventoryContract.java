package com.example.android.inventoryappshawnwalsh.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *      Created by shawn on 1/1/2017.
 *      Database name: inventory
 *      _ID             --> Primary key
 *      product_name    --> Unique not_null required.
 *      product_desc    --> product description
 *      quantityInStock --> quantity in stock
 *      pricePerUnit    --> float x.2
 *      costPerUnit     --> float x.2
 **/

public class InventoryContract {

    private InventoryContract(){};

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappshawnwalsh";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "inventoryItems";

    public static final class inventoryItem implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);


        //list of products
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // single item/product, etc...
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        //database table name
        public final static String TABLE_NAME = "inventory";
        //columns
        // Primary key
        public final static String _ID = BaseColumns._ID;
        // product name
        public final static String COLUMN_PRODUCT_NAME ="product_name";
        // product description
        public final static String COLUMN_PRODUCT_DESCRIPTION = "product_desc";
        // retail price for customer/vendor purchasing
        public final static String COLUMN_PRICE = "price";
        // cost from supplier
        public final static String COLUMN_COST = "cost";
        // quantity in stocl
        public final static String COLUMN_QTY = "quantityInStock";
    }

}
