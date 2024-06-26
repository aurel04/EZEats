package com.example.ezeats.detailrecipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezeats.databinding.FragmentStepsBinding
import com.example.ezeats.utils.ViewModelFactory

class StepsFragment : Fragment() {
    private var _binding: FragmentStepsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var stepsAdapter: StepsAdapter
    private val detailViewModel: DetailRecipeViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepsBinding.inflate(inflater, container, false)

        recyclerView = binding.rvSteps
        recyclerView.layoutManager = LinearLayoutManager(context)

        val bundle = arguments
        val steps = bundle?.getString("steps")
        val stepsList = steps?.split("\n ")
        stepsAdapter = stepsList?.let { StepsAdapter(it) }?: StepsAdapter(emptyList())

        recyclerView.adapter = stepsAdapter
        Log.d("Steps Fragment", "$steps data")

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailViewModel.steps.observe(viewLifecycleOwner, Observer { steps ->
            // Use the steps data
            stepsAdapter.steps = steps.split("\n ")
            stepsAdapter.notifyDataSetChanged()
        })
    }
}