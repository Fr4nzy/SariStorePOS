package com.projectfkklp.saristorepos.activities.store_finder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.repositories.UserStoreRelationRepository;
import com.projectfkklp.saristorepos.utils.RepositoryUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreFinderPage extends AppCompatActivity {
    private List<Store> searchedStores;
    SearchView storeSearch;

    FrameLayout emptySearchFrame;
    ProgressBar searchProgress;
    StoreFinderAdapter storeFinderPageAdapter;

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
        emptySearchFrame = findViewById(R.id.store_finder_empty_search_frame);
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
                   onSearch(query.toUpperCase());
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
        storeFinderPageAdapter = new StoreFinderAdapter(this, searchedStores);
        storeSelectoreRecycler.setAdapter(storeFinderPageAdapter);
    }

    private void onSearch(String searchText){
        searchProgress.setVisibility(View.VISIBLE);
        emptySearchFrame.setVisibility(View.GONE);

        // Before proceeding in searching stores,
        // fetch first the associated relations
        // to filter out non associated stores to the current user
        UserStoreRelationRepository
            .getRelationsByUserId(SessionRepository.getCurrentUser(this).getId())
            .continueWithTask(task -> {
                List<UserStoreRelation> userStoreRelations = task.getResult().toObjects(UserStoreRelation.class);
                List<String> associatedStoreIds = userStoreRelations.stream()
                    .map(UserStoreRelation::getStoreId)
                    .collect(Collectors.toList());

                return searchStores(searchText, associatedStoreIds);
            })
            .addOnSuccessListener(results->{
                searchedStores.clear();

                List<Object> toMergeResults = results.subList(0, 3);
                QuerySnapshot associatedStoresResult = (QuerySnapshot) results.get(3);
                List<Store> associatedStores = associatedStoresResult != null
                    ? associatedStoresResult.toObjects(Store.class)
                    : new ArrayList<>();

                for (DocumentSnapshot document : RepositoryUtils.mergeResults(toMergeResults)) {
                    Store store = document.toObject(Store.class);

                    boolean isAssociated = associatedStores.stream()
                            .anyMatch(associatedStore -> associatedStore.getId().equals(store.getId()));

                    // If not associated, add it to searchedStores
                    if (!isAssociated) {
                        searchedStores.add(store);
                    }
                }

                storeFinderPageAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task->{
                searchProgress.setVisibility(View.GONE);

                if (searchedStores.isEmpty()){
                    emptySearchFrame.setVisibility(View.VISIBLE);
                }
            })
        ;
    }

    public Task<List<Object>> searchStores(String searchText, List<String> storeToExcludeIds){
        return StoreRepository.searchStores(searchText, storeToExcludeIds);
    }

    private void clearSearchStores(){
        emptySearchFrame.setVisibility(View.VISIBLE);
        searchedStores.clear();
        storeFinderPageAdapter.notifyDataSetChanged();
    }

    public void navigateBack(View view){
        finish();
    }

    public void research() {
        onSearch(storeSearch.getQuery().toString().toUpperCase());
    }
}