package com.upi.masakin.adapters.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.upi.masakin.R
import com.upi.masakin.databinding.IngredientItemBinding

class IngredientAdapter(
    private val ingredients: List<String>,
    private val ingredientImages: List<String>? = null
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = IngredientItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient, ingredientImages?.getOrNull(position))
    }

    override fun getItemCount(): Int = ingredients.size

    inner class ViewHolder(private val binding: IngredientItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: String, imageUrl: String?) {
            binding.tvIngredient.text = ingredient

            imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .transform(RoundedCorners(20))
                    .placeholder(R.drawable.placeholder_article)
                    .into(binding.ivIngredient)
            } ?: run {
                binding.ivIngredient.setImageResource(R.drawable.placeholder_article)
            }
        }
    }
}
