package com.projectfkklp.saristorepos.activities.pos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.pos.checkout.InputFilterMinMax;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;

public class PosItemDetailDialog extends Dialog implements
        android.view.View.OnClickListener{
    private final int PALE_GREEN = Color.rgb(220, 255, 220);

    ShapeableImageView productImage;
    TextView productNameText, unitPriceText, subtotalPriceText, leftItemText;
    EditText quantityEdit;
    ImageButton deleteButton, minusButton, plusButton;

    Button btnDismiss, btnSubmit;
    Activity activity;
    PosViewHolder holder;

    private final TransactionItem transactionItem;
    private final TransactionItem editTransactionItem;
    private final Product product;
    private final boolean isListed;

    public PosItemDetailDialog(Activity activity, PosViewHolder holder, TransactionItem transactionItem, Product product, boolean isListed) {
        super(activity);

        this.activity = activity;
        this.holder = holder;
        this.transactionItem = transactionItem;
        this.editTransactionItem = transactionItem.clone();
        this.product = product;
        this.isListed = isListed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pos_item_detail_dialog);

        productImage = findViewById(R.id.pid_dialog_product_image);
        productNameText = findViewById(R.id.pid_dialog_product_name);
        unitPriceText = findViewById(R.id.pid_dialog_unit_price);
        quantityEdit = findViewById(R.id.pid_dialog_quantity_edit);
        subtotalPriceText = findViewById(R.id.pid_dialog_subtotal_price);
        leftItemText = findViewById(R.id.pid_dialog_left_item);
        deleteButton = findViewById(R.id.pid_dialog_btn_delete);
        minusButton = findViewById(R.id.pid_dialog_btn_minus);
        plusButton = findViewById(R.id.pid_dialog_btn_plus);

        btnDismiss = findViewById(R.id.pid_dialog_dismiss);
        btnSubmit = findViewById(R.id.pid_dialog_submit);

        // Attach listener
        btnDismiss.setOnClickListener(this);

        initializeViews();
    }

    @SuppressLint("DefaultLocale")
    private void initializeViews(){
        assert product != null;
        if (!StringUtils.isNullOrEmpty(product.getImgUrl())){
            Glide.with(activity).load(product.getImgUrl()).into(productImage);
        }
        productNameText.setText(product.getName());
        unitPriceText.setText(String.format(
                "Unit Price: %s",
                StringUtils.formatToPeso(editTransactionItem.getUnitPrice())
        ));
        quantityEdit.setText(String.valueOf(editTransactionItem.getQuantity()));
        subtotalPriceText.setText(StringUtils.formatToPeso(editTransactionItem.calculateAmount()));
        leftItemText.setText(String.format(
            "%d item left",
            product.getStocks()
        ));

        // Buttons
        deleteButton.setVisibility(isListed? View.VISIBLE: View.GONE);
        deleteButton.setOnClickListener(view -> showDeleteConfirmationDialog());

        minusButton.setEnabled(editTransactionItem.getQuantity()>1);
        minusButton.setOnClickListener(view -> {
            changeTransactionItemQuantity(
                    editTransactionItem,
                editTransactionItem.getQuantity()-1,
                product.getStocks()
            );
        });

        plusButton.setEnabled(editTransactionItem.getQuantity()<product.getStocks());
        plusButton.setOnClickListener(view ->
            changeTransactionItemQuantity(
                    editTransactionItem,
                editTransactionItem.getQuantity()+1,
                product.getStocks()
            )
        );

        quantityEdit.setFilters(new InputFilter[]{new InputFilterMinMax(1, product.getStocks())});
        quantityEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Clear focus when the "Done" key is pressed
                quantityEdit.clearFocus();
                ActivityUtils.hideKeyboard(v);
                return true;
            }
            return false;
        });
        quantityEdit.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus){
                return;
            }

            String newQuantityText = quantityEdit.getText().toString();
            if(StringUtils.isNullOrEmpty(newQuantityText)){
                // Reset Quantity Edit Text
                quantityEdit.setText(String.valueOf(editTransactionItem.getQuantity()));
                return;
            }

            int newQuantity = Integer.parseInt(newQuantityText);
            changeTransactionItemQuantity(editTransactionItem, newQuantity, product.getStocks());
        });

        btnSubmit.setOnClickListener(e->{
            if (!isListed){
                getParent().getTransactionItems().add(editTransactionItem);
                getParent().notifyDataSetChanged();
            }
            else {
                transactionItem.setQuantity(editTransactionItem.getQuantity());
                holder.container.setBackgroundColor(PALE_GREEN);
                holder.cartFrame.setVisibility(View.VISIBLE);
                holder.itemQuantityText.setText(String.valueOf(editTransactionItem.getQuantity()));
            }

            getParent().reloadViews();
            dismiss();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
            .setTitle("Remove Item?")
            .setMessage(String.format(
                    "Are you sure you want to remove \"%s\"?",
                    product.getName()
            ))
            .setPositiveButton("Yes", (dialog, which) -> {
                getParent().getTransactionItems().remove(transactionItem);
                getParent().notifyDataSetChanged();
                getParent().reloadViews();
                dismiss();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    private void changeTransactionItemQuantity(TransactionItem transactionItem, int newQuantity, int stocks){
        transactionItem.setQuantity(newQuantity);
        quantityEdit.setText(String.valueOf(newQuantity));
        minusButton.setEnabled(newQuantity>1);
        plusButton.setEnabled(newQuantity<stocks);
        subtotalPriceText.setText(StringUtils.formatToPeso(transactionItem.calculateAmount()));
    }

    PosPage getParent(){
        return (PosPage) activity;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.pid_dialog_dismiss) {
            dismiss();
        }
    }
}
