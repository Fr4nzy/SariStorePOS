package com.projectfkklp.saristorepos.activities.store_recruitment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.repositories.UserStoreRelationRepository;
import com.projectfkklp.saristorepos.utils.RepositoryUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRecruitmentPage extends AppCompatActivity {
    private List<User> searchedUsers;
    SearchView userSearch;

    FrameLayout emptySearchFrame;
    ProgressBar searchProgress;
    UserRecruitmentAdapter userRecruitmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_recruitment_page);

        initializedData();
        initializeViews();
        initializeRecyclerView();
    }

    private void initializedData() {
        searchedUsers = new ArrayList<>();
    }

    private void initializeViews(){
        userSearch = findViewById(R.id.user_search_view);
        searchProgress = findViewById(R.id.user_recruitment_search_progress);
        emptySearchFrame = findViewById(R.id.user_recruitment_empty_search_frame);
        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
       @Override
       public boolean onQueryTextChange(String newText) {
           if (newText.isEmpty()){
               clearSearchUsers();
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
        RecyclerView userSelectorRecycler = findViewById(R.id.user_recruitment_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userSelectorRecycler.setLayoutManager(layoutManager);

        userRecruitmentAdapter = new UserRecruitmentAdapter(this, searchedUsers);
        userSelectorRecycler.setAdapter(userRecruitmentAdapter);
    }

    private void onSearch(String searchText){
        searchProgress.setVisibility(View.VISIBLE);
        emptySearchFrame.setVisibility(View.GONE);

        // Before proceeding in searching users,
        // fetch first the associated relations
        // to filter out non associated users to the current user
        UserStoreRelationRepository
                .getRelationsByUserId(SessionRepository.getCurrentUser(this).getId())
                .continueWithTask(task -> {
                    List<UserStoreRelation> userStoreRelations = task.getResult().toObjects(UserStoreRelation.class);
                    List<String> associatedUserIds = userStoreRelations.stream()
                            .map(UserStoreRelation::getUserId)
                            .collect(Collectors.toList());

                    return searchUsers(searchText, associatedUserIds);
                })
                .addOnSuccessListener(results->{
                    searchedUsers.clear();

                    List<Object> toMergeResults = results.subList(0, 3);
                    QuerySnapshot associatedUsersResult = (QuerySnapshot) results.get(3);
                    List<User> associatedUsers = associatedUsersResult != null
                            ? associatedUsersResult.toObjects(User.class)
                            : new ArrayList<>();

                    for (DocumentSnapshot document : RepositoryUtils.mergeResults(toMergeResults)) {
                        User user = document.toObject(User.class);

                        boolean isAssociated = associatedUsers.stream()
                                .anyMatch(associatedUser -> associatedUser.getId().equals(user.getId()));

                        // If not associated, add it to searchedUsers
                        if (!isAssociated) {
                            searchedUsers.add(user);
                        }
                    }

                    emptySearchFrame.setVisibility(searchedUsers.isEmpty() ? View.VISIBLE: View.GONE);
                    userRecruitmentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
                .addOnCompleteListener(task-> searchProgress.setVisibility(View.GONE))
        ;
    }

    public Task<List<Object>> searchUsers(String searchText, List<String> userToExcludeIds){
        return UserRepository.searchUsers(searchText, userToExcludeIds);
    }

    private void clearSearchUsers(){
        emptySearchFrame.setVisibility(View.VISIBLE);
        searchedUsers.clear();
        userRecruitmentAdapter.notifyDataSetChanged();
    }

    public void navigateBack(View view){
        finish();
    }

    public void research() {
        onSearch(userSearch.getQuery().toString().toUpperCase());
    }

}