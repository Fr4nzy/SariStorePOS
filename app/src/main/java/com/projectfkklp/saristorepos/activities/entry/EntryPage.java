package com.projectfkklp.saristorepos.activities.entry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardPage;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user.UserLoginPage;
import com.projectfkklp.saristorepos.repositories.SessionRepository;

public class EntryPage extends AppCompatActivity {

    final Context context = this;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_page);

        iconImageView = findViewById(R.id.iconImageView);

        // Add the ripple effect when the activity starts
        addRippleEffect();

        checkSessions();
    }

    private void checkSessions() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SessionRepository.getCurrentUser(context) == null) {
                    // User session not exists, go to login page
                    navigateTo(UserLoginPage.class);
                } else if (SessionRepository.getCurrentStore(context) == null) {
                    // Store session not exists, go to store selector page
                    navigateTo(StoreSelectorPage.class);
                } else {
                    // Both user and store sessions exist, go to dashboard
                    navigateTo(DashboardPage.class);
                }
            }
        }, 1000);
    }

    private void navigateTo(Class<?> destinationClass) {
        Intent intent = new Intent(this, destinationClass);
        startActivity(intent);
        finish(); // Prevent going back to EntryActivity
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