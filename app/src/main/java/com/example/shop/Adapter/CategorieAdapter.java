package com.example.shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public CategorieAdapter(List<Categories> categoriesList, Context context) {
        this.categoriesList = categoriesList != null ? categoriesList : new ArrayList<>();
        this.context = context;
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
        Categories categorie = categoriesList.get(position);
        holder.nomTextView.setText(categorie.getNom() != null ? categorie.getNom() : "Sans nom");
        holder.descriptionTextView.setText(categorie.getDescription() != null ? categorie.getDescription() : "Aucune description");
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomTextView, descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomTextView = itemView.findViewById(R.id.textViewNomCategorie);
            descriptionTextView = itemView.findViewById(R.id.textViewDescriptionCategorie);
        }
    }
}