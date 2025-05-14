package com.example.shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Produits;
import com.example.shop.R;
import java.util.List;

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ViewHolder> {
    private List<Produits> favoriteProducts;
    private Context context;
    private DBHelper dbHelper;

    public FavoriteProductAdapter(List<Produits> favoriteProducts, Context context, DBHelper dbHelper) {
        this.favoriteProducts = favoriteProducts;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produits product = favoriteProducts.get(position);
        holder.productName.setText(product.getNom());
        holder.productPrice.setText(String.format("%.2f €", product.getPrix()));
        // Charger l'image si disponible (par exemple avec Glide)
        // Glide.with(context).load(product.getImage1()).into(holder.productImage);
        holder.btnRemoveFavorite.setOnClickListener(v -> {
            int userId = 1; // Remplacez par l'ID de l'utilisateur connecté
            boolean removed = dbHelper.removeFavorite(userId, product.getId());
            if (removed) {
                favoriteProducts.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Produit retiré des favoris", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteProducts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageButton btnRemoveFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}