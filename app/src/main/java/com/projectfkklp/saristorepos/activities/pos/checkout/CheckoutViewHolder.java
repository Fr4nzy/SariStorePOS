package com.projectfkklp.saristorepos.activities.pos.checkout;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.utils.ActivityUtils;


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

        productImage = itemView.findViewById(R.id.checkout_product_image);
        productNameText = itemView.findViewById(R.id.checkout_product_name);
        unitPriceText = itemView.findViewById(R.id.checkout_unit_price);
        quantityEdit = itemView.findViewById(R.id.checkout_quantity_edit);
        subtotalPriceText = itemView.findViewById(R.id.checkout_subtotal_price);
        leftItemText = itemView.findViewById(R.id.checkout_left_item);
        deleteBtn = itemView.findViewById(R.id.checkout_btn_delete);
        minusBtn = itemView.findViewById(R.id.checkout_btn_minus);
        plusButton = itemView.findViewById(R.id.checkout_btn_plus);

        quantityEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Clear focus when the "Done" key is pressed
                quantityEdit.clearFocus();
                ActivityUtils.hideKeyboard(v);
                return true;
            }
            return false;
        });

    }

}