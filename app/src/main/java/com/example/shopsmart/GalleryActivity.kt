package com.example.shopsmart

import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.QueryDocumentSnapshot

class GalleryActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        gridView = findViewById(R.id.gridView)

        // Fetch image details from Firestore and load images
        loadImagesFromFirestore()
    }

    private fun loadImagesFromFirestore() {
        firestore.collection("images")
            .get()
            .addOnSuccessListener { result ->
                val imageUrls = mutableListOf<String>()
                for (document: QueryDocumentSnapshot in result) {
                    val imageUrl = document.getString("imageUrl")
                    imageUrls.add(imageUrl ?: "")
                }

                // Use FirebaseUI to load images into GridView
                val adapter = FirebaseImageAdapter(this, imageUrls)
                gridView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load images: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
