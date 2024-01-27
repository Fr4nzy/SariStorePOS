package com.projectfkklp.saristorepos.activities.store_selector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_finder.StoreFinderPage;
import com.projectfkklp.saristorepos.activities.store_registration.StoreRegistrationPage;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfilePage;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.repositories.UserStoreRelationRepository;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StoreSelectorPage extends AppCompatActivity {
    ProgressBar loadingProgressBar;
    FrameLayout emptyFrame;
    TextView userNameText;
    RecyclerView storeSelectorRecycler;
    StoreSelectorAdapter storeSelectorPageAdapter;
    private User user;
    private List<UserStoreRelation> userStoreRelations;
    private List<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_selector_page);

        user = SessionRepository.getCurrentUser(this);
        initializeViews();
    }

    @Override
    protected void onStart() {
        // we use OnStart lice cycle
        // to reload (re-fetch from firebase) the associated stores
        // specially, when we pause this activity
        // e.g. we go to StoreFinderPage, and get back to this page
        // So the changes made can reflect here
        super.onStart();

        loadAssociatedStores();
    }

    private void loadAssociatedStores(){
        // Fetch Associated Stores from Firebase
        // Then, update recycler view
        loadingProgressBar.setVisibility(View.VISIBLE);
        UserStoreRelationRepository
            .getRelationsByUserId(user.getId())
            .continueWithTask(task->{
                // Fetch userStoreRelations from the task
                userStoreRelations = task.getResult().toObjects(UserStoreRelation.class);
                sortUserStoreRelations();

                // extract store ids
                List<String> storeIds = userStoreRelations.stream()
                        .map(UserStoreRelation::getStoreId)
                        .collect(Collectors.toList());

                // continue by fetching associated stores using extracted store ids
                return StoreRepository.getStoresByIds(storeIds);
            })
            .addOnSuccessListener(successTask->{
                stores = successTask != null
                    ? successTask.toObjects(Store.class)
                    : new ArrayList<>();
                initializeRecyclerView();
                emptyFrame.setVisibility(stores.isEmpty()? View.VISIBLE: View.GONE);
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> loadingProgressBar.setVisibility(View.GONE))
        ;
    }

    private  void initializeViews(){
        userNameText = findViewById(R.id.store_selector_page_user_name);
        storeSelectorRecycler = findViewById(R.id.store_selector_recycler);
        loadingProgressBar = findViewById(R.id.store_selector_page_progress);
        emptyFrame = findViewById(R.id.store_selector_empty_frame);

        userNameText.setText(user.getName());
    }

    private void sortUserStoreRelations(){
        userStoreRelations.sort(Comparator.comparing(UserStoreRelation::getStatus));
    }

    private void initializeRecyclerView(){
        // If no stores to display,
        // Then display empty fragment frame
        // And no need to setup recycler view
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        storeSelectorRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        storeSelectorPageAdapter = new StoreSelectorAdapter(this, userStoreRelations, stores);
        storeSelectorRecycler.setAdapter(storeSelectorPageAdapter);
    }

    public void gotoUserProfilePage(View view){
        startActivity(new Intent(this, UserProfilePage.class));
    }

    public void gotoStoreFinderPage(View view){
        startActivity(new Intent(this, StoreFinderPage.class));
    }

    public void gotoStoreRegistrationPage(View view){
        startActivity(new Intent(this, StoreRegistrationPage.class));
    }

}