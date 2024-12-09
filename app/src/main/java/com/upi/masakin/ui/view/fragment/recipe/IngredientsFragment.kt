package com.upi.masakin.ui.view.fragment.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.upi.masakin.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INGREDIENTS = "ingredients"

        fun newInstance(ingredients: List<String>): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_INGREDIENTS, ArrayList(ingredients))
            fragment.arguments = args
            return fragment
        }
    }

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

        val ingredients = arguments?.getStringArrayList(ARG_INGREDIENTS) ?: listOf()
        binding.tvDetailIngredients.text = ingredients.joinToString("\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}