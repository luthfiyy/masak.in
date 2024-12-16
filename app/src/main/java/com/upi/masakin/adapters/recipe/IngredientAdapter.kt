package com.upi.masakin.adapters.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_item, parent, false)
        IngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient, ingredientImages?.getOrNull(position))
    }

    override fun getItemCount(): Int = ingredients.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIngredient: TextView = itemView.findViewById(R.id.tv_ingredient)
        private val imgIngredient: ImageView = itemView.findViewById(R.id.iv_ingredient)


        fun bind(ingredient: String, imageUrl: String?) {
            tvIngredient.text = ingredient

            imageUrl?.let {
                Glide.with(itemView.context)
                    .load(it)
                    .transform(RoundedCorners(20))
                    .placeholder(R.drawable.placeholder_article)
                    .into(imgIngredient)
            }
        }
    }
}