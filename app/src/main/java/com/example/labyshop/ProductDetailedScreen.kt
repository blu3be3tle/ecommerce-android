package com.example.labyshop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


private const val DEFAULT_PRODUCT_IMAGE =
    "https://www.tcbap.org/global_graphics/default-store-350x350.jpg"


@Composable
fun ProductPrice(
    product: Product,
    selectedVariation: VariationItem?
) {
    when {

        selectedVariation != null -> {
            Text(
                text = "৳${selectedVariation.activePrice}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        product.price?.singlePrice != null -> {
            Text(
                text = "৳${product.price.singlePrice.activePrice}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        product.price?.variationPrice?.prices?.isNotEmpty() == true -> {
            val lowestPrice = product.price.variationPrice.prices.minOf { it.activePrice }

            Text(
                text = "From ৳$lowestPrice",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        else -> {

            Text(
                text = "Price unavailable",
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun VariationSelector(
    variations: List<VariationItem>,
    selectedVariation: VariationItem?,
    onVariationSelected: (VariationItem) -> Unit
) {
    if (variations.isEmpty()) {
        return
    }

    Column {
        Text(
            text = "Options",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )

        variations.forEach { variation ->
            Button(
                onClick = {
                    onVariationSelected(variation)
                },
                enabled = variation.isAvailable,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = when {
                        !variation.isAvailable ->
                            "${variation.titleSuffix} - Out of stock"

                        selectedVariation == variation ->
                            "✓ ${variation.titleSuffix} - ৳${variation.activePrice}"

                        else ->
                            "${variation.titleSuffix} — ৳${variation.activePrice}"
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onAddToCart: (CartItem) -> Unit
) {

    val imageUrl = DEFAULT_PRODUCT_IMAGE

    var selectedVariation by remember {
        mutableStateOf< VariationItem?>(null)
    }

    var quantity by remember {
        mutableStateOf(1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Product Details")
                },
                navigationIcon = {
                        IconButton(
                            onClick = onBackClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                }
            )
        }
    ) {
        innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                AsyncImage(
                    model = product.image?.large ?: imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Spacer(
                    modifier = Modifier.height(40.dp)
                )

                Text(
                    text = product.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                ProductPrice(
                    product = product,
                    selectedVariation = selectedVariation
                )

                val variations =
                    product.price?.variationPrice?.prices ?: emptyList()

                VariationSelector(
                    variations = variations,
                    selectedVariation = selectedVariation,
                    onVariationSelected = { variation ->
                        selectedVariation = variation
                    }
                )

                QuantitySelector(
                    quantity = quantity,
                    onDecrease = {
                        if (quantity > 1) {
                            quantity--
                        }
                    },
                    onIncrease = {
                        quantity++
                    }
                )

//                Add to Cart

                val hasVariations = variations.isNotEmpty()

                val canAddToCart =
                    if (hasVariations) {
                        selectedVariation?.isAvailable == true
                    } else {
                        product.stockQuantity > 0
                    }

                Button(
//                    enabled = canAddToCart,
                    onClick = {
                        val cartItem = CartItem(
                            product = product,
                            variation = selectedVariation,
                            quantity = quantity
                        )

                        onAddToCart(cartItem)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 26.dp)

                ) {
                    Text(
                        text = "Add to Cart",
                        fontSize = 18.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(17.dp)
                )

                if (!product.brand.isNullOrBlank()) {

                    Text(
                        text = "Brand: ${product.brand}",
                        fontSize = 15.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                }

                Text(
                    text = "Product ID: ${product.id}",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Origin: ${product.origin?.name ?: "Unknown"}",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )



                Text(
                    text = "Availability",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = if (product.stockQuantity > 0) {
                        "In stock"
                    } else {
                        "Out of stock"
                    }
                )

         }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onDecrease
        ) {
            Text("-")
        }

        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = onIncrease
        ) {
            Text("+")
        }
    }
}