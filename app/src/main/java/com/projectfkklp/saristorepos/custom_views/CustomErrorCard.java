package com.projectfkklp.saristorepos.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.projectfkklp.saristorepos.R;

import java.util.List;

public class CustomErrorCard extends CardView {
    private TextView contentTextView;
    private TextView closeButton;

    public CustomErrorCard(Context context) {
        super(context);
        init();
    }

    public CustomErrorCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomErrorCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_error_card, this, true);

        contentTextView = findViewById(R.id.custom_error_content);
        closeButton = findViewById(R.id.custom_error_close);

        closeButton.setOnClickListener(view -> {
            setVisibility(View.GONE); // Hide the ErrorCardView on close button click
        });
    }

    public void setErrors(List<String> errors) {
        String content = String.join("\n", errors);
        contentTextView.setText(content);
        setVisibility(VISIBLE);
    }
}
