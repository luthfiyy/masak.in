package com.upi.masakin.ui.view.fragment.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upi.masakin.R
import com.upi.masakin.adapters.recipe.IngredientAdapter

class IngredientsFragment : Fragment() {
    companion object {
        private const val ARG_INGREDIENTS = "ingredients"
        private const val ARG_INGREDIENT_IMAGES = "ingredient_images"

        fun newInstance(
            ingredients: List<String>,
            ingredientImages: List<String>? = null
        ): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_INGREDIENTS, ArrayList(ingredients))
            ingredientImages?.let {
                args.putStringArrayList(ARG_INGREDIENT_IMAGES, ArrayList(it))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        val ingredientsList = arguments?.getStringArrayList(ARG_INGREDIENTS) ?: emptyList()
        val ingredientImages = arguments?.getStringArrayList(ARG_INGREDIENT_IMAGES)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_ingredients)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = IngredientAdapter(ingredientsList, ingredientImages)

        return view
    }
}