package com.example.shop.Adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shop.Activities.CardActivity;
import com.example.shop.Activities.ProduitDetailActivity;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.io.File;
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

        loadImage(holder.productImage, article.getProduit().getImage1());

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


    private void loadImage(ImageView imageView, String imageSource) {
        if (imageSource == null || imageSource.isEmpty()) {
            imageView.setImageResource(R.drawable.image_not_found);
            imageView.setVisibility(View.GONE);
            Log.d(TAG, "Image source is null or empty for ImageView: " + imageView.getId());
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        Log.d(TAG, "Loading image: " + imageSource);

        // Handle drawable resources
        if (imageSource.startsWith("drawable://")) {
            String drawableName = imageSource.replace("drawable://", "");
            int resourceId = imageView.getContext().getResources().getIdentifier(
                    drawableName, "drawable", imageView.getContext().getPackageName());
            if (resourceId != 0) {
                Glide.with(imageView.getContext())
                        .load(resourceId)
                        .placeholder(R.drawable.notification_icon)
                        .error(R.drawable.image_not_found)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Failed to load drawable: " + drawableName + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                                imageView.setImageResource(R.drawable.image_not_found);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Drawable loaded successfully: " + drawableName);
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                Log.e(TAG, "Drawable resource not found: " + drawableName);
                imageView.setImageResource(R.drawable.image_not_found);
            }
            return;
        }

        // Handle URLs and URIs
        Object glideSource;
        if (imageSource.startsWith("http://") || imageSource.startsWith("https://")) {
            glideSource = imageSource;
        } else if (imageSource.startsWith("file://")) {
            glideSource = Uri.parse(imageSource);
        } else {
            // Assume it's a local file path (not recommended, but for backward compatibility)
            File file = new File(imageSource);
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: " + imageSource);
                imageView.setImageResource(R.drawable.image_not_found);
                return;
            }
            glideSource = Uri.fromFile(file);
        }

        Glide.with(imageView.getContext())
                .load(glideSource)
                .placeholder(R.drawable.notification_icon)
                .error(R.drawable.image_not_found)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Failed to load image: " + imageSource + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                        imageView.setImageResource(R.drawable.image_not_found);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully: " + imageSource);
                        return false;
                    }
                })
                .into(imageView);
    }
}