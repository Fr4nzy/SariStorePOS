package com.projectfkklp.saristorepos.activities.entry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardPage;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user_login.UserLoginPage;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.utils.ActivityUtils;

public class EntryPage extends AppCompatActivity {

    final Activity activity = this;
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
        new Handler().postDelayed(() -> {
            if (SessionRepository.getCurrentUser(activity) == null) {
                // User session not exists, go to login page
                ActivityUtils.navigateTo( activity, UserLoginPage.class);
            } else if (SessionRepository.getCurrentStore(activity) == null) {
                // Store session not exists, go to store selector page
                ActivityUtils.navigateTo(activity, StoreSelectorPage.class);
            } else {
                // Both user and store sessions exist, go to dashboard
                ActivityUtils.navigateTo(activity, DashboardPage.class);
            }
        }, 1000);
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