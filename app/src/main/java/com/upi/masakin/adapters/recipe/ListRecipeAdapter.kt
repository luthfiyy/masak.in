package com.upi.masakin.adapters.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upi.masakin.R
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.ItemRowRecipeBinding
import com.upi.masakin.utils.DiffUtilCallback

class ListRecipeAdapter(
    private val listRecipe: ArrayList<RecipeEntity>,
    private val onItemClick: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<ListRecipeAdapter.ListViewHolder>() {

    fun updateRecipes(newRecipes: List<RecipeEntity>) {
        val diffCallback = DiffUtilCallback(
            oldList = listRecipe,
            newList = newRecipes,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listRecipe.clear()
        listRecipe.addAll(newRecipes)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val recipe = listRecipe[position]
        holder.binding.apply {
            Glide.with(root.context).load(recipe.image)
                .placeholder(R.drawable.placeholder_article)
                .into(imgItemPhoto)
            tvItemTitle.text = recipe.title
            tvItemTime.text = recipe.time
            rbItemRating.rating = recipe.rating

            root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    override fun getItemCount(): Int = listRecipe.size

    class ListViewHolder(val binding: ItemRowRecipeBinding) : RecyclerView.ViewHolder(binding.root)
}