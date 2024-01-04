package com.projectfkklp.saristorepos.activities.entry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.projectfkklp.saristorepos.R;

public class EntryPage extends AppCompatActivity {

    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_page);

        iconImageView = findViewById(R.id.iconImageView);

        // Add the ripple effect when the activity starts
        addRippleEffect();
    }

    private void addRippleEffect() {
        // Set up the scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(500); // Adjust the duration as needed
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);

        // Start the animation
        iconImageView.startAnimation(scaleAnimation);
    }
}