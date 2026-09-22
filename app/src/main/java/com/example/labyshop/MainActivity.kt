package com.example.labyshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.labyshop.ui.theme.LabyShopTheme
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



private const val DEFAULT_PRODUCT_IMAGE =
    "https://www.tcbap.org/global_graphics/default-store-350x350.jpg"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LabyShopTheme {
                LabyShopApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabyShopApp() {

    var cartItems by remember {
        mutableStateOf<List<CartItem>>(emptyList())
    }

    val cartItemCount = cartItems.sumOf { it.quantity }

    val repository = remember {
        ProductRepository(RetrofitClient.api)
    }

    val factory = remember {
        ProductViewModelFactory(repository)
    }

    val viewModel: ProductViewModel = viewModel(
        factory = factory
    )

    val products by viewModel.products.collectAsState()
    val error by viewModel.error.collectAsState()

    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "products"
    ) {

        composable("products") {

            Scaffold(
                topBar = {
                        TopAppBar(
                        title = {
                            Text(
                                text = "LabyShop",
                                fontWeight = FontWeight.Bold
                            )
                        },

                    actions = {
                        TextButton(
                            onClick = {
                                navController.navigate("cart")
                            }
                        ) {
                            Text(
                                text = if (cartItemCount > 0) {"Cart ($cartItemCount)"}  else {"Cart"},
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    )
                }
            ) {
                innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(innerPadding)
                ) {

                    Text(
                        text = "Products",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                        )

                    if (error != null) {

                        Text(
                            text = "Error: $error",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )

                    } else {

                        ProductList(
                            products = products,
                            onProductClick = { product ->
                                navController.navigate(
                                    "product/${product.id}"
                                )
                            }
                        )
                    }
                }
            }
        }

        composable("product/{productId}") { backStackEntry ->

            val productId =
                backStackEntry.arguments
                    ?.getString("productId")
                    ?.toIntOrNull()

            val product =
                products.find { it.id == productId }

            if (product != null) {

                ProductDetailScreen(
                    product = product,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddToCart = { cartItem ->

                        val existingIndex = cartItems.indexOfFirst { existingItem ->
                            existingItem.product.id == cartItem.product.id &&
                                    existingItem.variation == cartItem.variation
                        }

                        if (existingIndex != -1) {
                            val existingItem = cartItems[existingIndex]

                            val updatedItem = existingItem.copy(
                                quantity = existingItem.quantity + cartItem.quantity
                            )
                            cartItems = cartItems.toMutableList().apply {
                                this[existingIndex] = updatedItem
                            }
                        } else {
                            cartItems = cartItems + cartItem
                        }
                    }
                )
            }
        }

        composable("cart") {
            CartScreen(
                cartItems = cartItems,
                onBackClick = {
                    navController.popBackStack()
                },
                onIncrease = { cartItem ->
                    cartItems = cartItems.map { item ->
                        if (item == cartItem) {
                            item.copy(
                                quantity = item.quantity + 1
                            )
                        } else {
                            item
                        }
                    }
                },
                onDecrease = { cartItem ->
                    cartItems = cartItems.map { item ->
                        if (item == cartItem && item.quantity > 1   ) {
                            item.copy(
                                quantity = item.quantity - 1
                            )
                        } else {
                            item
                        }
                    }
                },
                onRemove = { cartItem ->
                    cartItems = cartItems.filter { item ->
                        item != cartItem
                    }

                }
            )
        }
    }
}


@Composable
fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {

        items(products) { product ->

            ProductItem(
                product = product,
                onClick = {
                    onProductClick(product)
                }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit
    ) {

    val imageUrl = DEFAULT_PRODUCT_IMAGE

    val priceText = when {

        product.price?.singlePrice != null -> {
            "৳${product.price.singlePrice.activePrice}"
        }

        product.price?.variationPrice?.prices?.isNotEmpty() == true -> {

            val prices = product.price.variationPrice.prices
                .map { it.activePrice }

            "From ৳${prices.minOrNull() ?: 0.0}"
        }

        else -> {
            "Price unavailable"
        }
    }

    val stockText =
        if (product.stockQuantity > 0) {
            "In stock"
        } else {
            "Out of stock"
        }

    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {

        Column {
            AsyncImage(
                model = product.image?.large ?: imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(160.dp)
                    .padding(10.dp)
            )

             Column(
                 modifier = Modifier
                     .padding(10.dp)
                     .fillMaxWidth()
             ) {
                 Text(
                     text = product.title,
                     maxLines = 2,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.height(40.dp)
                 )

                 Text(
                     text = product.brand ?: "",
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.height(20.dp)
                 )

                 Text(
                     text = priceText
                 )

                 Text(
                     text = stockText
                 )
             }
        }
    }
}