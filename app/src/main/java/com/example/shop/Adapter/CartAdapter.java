package com.example.shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final List<ArticlePanier> cartItems;
    private final Context context;

    public CartAdapter(List<ArticlePanier> cartItems, Context context) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticlePanier item = cartItems.get(position);
        holder.textViewName.setText(item.getProduit().getNom() != null ? item.getProduit().getNom() : "Sans nom");
        holder.textViewPrice.setText(String.format("%.2f €", item.getPrixUnitaire()));
        holder.textViewQuantity.setText(String.valueOf(item.getQuantite()));

        // Charger l'image
        String imageUrl = item.getProduit().getImage1();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                if (imageUrl.startsWith("http")) {
                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.notification_icon)
                            .error(R.drawable.image_not_found)
                            .into(holder.imageViewProduct);
                } else if (imageUrl.matches("\\d+")) {
                    Glide.with(context)
                            .load(Integer.parseInt(imageUrl))
                            .placeholder(R.drawable.notification_icon)
                            .error(R.drawable.image_not_found)
                            .into(holder.imageViewProduct);
                } else {
                    holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
                }
            } catch (Exception e) {
                holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
            }
        } else {
            holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewName, textViewPrice, textViewQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
        }
    }
}