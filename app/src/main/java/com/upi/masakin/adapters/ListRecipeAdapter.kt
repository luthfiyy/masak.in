package com.upi.masakin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.databinding.ItemRowRecipeBinding
import com.upi.masakin.model.Recipe
import com.upi.masakin.ui.RecipeDetailActivity

class ListRecipeAdapter(private val listRecipe: ArrayList<Recipe>) :
    RecyclerView.Adapter<ListRecipeAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val recipe = listRecipe[position]

        with(holder.binding) {
            imgItemPhoto.setImageResource(recipe.image)
            tvItemTitle.text = recipe.title
            tvItemTime.text = recipe.time

            root.setOnClickListener { view ->
                val intent = Intent(view.context, RecipeDetailActivity::class.java)
                intent.putExtra("recipe", recipe)
                view.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = listRecipe.size

    class ListViewHolder(val binding: ItemRowRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)
}