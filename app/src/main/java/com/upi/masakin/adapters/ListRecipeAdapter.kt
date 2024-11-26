package com.upi.masakin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.databinding.ItemRowRecipeBinding
import com.upi.masakin.model.Recipe

class ListRecipeAdapter(
    private val listRecipe: ArrayList<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<ListRecipeAdapter.ListViewHolder>() {

    // Method to update the entire list
    fun updateRecipes(newRecipes: List<Recipe>) {
        listRecipe.clear()
        listRecipe.addAll(newRecipes)
        notifyDataSetChanged()
    }

    // Method to add a single recipe
    fun addRecipe(recipe: Recipe) {
        listRecipe.add(recipe)
        notifyItemInserted(listRecipe.size - 1)
    }

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

        holder.binding.apply {
            imgItemPhoto.setImageResource(recipe.image)
            tvItemTitle.text = recipe.title
            tvItemTime.text = recipe.time

            root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    override fun getItemCount(): Int = listRecipe.size

    class ListViewHolder(val binding: ItemRowRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
