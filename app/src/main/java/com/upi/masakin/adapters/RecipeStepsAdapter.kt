package com.upi.masakin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.databinding.ItemRecipeStepBinding

class RecipeStepsAdapter(private val steps: List<String>) :
    RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemRecipeStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stepText: String, position: Int) {
            binding.tvStepNumber.apply {
                text = (position + 1).toString()
                visibility = View.VISIBLE
            }
            binding.tvStepDescription.text = stepText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(steps[position], position)
    }

    override fun getItemCount() = steps.size
}
