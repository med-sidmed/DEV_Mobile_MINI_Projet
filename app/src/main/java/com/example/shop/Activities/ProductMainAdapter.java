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
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;
import androidx.recyclerview.widget.DiffUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductMainAdapter extends RecyclerView.Adapter<ProductMainAdapter.ViewHolder> {

    private List<Produits> produitsList;
    private Context context;
    private OnWishlistClickListener wishlistListener;
    private OnProductClickListener productClickListener;
    private int currentUserId;
    private Set<Integer> favoriteIds; // Optimization: Use Set for O(1) lookup during scroll

    public ProductMainAdapter(List<Produits> produitsList, ProductsByCategoryActivity context,
            ProductsByCategoryActivity wishlistListener) {
        this.produitsList = produitsList;
        this.context = context;
        this.wishlistListener = wishlistListener;
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Produits produit, boolean isAdded);
    }

    public interface OnProductClickListener {
        void onProductClick(Produits produit);
    }

    public ProductMainAdapter(List<Produits> produitsList, Context context,
            OnWishlistClickListener wishlistListener,
            OnProductClickListener productClickListener,
            int currentUserId,
            Set<Integer> favoriteIds) {
        this.produitsList = produitsList != null ? new ArrayList<>(produitsList) : new ArrayList<>();
        this.context = context;
        this.wishlistListener = wishlistListener;
        this.productClickListener = productClickListener;
        this.currentUserId = currentUserId;
        this.favoriteIds = favoriteIds != null ? favoriteIds : new HashSet<>();
    }

    public void updateData(List<Produits> newProduitsList) {
        DiffUtil.DiffResult diffResult = DiffUtil
                .calculateDiff(new ProductDiffCallback(this.produitsList, newProduitsList));
        this.produitsList.clear();
        if (newProduitsList != null) {
            this.produitsList.addAll(newProduitsList);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateFavoriteIds(Set<Integer> newFavoriteIds) {
        this.favoriteIds = newFavoriteIds != null ? newFavoriteIds : new HashSet<>();
        notifyDataSetChanged(); // Still needed for refresh when favorites change globally
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

        // Charger l'image principale (image1) avec Glide optimisé
        if (produit.getImage1() != null && !produit.getImage1().isEmpty()) {
            Glide.with(context)
                    .load(produit.getImage1())
                    .placeholder(R.drawable.notification_icon)
                    .error(R.drawable.image_not_found)
                    .centerCrop() // Optimization: uniform aspect ratio
                    .override(400, 400) // Optimization: limit bitmap size in memory
                    .into(holder.imageViewProduct);
        } else {
            holder.imageViewProduct.setImageResource(R.drawable.image_not_found);
        }

        // Vérifier si le produit est dans les favoris (O(1) lookup, no DB access)
        boolean isInWishlist = currentUserId != -1 && favoriteIds.contains(produit.getId());
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

    // ProductDiffCallback for efficient RecyclerView updates
    public static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Produits> oldList;
        private final List<Produits> newList;

        public ProductDiffCallback(List<Produits> oldList, List<Produits> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Produits oldProduct = oldList.get(oldItemPosition);
            Produits newProduct = newList.get(newItemPosition);
            return oldProduct.getNom().equals(newProduct.getNom()) &&
                    oldProduct.getPrix() == newProduct.getPrix() &&
                    oldProduct.getStock() == newProduct.getStock();
        }
    }
}