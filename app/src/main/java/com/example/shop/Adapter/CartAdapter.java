package com.example.shop.Adapter;

import android.content.Intent;
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
import com.example.shop.Activities.CardActivity;
import com.example.shop.Activities.ProduitDetailActivity;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";
    private List<ArticlePanier> cartItems;
    private DBHelper dbHelper;

    public CartAdapter(List<ArticlePanier> cartItems, DBHelper dbHelper) {
        this.cartItems = cartItems;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_carts, parent, false);
            return new CartViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating item_carts.xml: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ArticlePanier article = cartItems.get(position);
        if (holder.productName == null || holder.productPrice == null || holder.quantity == null) {
            Log.e(TAG, "One or more TextViews are null in CartViewHolder");
            return;
        }
        if (article.getProduit() == null) {
            Log.e(TAG, "Produit is null for article at position: " + position);
            holder.productName.setText("Produit inconnu");
            holder.productPrice.setText("0.00 €");
            holder.quantity.setText("Quantité: " + article.getQuantite());
            return;
        }
        holder.productName.setText(article.getProduit().getNom());
        holder.productPrice.setText(String.format("%.2f €", article.getPrixUnitaire()));
        holder.quantity.setText("Quantité: " + article.getQuantite());

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
            if (holder.itemView.getContext() instanceof CardActivity) {
                ((CardActivity) holder.itemView.getContext()).updateCartSummary();
            }
        });

        holder.btnDetails.setOnClickListener(v -> {
            if (article.getProduit() != null) {
                Intent intent = new Intent(holder.itemView.getContext(), ProduitDetailActivity.class);
                intent.putExtra("product_id", article.getProduit().getId());
                Log.d(TAG, "Navigating to ProductDetailActivity with product_id: " + article.getProduit().getId());
                holder.itemView.getContext().startActivity(intent);
            } else {
                Log.e(TAG, "Cannot navigate to ProductDetailActivity: Produit is null");
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
        Button btnRemove, btnDetails;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnDetails = itemView.findViewById(R.id.btn_details);

            Log.d(TAG, "CartViewHolder initialized - productImage: " + (productImage != null));
            Log.d(TAG, "CartViewHolder initialized - productName: " + (productName != null));
            Log.d(TAG, "CartViewHolder initialized - productPrice: " + (productPrice != null));
            Log.d(TAG, "CartViewHolder initialized - quantity: " + (quantity != null));
            Log.d(TAG, "CartViewHolder initialized - btnRemove: " + (btnRemove != null));
            Log.d(TAG, "CartViewHolder initialized - btnDetails: " + (btnDetails != null));
        }
    }
}