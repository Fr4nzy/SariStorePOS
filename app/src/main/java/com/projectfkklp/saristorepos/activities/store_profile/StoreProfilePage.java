package com.projectfkklp.saristorepos.activities.store_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;

public class StoreProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_profile_page);
    }

    public void dismiss(View view){
        finish();
    }
}