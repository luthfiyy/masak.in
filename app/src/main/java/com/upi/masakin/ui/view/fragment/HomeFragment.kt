package com.upi.masakin.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.R
import com.upi.masakin.adapters.ListRecipeAdapter
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.databinding.FragmentHomeBinding
import com.upi.masakin.databinding.ItemChefBinding
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.repository.RecipeRepository
import com.upi.masakin.model.Recipe
import com.upi.masakin.model.RecipeData
import com.upi.masakin.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<Recipe>()
    private lateinit var viewModel: RecipeViewModel
    private lateinit var listRecipeAdapter: ListRecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)

        list.addAll(getListRecipe())
        showRecyclerList()

        val database = MasakinDatabase.getDatabase(requireContext())
        val chefDao = database.chefDao()

        val repository = RecipeRepository(requireContext())

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            RecipeViewModel.RecipeViewModelFactory(repository)
        )[RecipeViewModel::class.java]

        // Setup RecyclerView
        binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initialize adapter
        listRecipeAdapter = ListRecipeAdapter(ArrayList()) { recipe ->
            val action = HomeFragmentDirections.actionHomeToDetail(recipe)
            findNavController().navigate(action)
        }
        binding.rvRecipes.adapter = listRecipeAdapter

        // Observe recipes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                listRecipeAdapter.updateRecipes(recipes)
            }
        }

        lifecycleScope.launch {
            chefDao.insertChef(Chef(name = "Chef A", image = R.drawable.img_chef1))
            chefDao.insertChef(Chef(name = "Chef B", image = R.drawable.img_chef2))
            chefDao.insertChef(Chef(name = "Chef C", image = R.drawable.img_chef3))
            chefDao.insertChef(Chef(name = "Chef D", image = R.drawable.img_chef4))

            val chefs = chefDao.getAllChefs()

            for (chef in chefs) {
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
                id = i + 1, // Add a unique ID
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
