package com.example.shop.Activities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;
import java.util.ArrayList;
import java.util.List;

public class ProductMainAdapter extends RecyclerView.Adapter<ProductMainAdapter.ViewHolder> {

    private List<Produits> produitsList;
    private Context context;
    private OnWishlistClickListener wishlistListener;
    private OnProductClickListener productClickListener;
    private int currentUserId;
    private DBHelper dbHelper;

    public ProductMainAdapter(List<Produits> produitsList, ProductsByCategoryActivity context, ProductsByCategoryActivity wishlistListener) {
        this.produitsList=produitsList;
        this.context=context;
        this.wishlistListener=wishlistListener;
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Produits produit, boolean isAdded);
    }

    public interface OnProductClickListener {
        void onProductClick(Produits produit);
    }

    public ProductMainAdapter(List<Produits> produitsList, Context context,
                              OnWishlistClickListener wishlistListener,
                              OnProductClickListener productClickListener, int currentUserId) {
        this.produitsList = produitsList != null ? produitsList : new ArrayList<>();
        this.context = context;
        this.wishlistListener = wishlistListener;
        this.productClickListener = productClickListener;
        this.currentUserId = currentUserId;
        this.dbHelper = new DBHelper(context);
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
        String imageSource = produit.getImage1();
        if (imageSource != null && !imageSource.isEmpty()) {
            if (imageSource.startsWith("drawable://")) {
                // Extraire le nom du drawable
                String drawableName = imageSource.replace("drawable://", "");
                int resourceId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());

                if (resourceId != 0) {
                    Glide.with(context)
                            .load(resourceId)
                            .placeholder(R.drawable.notification_icon)  // image de chargement
                            .error(R.drawable.image_not_found)         // image d'erreur
                            .into(holder.imageViewProduct);
                } else {
                    Log.e("ProduitAdapter", "Ressource drawable non trouvée : " + drawableName);
                    holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
                }
            } else {
                // Charger à partir d'une URL ou d'un fichier local (file://)
                Uri imageUri = imageSource.startsWith("file://") ? Uri.parse(imageSource) : Uri.parse(imageSource);
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.notification_icon)
                        .error(R.drawable.image_not_found)
                        .into(holder.imageViewProduct);
            }
        } else {
            // Image vide ou null
            holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
        }


        // Vérifier si le produit est dans les favoris
        boolean isInWishlist = currentUserId != -1 && dbHelper.isFavorite(currentUserId, produit.getId());
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

        // Gérer le clic sur l'item
        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(produit);
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