package com.example.shop.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shop.Models.Produits;
import com.example.shop.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    private List<Produits> produitsList;
    private Context context;
    private OnProductActionListener actionListener;

    public interface OnProductActionListener {
        void onEditProduct(Produits produit, int position);
        void onDeleteProduct(Produits produit, int position);
    }

    public ProduitAdapter(List<Produits> produitsList, Context context, OnProductActionListener listener) {
        this.produitsList = produitsList != null ? produitsList : new ArrayList<>();
        this.context = context;
        this.actionListener = listener;
    }

    public void updateData(List<Produits> newProduitsList) {
        produitsList.clear();
        if (newProduitsList != null) {
            produitsList.addAll(newProduitsList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produits produit = produitsList.get(position);
        Log.d("ProduitAdapter", "Produit ID: " + produit.getId() + ", Nom: " + produit.getNom());
        holder.nomTextView.setText(produit.getNom() != null ? produit.getNom() : "Sans nom");
        holder.descriptionTextView.setText(produit.getDescription() != null ? produit.getDescription() : "Aucune description");
        holder.prixTextView.setText(String.format("%.2f", produit.getPrix()));
        holder.quantiteTextView.setText(String.valueOf(produit.getQuantite()));

        loadImage(holder.imageView1, produit.getImage1());
        loadImage(holder.imageView2, produit.getImage2());
        loadImage(holder.imageView3, produit.getImage3());
        loadImage(holder.imageView4, produit.getImage4());

        holder.editButton.setOnClickListener(v -> {
            Log.d("ProduitAdapter", "Edit clicked for product ID: " + produit.getId());
            if (actionListener != null) {
                actionListener.onEditProduct(produit, position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            Log.d("ProduitAdapter", "Delete clicked for product ID: " + produit.getId());
            if (actionListener != null) {
                actionListener.onDeleteProduct(produit, position);
            }
        });
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            Log.d("ProduitAdapter", "Loading image: " + imageUrl);
            Object imageSource = imageUrl.startsWith("http") ? imageUrl : new File(imageUrl);
            if (!imageUrl.startsWith("http") && !new File(imageUrl).exists()) {
                Log.e("ProduitAdapter", "File does not exist: " + imageUrl);
                imageView.setVisibility(View.GONE);
                return;
            }
            Glide.with(context)
                    .load(imageSource)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.image_not_found)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ProduitAdapter", "Failed to load image: " + imageUrl + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                            imageView.setVisibility(View.VISIBLE); // Show error image
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("ProduitAdapter", "Image loaded successfully: " + imageUrl);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
            Log.d("ProduitAdapter", "Image URL is null or empty for ImageView: " + imageView.getId());
        }
    }

    @Override
    public int getItemCount() {
        return produitsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomTextView, descriptionTextView, prixTextView, quantiteTextView;
        ImageView imageView1, imageView2, imageView3, imageView4;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomTextView = itemView.findViewById(R.id.textViewNom);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            prixTextView = itemView.findViewById(R.id.textViewPrix);
            quantiteTextView = itemView.findViewById(R.id.textViewQuantite);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
            imageView4 = itemView.findViewById(R.id.imageView4);
            editButton = itemView.findViewById(R.id.produitEdit);
            deleteButton = itemView.findViewById(R.id.produitDelete);
        }
    }
}