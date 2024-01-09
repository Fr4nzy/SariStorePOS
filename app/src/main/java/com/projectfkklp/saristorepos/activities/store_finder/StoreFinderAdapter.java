package com.projectfkklp.saristorepos.activities.store_finder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Store;

import java.util.List;

public class StoreFinderAdapter extends RecyclerView.Adapter<StoreFinderRecycler> {

    private Context context;
    private List<Store> searchedStores;

    public StoreFinderAdapter(Context context, List<Store> stores) {
        this.context = context;
        this.searchedStores = stores;
    }

    @NonNull
    @Override
    public StoreFinderRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_finder_recycler_view, parent, false);
        return new StoreFinderRecycler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreFinderRecycler holder, int position) {
        Store searchedStore = searchedStores.get(position);

        holder.storeNameText.setText(searchedStore.getName());
        holder.storeAddressText.setText(searchedStore.getAddress());
        holder.storeIdText.setText(searchedStore.getId());

        holder.cardView.setOnClickListener(l->{
            StoreFinderRecyclerDialog cdd = new StoreFinderRecyclerDialog((Activity) context, searchedStore);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });
    }

    @Override
    public int getItemCount() {
        return searchedStores.size();
    }

}
