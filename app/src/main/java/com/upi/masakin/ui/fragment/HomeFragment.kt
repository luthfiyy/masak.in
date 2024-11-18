@file:Suppress("DEPRECATION")

package com.upi.masakin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.R
import com.upi.masakin.adapters.ListRecipeAdapter
import com.upi.masakin.databinding.FragmentHomeBinding
import com.upi.masakin.model.Recipe

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)  // Pastikan fragment menerima callback opsi menu

        binding.rvRecipes.setHasFixedSize(true)
        list.addAll(getListRecipe())
        showRecyclerList()
    }

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
        val dataIngredients = resources.getStringArray(R.array.data_ingredients)
        val description = resources.getStringArray(R.array.data_description)
        val steps = resources.getStringArray(R.array.data_steps)
        val time = resources.getStringArray(R.array.data_time)
        val serving = resources.getStringArray(R.array.data_serving)
        val dataPhoto = resources.obtainTypedArray(R.array.data_image)

        val listRecipe = ArrayList<Recipe>()
        for (i in dataName.indices) {
            val recipe = Recipe(
                dataName[i],
                dataIngredients[i],
                steps[i],
                description[i],
                time[i],
                serving[i],
                dataPhoto.getResourceId(i, -1)
            )
            listRecipe.add(recipe)
        }
        dataPhoto.recycle()
        return listRecipe
    }

    private fun showRecyclerList() {
        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        val listRecipeAdapter = ListRecipeAdapter(list)
        binding.rvRecipes.adapter = listRecipeAdapter
    }
}