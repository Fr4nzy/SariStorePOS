package com.projectfkklp.saristorepos.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfileRecyclerDialog;
import com.projectfkklp.saristorepos.models.Store;

import java.util.List;

public class StoreFinderPageAdapter extends RecyclerView.Adapter<StoreFinderPageAdapter.StoreFinderViewHolder> {

    private Context context;
    private List<Store> searchedStores;

    public StoreFinderPageAdapter(Context context, List<Store> stores) {
        this.context = context;
        this.searchedStores = stores;
    }

    @NonNull
    @Override
    public StoreFinderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_finder_recycler_view, parent, false);
        return new StoreFinderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreFinderViewHolder holder, int position) {
        Store searchedStore = searchedStores.get(position);

        holder.storeNameText.setText(searchedStore.getName());
        holder.storeAddressText.setText(searchedStore.getAddress());
        holder.storeIdText.setText(searchedStore.getId());

        holder.cardView.setOnClickListener(l->{
            UserProfileRecyclerDialog cdd = new UserProfileRecyclerDialog((Activity) context);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });
    }

    @Override
    public int getItemCount() {
        return searchedStores.size();
    }

    static class StoreFinderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        TextView storeNameText;
        TextView storeAddressText;
        TextView storeIdText;

        public StoreFinderViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.store_finder_store_card);
            storeNameText = itemView.findViewById(R.id.store_finder_store_name);
            storeAddressText = itemView.findViewById(R.id.store_finder_store_address);
            storeIdText = itemView.findViewById(R.id.store_finder_store_id);
        }
    }
}
