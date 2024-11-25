package com.mobil80.productlist.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mobil80.productlist.data.model.Product
import com.mobil80.productlist.presentation.ui.theme.ProductListTheme
import com.mobil80.productlist.presentation.viewmodels.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {

    private val detailViewModel by lazy { getViewModel<ProductViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductListTheme {
                MainNavigation(detailViewModel)
            }
        }
    }
}

@Composable
fun MainNavigation(detailViewModel: ProductViewModel) { // E8EDF6
    val navController = rememberNavController()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE8EDF6))) { // Set global background
        NavHost(navController, startDestination = "productList") {
            composable("productList") {
                ProductListScreen(navController, detailViewModel)
            }
            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                ProductDetailScreen(
                    navController = navController,
                    productId = productId,
                    viewModel = detailViewModel
                )
            }
        }
    }
}

@Composable
fun ProductListScreen(navController: NavController, viewModel: ProductViewModel) {
    val products = viewModel.productList.collectAsState().value // Collect the product list state
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(products) { product -> // Directly pass the list
                        ProductCard(product = product) {
                            navController.navigate("productDetail/${product.id}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    viewModel: ProductViewModel
) {
    val productDetail = viewModel.productDetail.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    // Trigger API call on productId change
    LaunchedEffect(productId) {
        productId?.let {
            viewModel.fetchProductDetails(it)
        }
    }

    // Set background color using a Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8EDF6))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8EDF6))
                        ) {
                            // Back icon (aligned to start)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterStart)
                                    .clickable { navController.popBackStack() },
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            // Title (centered)
                            Text(
                                text = "Product Details",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            // Optional forward icon (aligned to end)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Forward",
                                modifier = Modifier
                                    .size(24.dp)
                                    .alpha(0f)
                                    .align(Alignment.CenterEnd)
                                    .clickable { /* Handle forward icon click */ },
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = null, // Prevent the default navigation icon, as we're using custom
                    backgroundColor = Color(0xFFE8EDF6), // Set background color for the TopAppBar
                    contentColor = Color.Black, // Text/icon color
                    elevation = 4.dp // Optional elevation for shadow
                )
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                when {
                    isLoading -> {
//                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    productDetail != null -> {
                        ProductDetailContent(product = productDetail)
                    }
                }
            }
        }
    }
}


@Composable
fun ProductDetailContent(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Product Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.thumbnailImage)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "Product Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Product Details
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Brand: ${product.brand}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = "Price: $${product.basePrice}",
            style = MaterialTheme.typography.bodyMedium,
            color =  Color.Green,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = if (product.inStock) "In Stock" else "Out of Stock",
            color = if (product.inStock)  Color.Green else Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Additional Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Description:", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
//            Timber.tag("amplifyify").d("${product.thumbnailImage}")

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.thumbnailImage)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product Details
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Brand: ${product.brand}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "$${product.basePrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color =  Color.Green
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (product.inStock) "In Stock" else "Out of Stock",
                        color = if (product.inStock) Color.Green else Color.Red
                    )
                    Text(text = "Stock: ${product.stock}")
                }
            }
        }
    }
}

