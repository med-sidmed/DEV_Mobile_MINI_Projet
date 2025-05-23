package com.example.shop.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<ArticlePanier> cartItems;
    private DBHelper dbHelper;

    public CartAdapter(List<ArticlePanier> cartItems, DBHelper dbHelper) {
        this.cartItems = cartItems;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carts, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ArticlePanier article = cartItems.get(position);
        if (holder.productName == null || holder.productPrice == null || holder.quantity == null) {
            Log.e("CartAdapter", "One or more TextViews are null in CartViewHolder");
            return;
        }
        holder.productName.setText(article.getProduit().getNom());
        holder.productPrice.setText(String.format("%.2f €", article.getPrixUnitaire()));
        holder.quantity.setText(String.valueOf(article.getQuantite()));

        String imagePath = article.getProduit().getImage1();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagePath)
                    .placeholder(R.drawable.image_not_found)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.image_head);
        }

        holder.btnRemove.setOnClickListener(v -> {
            dbHelper.supprimerArticlePanier(article.getId());
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (holder.itemView.getContext() instanceof com.example.shop.Activities.CardActivity) {
                ((com.example.shop.Activities.CardActivity) holder.itemView.getContext()).updateCartSummary();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantity;
        Button btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);

            // Log to verify initialization
            Log.d("CartViewHolder", "productImage: " + (productImage != null));
            Log.d("CartViewHolder", "productName: " + (productName != null));
            Log.d("CartViewHolder", "productPrice: " + (productPrice != null));
            Log.d("CartViewHolder", "quantity: " + (quantity != null));
            Log.d("CartViewHolder", "btnRemove: " + (btnRemove != null));
        }
    }
}