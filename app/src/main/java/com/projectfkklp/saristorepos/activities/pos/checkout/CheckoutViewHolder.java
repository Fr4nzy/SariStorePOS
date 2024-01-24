package com.projectfkklp.saristorepos.activities.pos.checkout;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.projectfkklp.saristorepos.R;


public class CheckoutViewHolder extends RecyclerView.ViewHolder {
    ShapeableImageView productImage;
    TextView productNameText;
    TextView unitPriceText;
    public EditText quantityEdit;
    TextView subtotalPriceText;
    TextView leftItemText;
    ImageButton deleteBtn;
    ImageButton minusBtn;
    public ImageButton plusButton;

    public CheckoutViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.pos_product_image);
        productNameText = itemView.findViewById(R.id.pos_product_name);
        unitPriceText = itemView.findViewById(R.id.pos_unit_price);
        quantityEdit = itemView.findViewById(R.id.pos_quantity_edit);
        subtotalPriceText = itemView.findViewById(R.id.pos_subtotal_price);
        leftItemText = itemView.findViewById(R.id.pos_left_item);
        deleteBtn = itemView.findViewById(R.id.pos_btn_delete);
        minusBtn = itemView.findViewById(R.id.pos_btn_minus);
        plusButton = itemView.findViewById(R.id.pos_btn_plus);

        quantityEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Clear focus when the "Done" key is pressed
                quantityEdit.clearFocus();
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}