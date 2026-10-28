package com.example.myapplication.ui.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentAddEditProductBinding
import com.example.myapplication.models.Product
import com.example.myapplication.utils.FirebaseUtils
import java.util.*

class AddEditProductFragment : Fragment() {
    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditProductFragmentArgs by navArgs()
    private var imageUri: Uri? = null
    private var currentImageUrl: String = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.ivProduct.setImageURI(imageUri)
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { binding.ivProduct.setImageURI(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        
        args.productId?.let { loadProduct(it) }

        binding.btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        binding.btnSave.setOnClickListener { saveProduct() }
    }

    private fun takePhoto() {
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val file = java.io.File(storageDir, fileName)
        imageUri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        takePhotoLauncher.launch(intent)
    }

    private fun setupSpinners() {
        val categories = arrayOf("Electrónica", "Ropa", "Hogar", "Otros")
        binding.actvCategory.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories))
        
        val states = arrayOf("nuevo", "usado")
        binding.actvState.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, states))
    }

    private fun loadProduct(id: String) {
        FirebaseUtils.db.collection("productos").document(id).get()
            .addOnSuccessListener { doc ->
                val product = doc.toObject(Product::class.java)
                product?.let {
                    binding.etName.setText(it.nombre)
                    binding.etDescription.setText(it.descripcion)
                    binding.etPrice.setText(it.precio.toString())
                    binding.etQuantity.setText(it.cantidad.toString())
                    binding.actvCategory.setText(it.categoria, false)
                    binding.actvState.setText(it.estado, false)
                    currentImageUrl = it.imagen
                    Glide.with(this).load(it.imagen).into(binding.ivProduct)
                }
            }
    }

    private fun saveProduct() {
        val name = binding.etName.text.toString()
        val desc = binding.etDescription.text.toString()
        val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val qty = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        val cat = binding.actvCategory.text.toString()
        val state = binding.actvState.text.toString()
        val sellerId = FirebaseUtils.currentUserId ?: return

        if (imageUri != null) {
            uploadImage { url ->
                val product = Product("", name, desc, price, cat, state, qty, url, sellerId)
                persistProduct(product)
            }
        } else {
            val product = Product("", name, desc, price, cat, state, qty, currentImageUrl, sellerId)
            persistProduct(product)
        }
    }

    private fun uploadImage(onSuccess: (String) -> Unit) {
        val ref = FirebaseUtils.storage.reference.child("products/${UUID.randomUUID()}")
        imageUri?.let { uri ->
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { onSuccess(it.toString()) }
            }
        }
    }

    private fun persistProduct(product: Product) {
        val collection = FirebaseUtils.db.collection("productos")
        val task = if (args.productId != null) {
            collection.document(args.productId!!).set(product)
        } else {
            collection.add(product)
        }
        
        task.addOnSuccessListener {
            Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
