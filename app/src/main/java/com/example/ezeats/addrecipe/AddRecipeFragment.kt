package com.example.ezeats.addrecipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ezeats.R
import com.example.ezeats.databinding.FragmentAddRecipeBinding
import com.example.ezeats.utils.ViewModelFactory
import com.example.ezeats.utils.getImageUri
import com.example.ezeats.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddRecipeFragment : Fragment() {
    private var currentImageUri: Uri? = null

    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!

    private val addRecipeViewModel: AddRecipeViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivRecipe.setImageResource(R.mipmap.base_food_image)
        binding.btAddImage.setOnClickListener{
            view -> showPopUpMenu(view)
        }
        binding.btSubmit.setOnClickListener{
            val title = binding.inputRecipeName.text.toString()
            val ingredients = binding.inputIngredient.text.toString()
            val steps = binding.inputSteps.text.toString()

            uploadData(title, ingredients, steps)
        }
        Log.e("AddRecipe", "AddRecipeFragment opened")
    }

    private fun showPopUpMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        popupMenu.menuInflater.inflate(R.menu.popup_image, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.camera -> {
                    currentImageUri = getImageUri(requireContext())
                    launcherIntentCamera.launch(currentImageUri)
                    true
                }
                R.id.gallery -> {
                    val gallery = Intent.createChooser(intent, "Choose a image")
                    startGallery.launch(gallery)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private val startGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                currentImageUri = result.data?.data as Uri
                if (currentImageUri != null) {
                    binding.ivRecipe.setImageURI(currentImageUri)
                } else {
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                binding.ivRecipe.setImageURI(currentImageUri)
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun uploadData(title: String, ingredients: String, steps: String){
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())
            Log.d("Image File", "showImage: ${imageFile.path}")

            val titleBody = title.toRequestBody("text/plain".toMediaType())
            val ingredientsBody = ingredients.toRequestBody("text/plain".toMediaType())
            val stepsBody = steps.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", null, titleBody)
                .addFormDataPart("ingredients", null, ingredientsBody)
                .addFormDataPart("steps", null, stepsBody)
                .addFormDataPart("photo", imageFile.name, requestImageFile)
                .build()
        }
    }
}