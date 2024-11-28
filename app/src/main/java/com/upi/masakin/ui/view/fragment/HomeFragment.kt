package com.upi.masakin.ui.view.fragment

import android.os.Bundle
import android.util.Log
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
import com.upi.masakin.data.repository.ChefRepository
import com.upi.masakin.databinding.FragmentHomeBinding
import com.upi.masakin.databinding.ItemChefBinding
import com.upi.masakin.data.repository.RecipeRepository
import com.upi.masakin.ui.viewmodel.ChefViewModel
import com.upi.masakin.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipeViewModel
    private lateinit var listRecipeAdapter: ListRecipeAdapter
    private lateinit var chefViewModel: ChefViewModel


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

        listRecipeAdapter = ListRecipeAdapter(ArrayList()) { recipe ->
            val action = HomeFragmentDirections.actionHomeToDetail(recipe)
            findNavController().navigate(action)
        }
        binding.rvRecipes.adapter = listRecipeAdapter

        viewModel = ViewModelProvider(
            this,
            RecipeViewModel.RecipeViewModelFactory(
                RecipeRepository(
                    requireContext(),
                    Dispatchers.IO
                )
            )
        )[RecipeViewModel::class.java]

        chefViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ChefViewModel::class.java)) {
                        return ChefViewModel(ChefRepository(requireContext())) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        )[ChefViewModel::class.java]


        // Fetch and observe recipes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                listRecipeAdapter.updateRecipes(recipes)
            }
        }

        // Observe and display chefs
        viewLifecycleOwner.lifecycleScope.launch {
            chefViewModel.chefs.collect { chefs ->
                Log.d("HomeFragment", "Received chefs: ${chefs.size}")
                binding.lChef.removeAllViews() // Clear existing views
                if (chefs.isEmpty()) {
                    Log.w("HomeFragment", "No chefs found")
                }
                for (chef in chefs) {
                    val chefItemBinding = ItemChefBinding.inflate(
                        LayoutInflater.from(context),
                        binding.lChef,
                        false
                    )
                    chefItemBinding.imgItemPhoto.setImageResource(chef.image)
                    chefItemBinding.tvChefName.text = chef.name

                    // Add click listener
                    chefItemBinding.root.setOnClickListener {
                        val action = HomeFragmentDirections.actionHomeToChefDetail(chef)
                        findNavController().navigate(action)
                    }

                    binding.lChef.addView(chefItemBinding.root)
                }
            }
        }

        // Insert sample chefs if needed
        chefViewModel.insertSampleChefs()
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
}
