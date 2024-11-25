package com.mobil80.productlist.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobil80.productlist.data.model.Product
import com.mobil80.productlist.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val apiService: ApiService) : ViewModel() {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList

    private val _productDetail = MutableStateFlow<Product?>(null)
    val productDetail: StateFlow<Product?> = _productDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val products = apiService.getProducts() // API call
                _productList.value = products
                _errorMessage.value = null // Clear error if data is fetched
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching products: ${e.message}"
                _productList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val product = apiService.getProductDetails(productId) // Replace with actual API call
                _productDetail.value = product
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load product details."
            } finally {
                _isLoading.value = false
            }
        }
    }
}


