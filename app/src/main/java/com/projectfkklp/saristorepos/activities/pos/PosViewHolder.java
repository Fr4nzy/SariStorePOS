package com.projectfkklp.saristorepos.activities.pos;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;


public class PosViewHolder extends RecyclerView.ViewHolder {
    EditText quantityEdit;
    public PosViewHolder(@NonNull View itemView) {
        super(itemView);

        quantityEdit = itemView.findViewById(R.id.pos_quantity_edit);

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