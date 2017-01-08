package com.example.android.inventoryappshawnwalsh;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappshawnwalsh.data.InventoryContract;

import static com.example.android.inventoryappshawnwalsh.data.InventoryContract.inventoryItem.CONTENT_URI;


public class InventoryListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PROD_LOADER = 0;
    private Uri mCurrentProdUri;
    private EditText mProdNameEditText;
    private EditText mProdDescEditText;
    private EditText mProdPriceEditText;
    private EditText mProdCostEditText;
    private TextView mQuantityTextView;
    private boolean mProdHasChanged = false;

    InvCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryListActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        ListView prodListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        prodListView.setEmptyView(emptyView);

        mCursorAdapter = new InvCursorAdapter(this, null);
        prodListView.setAdapter(mCursorAdapter);

        prodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(InventoryListActivity.this, EditorActivity.class);
                Uri currentProdUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentProdUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PROD_LOADER, null, this);
    }

    public void increment(View v) {
        // we will have multiple list items, so we need to make sure that when the
        // button is clicked, is references the parent list view item and update that one...
        // get parent linear layout
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        // our text view is index 1
        TextView saleCounter = (TextView)vwParentRow.getChildAt(1);
        // get the current value as a string
        String currentSalesCount = saleCounter.getText().toString();
        // convert it to an integer
        int currentCount = Integer.valueOf(currentSalesCount);
        // increment it
        currentCount++;
        // comvert back to a string
        String newCount = Integer.toString(currentCount);
        // update the textView
        saleCounter.setText(newCount);
    }

    public void decrement(View v) {
        // we will have multiple list items, so we need to make sure that when the
        // button is clicked, is references the parent list view item and update that one...
        // get parent linear layout
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        // our text view is index 1
        TextView saleCounter = (TextView)vwParentRow.getChildAt(1);
        // get the current value as a string
        String currentSalesCount = saleCounter.getText().toString();
        // convert it to an integer
        int currentCount = Integer.valueOf(currentSalesCount);
        // increment it
        currentCount--;
        if (currentCount < 0) {
            currentCount = 0;
        }
        // comvert back to a string
        String newCount = Integer.toString(currentCount);
        // update the textView
        saleCounter.setText(newCount);;
    }

    public void completeSale(View v){
        // get textview items..,
        LinearLayout ll2 = (LinearLayout) ((ViewGroup) v.getParent().getParent());
        LinearLayout ll4 = (LinearLayout) ((ViewGroup) ll2.getChildAt(1));
        LinearLayout ll1 = (LinearLayout) ((ViewGroup) v.getParent().getParent().getParent());
        // get value for sale count, change value to int and store in an int variable
        TextView saleCounterTV = (TextView) ll4.getChildAt(1);
        String saleNumString = saleCounterTV.getText().toString().trim();
        int sale = Integer.parseInt(saleNumString);
        // get product details for list item quantity (current value before sale)
        LinearLayout llProdContainer = (LinearLayout)ll1.getChildAt(0);
        LinearLayout firstLinLayout = (LinearLayout)llProdContainer.getChildAt(0);
        TextView qtyTV = (TextView)firstLinLayout.getChildAt(2);
        // get string for quantity
        String quantity = qtyTV.getText().toString().substring(5);
        int qtyNum = Integer.parseInt(quantity);
        // quantity after sale (if done)
        int newQty = qtyNum - sale;
        if (newQty < 0 ) {
            Context context = getApplicationContext();
            CharSequence text = "You do not have enough inventory to complete this sale...";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            String newQtyString = "Qty: " + String.valueOf(newQty);
            qtyTV.setText(newQtyString);
            ContentValues values = new ContentValues();
            values.put(InventoryContract.inventoryItem.COLUMN_QTY, newQty);
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);

        }
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("InventoryListActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.inventoryItem._ID,
                InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME,
                InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION,
                InventoryContract.inventoryItem.COLUMN_QTY,
                InventoryContract.inventoryItem.COLUMN_PRICE,
                InventoryContract.inventoryItem.COLUMN_COST};

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}


