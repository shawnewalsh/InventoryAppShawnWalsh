package com.example.android.inventoryappshawnwalsh;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryappshawnwalsh.data.InventoryContract;

public class InvCursorAdapter  extends CursorAdapter {
    //comment

    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_inventory_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // product name
        TextView prodNameTextView = (TextView) view.findViewById(R.id.productNameTV);
        // quantity
        TextView QtyTextView = (TextView) view.findViewById(R.id.inStockTV);
        // price
        TextView retailTV = (TextView) view.findViewById(R.id.retailPriceTV);
        int prodNameColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME);
        int prodDescColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION);
        int qtyProductIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_QTY);
        int retailProdIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRICE);

        // get string for each attribute/column
        String prodName = cursor.getString(prodNameColumnIndex);
        String prodDesc = cursor.getString(prodDescColumnIndex);
        String qty = cursor.getString(qtyProductIndex);
        String retail = cursor.getString(retailProdIndex);

        //populate textviews
        //String productIdformatted = String.valueOf(prodIDString);
        //System.out.println("product id is " + productIdformatted);
        prodNameTextView.setText("Product Name: " + prodName);
        QtyTextView.setText("Qty: " + qty);
        retailTV.setText("Retail Price: " + retail);

    }

}
