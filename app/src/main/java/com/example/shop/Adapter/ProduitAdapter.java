package com.example.shop.Adapter;

import android.content.Context;
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
import com.example.shop.Models.Produits;
import com.example.shop.R;
import java.util.ArrayList;
import java.util.List;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    private List<Produits> produitsList;
    private Context context;
    private OnProductActionListener actionListener;

    // Interface pour les callbacks
    public interface OnProductActionListener {
        void onEditProduct(Produits produit, int position);
        void onDeleteProduct(Produits produit, int position);
    }

    public ProduitAdapter(List<Produits> produitsList, Context context, OnProductActionListener listener) {
        this.produitsList = produitsList != null ? produitsList : new ArrayList<>();
        this.context = context;
        this.actionListener = listener;
    }

    // Méthode pour mettre à jour les données
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
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.image_not_found)
                    .into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
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