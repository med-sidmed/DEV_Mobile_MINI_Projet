package com.example.shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop.Models.Categories;
import com.example.shop.R;
import java.util.ArrayList;
import java.util.List;

public class CategorieAdapter extends RecyclerView.Adapter<CategorieAdapter.ViewHolder> {

    private List<Categories> categoriesList;
    private Context context;
    private OnCategoryActionListener actionListener;

    // Interface pour les callbacks
    public interface OnCategoryActionListener {
        void onEditCategory(Categories category, int position);
        void onDeleteCategory(Categories category, int position);
    }

    public CategorieAdapter(List<Categories> categoriesList, Context context, OnCategoryActionListener listener) {
        this.categoriesList = categoriesList != null ? categoriesList : new ArrayList<>();
        this.context = context;
        this.actionListener = listener;
    }

    // Méthode pour mettre à jour les données
    public void updateData(List<Categories> newCategoriesList) {
        categoriesList.clear();
        if (newCategoriesList != null) {
            categoriesList.addAll(newCategoriesList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categorie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categories category = categoriesList.get(position);
        holder.nomTextView.setText(category.getNom() != null ? category.getNom() : "Sans nom");
        holder.descriptionTextView.setText(category.getDescription() != null ? category.getDescription() : "Aucune description");

        // Configurer les listeners pour les boutons
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditCategory(category, position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteCategory(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomTextView, descriptionTextView;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomTextView = itemView.findViewById(R.id.textViewNom);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            editButton = itemView.findViewById(R.id.categorieEdit);
            deleteButton = itemView.findViewById(R.id.categorieDelete);
        }
    }
}