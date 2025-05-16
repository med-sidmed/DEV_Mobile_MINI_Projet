package com.example.shop.API;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.Activities.CardActivity;
import com.example.shop.Models.ArticlePanier;
import com.example.shop.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private CartAdapterListener listener;

    public CartAdapter(List<ArticlePanier> cartItems, CardActivity cardActivity) {
    }

    public interface CartAdapterListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemRemoved(int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartAdapterListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Afficher les informations du produit
        holder.productName.setText(item.getName());

        // Formater le prix avec le symbole €
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        holder.productPrice.setText(formatter.format(item.getPrice()));

        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        // Charger l'image avec Glide
//        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
//            Glide.with(context)
//                    .load(item.getImageUrl())
//                    .placeholder(R.drawable.clothes_icon)
//                    .error(R.drawable.tshirt)
//                    .into(holder.productImage);
//        } else {
//            holder.productImage.setImageResource(R.drawable.clothes_icon);
//        }

        // Gestion des boutons de quantité
        holder.btnMinus.setOnClickListener(v -> {
            int position1 = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity >= 1) {
                    item.setQuantity(newQuantity);
                    holder.productQuantity.setText(String.valueOf(newQuantity));
                    listener.onQuantityChanged(position1, newQuantity);
                }
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int position2 = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                holder.productQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(position, newQuantity);
            }
        });

        // Gestion du bouton supprimer
        holder.btnDelete.setOnClickListener(v -> {
            int position3 = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemRemoved(position3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;
        Button btnMinus, btnPlus;
        ImageButton btnDelete;

        @SuppressLint("WrongViewCast")
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.quantiteProduit);
            btnMinus = itemView.findViewById(R.id.buttonMinus);
            btnPlus = itemView.findViewById(R.id.buttonPlus);
            btnDelete = itemView.findViewById(R.id.btnCheckout);
        }
    }
}