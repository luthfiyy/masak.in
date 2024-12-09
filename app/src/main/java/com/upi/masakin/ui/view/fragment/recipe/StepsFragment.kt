package com.upi.masakin.ui.view.fragment.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.adapters.recipe.RecipeStepsAdapter
import com.upi.masakin.databinding.FragmentStepsBinding

class StepsFragment : Fragment() {
    private lateinit var binding: FragmentStepsBinding
    private lateinit var stepsAdapter: RecipeStepsAdapter

    companion object {
        private const val ARG_STEPS = "steps"

        fun newInstance(steps: List<String>): StepsFragment {
            val fragment = StepsFragment()
            val args = Bundle().apply {
                putStringArrayList(ARG_STEPS, ArrayList(steps))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val steps = arguments?.getStringArrayList(ARG_STEPS) ?: listOf()

        stepsAdapter = RecipeStepsAdapter(steps)
        binding.stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stepsAdapter
        }
    }
}