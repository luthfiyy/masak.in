package com.upi.masakin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.ItemRowRecipeBinding

class  ListRecipeAdapter(
    private val listRecipe: ArrayList<RecipeEntity>,
    private val onItemClick: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<ListRecipeAdapter.ListViewHolder>() {

    fun updateRecipes(newRecipes: List<RecipeEntity>) {
        val diffCallback = RecipeDiffCallback(listRecipe, newRecipes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listRecipe.clear()
        listRecipe.addAll(newRecipes)
        diffResult.dispatchUpdatesTo(this)
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

    class RecipeDiffCallback(
        private val oldList: List<RecipeEntity>,
        private val newList: List<RecipeEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}


