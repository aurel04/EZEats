package com.example.ezeats.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezeats.databinding.FragmentHomeBinding
import com.example.ezeats.detailrecipe.RecipeAdapter
import com.example.ezeats.utils.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        recipeAdapter = RecipeAdapter{ recipe, imageView, nameView ->
            val id = recipe.id
            val title = recipe.title
            val ingredients = recipe.ingredients
            val steps = recipe.steps
            val images = recipe.images
            val likes = recipe.likes

            val extras = FragmentNavigatorExtras(
                imageView to imageView.transitionName,
                nameView to nameView.transitionName
            )

            val action = HomeFragmentDirections.actionHomeFragmentToDetailRecipeFragment(
                id!!, title!!, ingredients!!, steps!!, images!!
            )

            findNavController().navigate(action)
        }

        val recyclerRecipe : RecyclerView = binding.rvRecipe

        recyclerRecipe.layoutManager = GridLayoutManager(context, 2)
        recyclerRecipe.setHasFixedSize(true)
        binding.rvRecipe.adapter = recipeAdapter

//        val recyclerTrending = binding.rvTrending
//        recyclerTrending.layoutManager = LinearLayoutManager(context)
//        binding.rvTrending.adapter = recipeAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.recipe.observe(viewLifecycleOwner){ data ->
            Log.d("HomeFragment", "Received data: $data")
            if(data!=null){
                Log.d("HomeFragment", "Data is not null, submitting to adapter")
                recipeAdapter.submitData(viewLifecycleOwner.lifecycle, data)
                Log.d("HomeFragment", "Data loaded: $data items")
            }else{
                Log.d("HomeFragment", "No data loaded")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )
    }

    override fun onConn

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HomeFragment", "Destroying HomeFragment view")
        _binding = null
    }

    override fun onPause() {
        super.onPause()
    }
}