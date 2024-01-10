package com.projectfkklp.saristorepos.activities.store_selector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_finder.StoreFinderPage;
import com.projectfkklp.saristorepos.activities.store_registration.StoreRegistrationPage;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfilePage;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.models.Store;

import java.util.ArrayList;
import java.util.Comparator;

public class StoreSelectorPage extends AppCompatActivity {
    StoreSelectorPageAdapter storeSelectorPageAdapter;

    private ArrayList<UserStoreRelation> userStoreRelations;
    private ArrayList<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_selector_page);

        initializeDummyItems();
        sortUserStoreRelations();
        initializeRecyclerView();
    }

    private void initializeDummyItems(){
        String[] storeIds = new String[]{
            "17f1e065a4f001",
            "17f1e065a4f002",
            "17f1e065a4f003",
            "17f1e065a4f004",
            "17f1e065a4f005",
            "17f1e065a4f006",
            "17f1e065a4f007"
        };
        userStoreRelations = new ArrayList<>();
        stores = new ArrayList<>();

        userStoreRelations.add(new UserStoreRelation("", storeIds[0], UserRole.ASSISTANT, UserStatus.ACTIVE));
        stores.add(new Store(storeIds[0], "Store A"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[1], UserRole.ASSISTANT, UserStatus.INVITED));
        stores.add(new Store(storeIds[1], "Store B"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[2], UserRole.ASSISTANT, UserStatus.REQUESTED));
        stores.add(new Store(storeIds[2], "Store C"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[3], UserRole.OWNER, UserStatus.ACTIVE));
        stores.add(new Store(storeIds[3], "Store D"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[4], UserRole.OWNER, UserStatus.INVITED));
        stores.add(new Store(storeIds[4], "Store E"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[5], UserRole.OWNER, UserStatus.REQUESTED));
        stores.add(new Store(storeIds[5], "Store F"));

        userStoreRelations.add(new UserStoreRelation("", storeIds[6], UserRole.OWNER, UserStatus.ACTIVE));
        stores.add(new Store(storeIds[6], "Store G"));
    }

    private void sortUserStoreRelations(){
        userStoreRelations.sort(Comparator.comparing(UserStoreRelation::getStatus));
    }

    private void initializeRecyclerView(){
        // Initialize views and set up RecyclerView
        RecyclerView storeSelectoreRecycler = findViewById(R.id.store_selector_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        storeSelectoreRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        storeSelectorPageAdapter = new StoreSelectorPageAdapter(this, userStoreRelations, stores);
        storeSelectoreRecycler.setAdapter(storeSelectorPageAdapter);
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