package com.upi.masakin.ui.view.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.R
import com.upi.masakin.adapters.chef.ListChefAdapter
import com.upi.masakin.adapters.recipe.ListRecipeAdapter
import com.upi.masakin.databinding.FragmentHomeBinding
import com.upi.masakin.ui.viewmodel.chef.ChefViewModel
import com.upi.masakin.ui.viewmodel.recipe.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()
    private val chefViewModel: ChefViewModel by viewModels()

    private lateinit var listRecipeAdapter: ListRecipeAdapter
    private lateinit var listChefAdapter: ListChefAdapter

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

        binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)

        setupSearchBar()
        setupChipFilters()
        setupRecipeAdapter()
        setupChefAdapter()
        setupOptionsMenu()

        observeRecipes()
        observeChefs()
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener { text ->
            val query = text.toString().trim()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchRecipes(query).collect { filteredRecipes ->
                    listRecipeAdapter.updateRecipes(filteredRecipes)
                }
            }
        }
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnClickListener {
            binding.chipAll.isChecked = true
            binding.chipPopular.isChecked = false

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.recipes.collect { recipes ->
                    listRecipeAdapter.updateRecipes(recipes)
                }
            }
        }

        binding.chipPopular.setOnClickListener {
            binding.chipAll.isChecked = false
            binding.chipPopular.isChecked = true

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.popularRecipes.collect { popularRecipes ->
                    listRecipeAdapter.updateRecipes(popularRecipes)
                }
            }
        }
    }

    private fun setupRecipeAdapter() {
        listRecipeAdapter = ListRecipeAdapter(ArrayList()) { recipe ->
            val action = HomeFragmentDirections.actionHomeToDetail(recipe)
            findNavController().navigate(action)
        }
        binding.rvRecipes.adapter = listRecipeAdapter
    }

    private fun setupChefAdapter() {
        binding.rvChefs.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun observeRecipes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                listRecipeAdapter.updateRecipes(recipes)
            }
        }
    }

    private fun observeChefs() {
        viewLifecycleOwner.lifecycleScope.launch {
            chefViewModel.chefs.collect { chefs ->
                if (chefs.isEmpty()) {
                    Log.w("HomeFragment", "No chefs found")
                } else {
                    listChefAdapter = ListChefAdapter(chefs) { chef ->
                        val action = HomeFragmentDirections.actionHomeToChefDetail(chef)
                        findNavController().navigate(action)
                    }
                    binding.rvChefs.adapter = listChefAdapter
                }
            }
        }
    }

    private fun setupOptionsMenu() {
        val menuHost: MenuHost = requireActivity()
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_list -> {
                        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
                        true
                    }
                    R.id.action_grid -> {
                        binding.rvRecipes.layoutManager = GridLayoutManager(requireContext(), 2)
                        true
                    }
                    else -> false
                }
            }
        }
        menuHost.removeMenuProvider(menuProvider)
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}