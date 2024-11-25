package com.mobil80.productlist.data.network

import com.mobil80.productlist.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") productId: String): Product
}