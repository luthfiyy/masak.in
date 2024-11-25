package com.upi.masakin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.R
import com.upi.masakin.adapters.ListRecipeAdapter
import com.upi.masakin.databinding.FragmentHomeBinding
import com.upi.masakin.databinding.ItemChefBinding
import com.upi.masakin.model.Chef
import com.upi.masakin.model.Recipe
import com.upi.masakin.model.RecipeData

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<Recipe>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)

        list.addAll(getListRecipe())
        showRecyclerList()

        val chefList = listOf(
            Chef("Chef A", R.drawable.img_chef1),
            Chef("Chef B", R.drawable.img_chef2),
            Chef("Chef C", R.drawable.img_chef3),
            Chef("Chef D", R.drawable.img_chef4)
        )

        for (chef in chefList) {
            val chefItemBinding = ItemChefBinding.inflate(
                LayoutInflater.from(context),
                binding.lChef,
                false
            )
            chefItemBinding.imgItemPhoto.setImageResource(chef.image)
            chefItemBinding.tvChefName.text = chef.name
            binding.lChef.addView(chefItemBinding.root)
        }
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list -> {
                binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
            }
            R.id.action_grid -> {
                binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getListRecipe(): ArrayList<Recipe> {
        val dataName = resources.getStringArray(R.array.data_title)
        val ingredients = RecipeData.getIngredientsList()
        val steps = RecipeData.getStepsList()
        val description = resources.getStringArray(R.array.data_description)
        val time = resources.getStringArray(R.array.data_time)
        val serving = resources.getStringArray(R.array.data_serving)
        val reviews = resources.getStringArray(R.array.data_reviews)
        val dataPhoto = resources.obtainTypedArray(R.array.data_image)

        val listRecipe = ArrayList<Recipe>()
        for (i in dataName.indices) {
            val recipe = Recipe(
                id,
                dataName[i],
                ingredients[i],
                steps[i],
                description[i],
                time[i],
                serving[i],
                reviews[i],
                dataPhoto.getResourceId(i, -1)
            )
            listRecipe.add(recipe)
        }
        dataPhoto.recycle()
        return listRecipe
    }

    private fun showRecyclerList() {
        val listRecipeAdapter = ListRecipeAdapter(list) { recipe ->
             val action = HomeFragmentDirections.actionHomeToDetail(recipe)
             findNavController().navigate(action)
        }
        binding.rvRecipes.adapter = listRecipeAdapter
    }
}
