package com.example.android.inventoryappshawnwalsh;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappshawnwalsh.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PROD_LOADER = 0;
    private Uri mCurrentProdUri;
    private EditText mProdNameEditText;
    private EditText mProdDescEditText;
    private EditText mProdPriceEditText;
    private EditText mProdCostEditText;
    private TextView mQuantityTextView;
    //sell and order buttons
    private EditText mSellProductEditText;
    private EditText mOrderNoteEditText;

    private boolean mProdHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProdHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_editor);
        Intent intent = getIntent();
        mCurrentProdUri = intent.getData();
        if (mCurrentProdUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PROD_LOADER, null, this);
        }
        mProdNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProdNameEditText.setOnTouchListener(mTouchListener);

        mProdDescEditText = (EditText) findViewById(R.id.edit_product_desc);
        mProdDescEditText.setOnTouchListener(mTouchListener);

        mProdPriceEditText = (EditText) findViewById(R.id.edit_retailPrice);
        mProdPriceEditText.setOnTouchListener(mTouchListener);

        mProdCostEditText = (EditText) findViewById(R.id.edit_product_cost);
        mProdCostEditText.setOnTouchListener(mTouchListener);

        mQuantityTextView = (TextView) findViewById(R.id.qtyTV);
        mQuantityTextView.setOnTouchListener(mTouchListener);

        mSellProductEditText = (EditText) findViewById(R.id.qtyToSellDetailTV);
        mSellProductEditText.setOnTouchListener(mTouchListener);

        mOrderNoteEditText = (EditText) findViewById(R.id.qtyToOrderDetailTV);
        mOrderNoteEditText.setOnTouchListener(mTouchListener);


    }

    private void saveProduct() {
        String prodNameString = mProdNameEditText.getText().toString().trim();
        String prodDescString = mProdDescEditText.getText().toString().trim();
        String prodPriceString = mProdPriceEditText.getText().toString().trim();
        String prodCostString = mProdCostEditText.getText().toString().trim();

        if (mCurrentProdUri == null &&
                TextUtils.isEmpty(prodNameString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME, prodNameString);
        values.put(InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION, prodDescString);
         values.put(InventoryContract.inventoryItem.COLUMN_PRICE, prodPriceString);
        values.put(InventoryContract.inventoryItem.COLUMN_COST, prodCostString);

        if (mCurrentProdUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.inventoryItem.CONTENT_URI, values);
            System.out.println("I got here");
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void orderMore() {
        String prodNameString = mProdNameEditText.getText().toString().trim();
        String order = mOrderNoteEditText.getText().toString().trim();
        int orderQty = Integer.parseInt(order);
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        // add current order to quantity
        int newQuantity = quantity + orderQty;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.inventoryItem.COLUMN_QTY, newQuantity);

        if (mCurrentProdUri == null &&
                TextUtils.isEmpty(prodNameString)) {
            return;
        }
        if (mCurrentProdUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.inventoryItem.CONTENT_URI, values);
            System.out.println("I got here");
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sellProd() {
        String prodNameString = mProdNameEditText.getText().toString().trim();
        String sell = mSellProductEditText.getText().toString().trim();
        int sellQty = Integer.parseInt(sell);
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        // add current order to quantity
        int newQuantity = quantity - sellQty;

        if (newQuantity < 0) {
            Context context = getApplicationContext();
            CharSequence text = "You do not have enough inventory to complete this sale...";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }   else {

            ContentValues values = new ContentValues();
            values.put(InventoryContract.inventoryItem.COLUMN_QTY, newQuantity);

            if (mCurrentProdUri == null &&
                    TextUtils.isEmpty(prodNameString)) {
                return;
            }
            if (mCurrentProdUri == null) {
                Uri newUri = getContentResolver().insert(InventoryContract.inventoryItem.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_prod_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);
                System.out.println("mCurrentProdUri = " + mCurrentProdUri.toString());
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProdHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void orderBtnPress(View v){
        orderMore();
        finish();
    }

    public void sellBtnPress(View v){
        sellProd();
        finish();
    }
    @Override
    public void onBackPressed() {
        if (!mProdHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.inventoryItem._ID,
                InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME,
                InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION,
                InventoryContract.inventoryItem.COLUMN_PRICE,
                InventoryContract.inventoryItem.COLUMN_COST,
                InventoryContract.inventoryItem.COLUMN_QTY
        };
        return new CursorLoader(this, mCurrentProdUri, projection, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_PRICE);
            int costColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_COST);
            int qtyColumnIndex = cursor.getColumnIndex(InventoryContract.inventoryItem.COLUMN_QTY);

            String prodName = cursor.getString(nameColumnIndex);
            mProdNameEditText.setText(prodName);

            String prodDesc = cursor.getString(descriptionColumnIndex);
            mProdDescEditText.setText(prodDesc);

            String prodPrice = cursor.getString(priceColumnIndex);
            mProdPriceEditText.setText(prodPrice);

            String prodCost = cursor.getString(costColumnIndex);
            mProdCostEditText.setText(prodCost);

            String prodQty = cursor.getString(qtyColumnIndex);
            mQuantityTextView.setText(prodQty);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProdNameEditText.setText("");
        mProdDescEditText.setText("");
        mProdPriceEditText.setText("");
        mProdCostEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProdUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
