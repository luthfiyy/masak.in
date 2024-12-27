package com.upi.masakin.ui.view.fragment.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.adapters.recipe.IngredientAdapter
import com.upi.masakin.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {

    companion object {
        private const val ARG_INGREDIENTS = "ingredients"
        private const val ARG_INGREDIENT_IMAGES = "ingredient_images"

        fun newInstance(
            ingredients: List<String>,
            ingredientImages: List<String>? = null
        ): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle().apply {
                putStringArrayList(ARG_INGREDIENTS, ArrayList(ingredients))
                ingredientImages?.let {
                    putStringArrayList(ARG_INGREDIENT_IMAGES, ArrayList(it))
                }
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ingredientsList = arguments?.getStringArrayList(ARG_INGREDIENTS) ?: emptyList()
        val ingredientImages = arguments?.getStringArrayList(ARG_INGREDIENT_IMAGES)

        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = IngredientAdapter(ingredientsList, ingredientImages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
