package com.mobil80.productlist.data.model

data class Product(
    val id: Int,
    val productCategory: String,
    val name: String,
    val brand: String,
    val description: String,
    val basePrice: Double,
    val inStock: Boolean,
    val stock: Int,
    val featuredImage: String,
    val thumbnailImage: String,
    val storageOptions: List<String>,
    val colorOptions: List<String>,
    val display: String,
    val CPU: String,
    val camera: Camera
)

data class Camera(
    val rearCamera: String,
    val frontCamera: String
)

