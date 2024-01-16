package com.projectfkklp.saristorepos.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.projectfkklp.saristorepos.R;

import java.util.List;

public class ErrorAlert extends CardView {
    private TextView contentTextView;
    private TextView closeButton;

    public ErrorAlert(Context context) {
        super(context);
        init();
    }

    public ErrorAlert(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ErrorAlert(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.views_error_alert, this, true);
        setVisibility(View.GONE);

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
