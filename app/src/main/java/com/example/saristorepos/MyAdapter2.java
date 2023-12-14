package com.example.saristorepos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;
    private OnItemClickListener onItemClickListener; // Declare the listener

    public MyAdapter2(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewer2, parent, false);
        return new MyViewHolder(view, onItemClickListener, dataList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass currentItem = dataList.get(position);

        // Use Glide to load the image
        Glide.with(context).load(currentItem.getDataImage()).into(holder.rec2Image);

        // Set text values
        holder.rec2Product.setText(currentItem.getDataProduct());
        holder.rec2Price.setText(String.valueOf(currentItem.getDataPrice()));
        holder.rec2Stock.setText(String.valueOf(currentItem.getDataStock()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(DataClass data);
    }

    // Setter for click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView rec2Image, cartImageView;
        TextView rec2Product, rec2Price, rec2Stock;
        CardView rec2Card;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener, List<DataClass> dataList) {
            super(itemView);

            // Initialize views
            rec2Image = itemView.findViewById(R.id.rec2Image);
            rec2Card = itemView.findViewById(R.id.rec2Card);
            rec2Price = itemView.findViewById(R.id.rec2Price);
            rec2Stock = itemView.findViewById(R.id.rec2Stock);
            rec2Product = itemView.findViewById(R.id.rec2Product);
            cartImageView = itemView.findViewById(R.id.cartImageView);

            // Set click listener for cartImageView
            cartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If onItemClickListener is set, trigger its onItemClick method
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(dataList.get(position));
                        }
                    }
                }
            });

            // Set click listener for rec2Card or other views if needed
            rec2Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click on the card view if needed
                }
            });

            // Add more click listeners for other views as needed
        }
    }
}
