package com.example.shop.Activities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shop.Models.Produits;
import com.example.shop.R;
import java.util.ArrayList;
import java.util.List;

public class ProductMainAdapter extends RecyclerView.Adapter<ProductMainAdapter.ViewHolder> {

    private List<Produits> produitsList;
    private Context context;
    private OnWishlistClickListener wishlistListener;

    public interface OnWishlistClickListener {
        void onWishlistClick(Produits produit, boolean isAdded);
    }

    public ProductMainAdapter(List<Produits> produitsList, Context context, OnWishlistClickListener listener) {
        this.produitsList = produitsList != null ? produitsList : new ArrayList<>();
        this.context = context;
        this.wishlistListener = listener;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produits produit = produitsList.get(position);

        holder.textViewName.setText(produit.getNom() != null ? produit.getNom() : "Sans nom");
        holder.textViewPrice.setText(String.format("%.2f €", produit.getPrix()));
        holder.textViewRating.setText("4.6"); // À remplacer par un champ réel si disponible
        holder.textViewViews.setText("12"); // À remplacer par un champ réel si disponible

        // Charger l'image principale (image1) avec Glide
        if (produit.getImage1() != null && !produit.getImage1().isEmpty()) {
            Glide.with(context)
                    .load(produit.getImage1())
                    .placeholder(R.drawable.notification_icon)
                    .error(R.drawable.image_not_found)
                    .into(holder.imageViewProduct);
        } else {
            holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
        }

        // Gérer l'état du bouton Wishlist
        boolean isInWishlist = produit.isActive(); // À implémenter dans Produits si nécessaire
        holder.btnWishlist.setImageResource(isInWishlist ? R.drawable.wishlist : R.drawable.ic_heart_outline);
        holder.btnWishlist.setTag(isInWishlist);

        holder.btnWishlist.setOnClickListener(v -> {
            boolean currentState = (boolean) holder.btnWishlist.getTag();
            boolean newState = !currentState;
            holder.btnWishlist.setImageResource(newState ? R.drawable.wishlist : R.drawable.ic_heart_outline);
            holder.btnWishlist.setTag(newState);
            if (wishlistListener != null) {
                wishlistListener.onWishlistClick(produit, newState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return produitsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice, textViewRating, textViewViews;
        ImageView imageViewProduct, btnWishlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewViews = itemView.findViewById(R.id.textViewViews);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
        }
    }
}