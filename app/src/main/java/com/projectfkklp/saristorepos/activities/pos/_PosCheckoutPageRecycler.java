package com.projectfkklp.saristorepos.activities.pos;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

class _PosCheckoutPageRecycler extends RecyclerView.ViewHolder {
    TextView cartProduct, cartPrice, quantityTextView;
    Button btnMinus, btnPlus;

    public _PosCheckoutPageRecycler(@NonNull View itemView) {
        super(itemView);

        cartProduct = itemView.findViewById(R.id.rec2Product);
        cartPrice = itemView.findViewById(R.id.recPrice);
        quantityTextView = itemView.findViewById(R.id.quantityTextView);
        btnMinus = itemView.findViewById(R.id._pos_btn_minus);
        btnPlus = itemView.findViewById(R.id._pos_btn_plus);
    }
}
