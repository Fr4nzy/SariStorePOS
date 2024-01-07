package com.projectfkklp.saristorepos.activities.store_finder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.adapters.StoreFinderPageAdapter;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.utils.TestingUtils;

import java.util.ArrayList;
import java.util.List;

public class StoreFinderPage extends AppCompatActivity {
    private List<Store> searchedStores;
    SearchView storeSearch;
    ProgressBar searchProgress;
    StoreFinderPageAdapter storeFinderPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_finder_page);

        initializedData();
        initializeViews();
        initializeRecyclerView();
    }

    private void initializedData() {
        searchedStores = new ArrayList<>();
    }

    private void initializeViews(){
        storeSearch = findViewById(R.id.store_search_view);
        searchProgress = findViewById(R.id.store_finder_search_progress);
        storeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextChange(String newText) {
                   if (newText.isEmpty()){
                       clearSearchStores();
                   }
                   return true;
               }

               @Override
               public boolean onQueryTextSubmit(String query) {
                   onSearch(query);
                   return true;
               }
           }
        );
    }

    private void initializeRecyclerView(){
        // Initialize views and set up RecyclerView
        RecyclerView storeSelectoreRecycler = findViewById(R.id.store_finder_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        storeSelectoreRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        storeFinderPageAdapter = new StoreFinderPageAdapter(this, searchedStores);
        storeSelectoreRecycler.setAdapter(storeFinderPageAdapter);
    }

    private void onSearch(String searchText){
        searchProgress.setVisibility(View.VISIBLE);

        TestingUtils.delay(1000, ()->{
            fetchDummySearchedStores();
            searchProgress.setVisibility(View.GONE);
        });
    }

    private void clearSearchStores(){
        searchedStores.clear();
        storeFinderPageAdapter.notifyDataSetChanged();
    }

    private void fetchDummySearchedStores() {
        String[] storeIds = new String[]{
                "17f1e065a4f001",
                "17f1e065a4f002",
                "17f1e065a4f003",
                "17f1e065a4f004",
                "17f1e065a4f005",
                "17f1e065a4f006",
                "17f1e065a4f007"
        };

        searchedStores.add(new Store(storeIds[0], "Store A", "Sitio Yakal, Brgy Malinao, Sta Cruz, Laguna"));
        searchedStores.add(new Store(storeIds[1], "Store B", "Sitio Manga, Brgy Malinao, Sta Cruz, Laguna"));
        searchedStores.add(new Store(storeIds[2], "Store C", "Sitio Yakal, Brgy Malinao, Lumban, Laguna"));
        searchedStores.add(new Store(storeIds[3], "Store D", "Sitio Yakal, Brgy Malinao, Pagsanjan, Laguna"));
        searchedStores.add(new Store(storeIds[4], "Store E", "Sitio Yakal, Brgy Malinao, Los Banos, Laguna"));
        searchedStores.add(new Store(storeIds[5], "Store F", "Sitio Yakal, Brgy Malinao, Calamba, Laguna"));
        searchedStores.add(new Store(storeIds[6], "Store G", "Sitio Yakal, Brgy Malinao, San Pablo City, Laguna"));

        storeFinderPageAdapter.notifyDataSetChanged();
    }

    public void navigateBack(View view){
        finish();
    }

}