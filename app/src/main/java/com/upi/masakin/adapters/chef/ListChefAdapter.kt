package com.upi.masakin.adapters.chef

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.databinding.ItemChefBinding

class ListChefAdapter(
    private val chefs: List<Chef>,
    private val onClick: (Chef) -> Unit
) : RecyclerView.Adapter<ListChefAdapter.ChefViewHolder>() {

    inner class ChefViewHolder(private val binding: ItemChefBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chef: Chef) {
            binding.imgItemPhoto.setImageResource(chef.image)
            binding.tvChefName.text = chef.name

            binding.root.setOnClickListener {
                onClick(chef)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChefViewHolder {
        val binding = ItemChefBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChefViewHolder, position: Int) {
        holder.bind(chefs[position])
    }

    override fun getItemCount(): Int = chefs.size
}
