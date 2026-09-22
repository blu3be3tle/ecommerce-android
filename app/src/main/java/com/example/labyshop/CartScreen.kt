package com.example.labyshop

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale


import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.motionEventSpy


private const val CART_IMAGE =  "https://www.tcbap.org/global_graphics/default-store-350x350.jpg"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Cart")
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
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your cart is empty",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = "Add some product to get started."
                )
            }
        } else {
            val total = cartItems.sumOf { cartItem ->
                val unitPrice =
                    cartItem.variation?.activePrice
                        ?: cartItem.product.price
                            ?.singlePrice
                            ?.activePrice
                        ?: 0.0
                unitPrice * cartItem.quantity
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    items(cartItems) {
                        cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onIncrease = {
                                onIncrease(cartItem)
                            },
                            onDecrease = {
                                onDecrease(cartItem)
                            },
                            onRemove = {
                                onRemove(cartItem)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }
                }

                Divider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Subtotal",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "৳$total",
                        fontSize = 16.sp
                    )
                }
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "৳$total",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    Modifier.height(12.dp)
                )

                TextButton(
                    onClick = {
                        // Checkout
                    },
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Checkout",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
) {
    val unitPrice =
        cartItem.variation?.activePrice
            ?: cartItem.product.price
                ?.singlePrice
                ?.activePrice
            ?: 0.0

    val lineTotal = unitPrice * cartItem.quantity

    val variationText = cartItem.variation?.titleSuffix

    Card(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                AsyncImage(
                    model = cartItem.product.image?.medium ?: CART_IMAGE,
                    contentDescription = cartItem.product.title,
                    Modifier.size(90.dp)
                )
                Spacer(
                    Modifier.size(12.dp)
                )

                Column(
                    Modifier.weight(1f)
                ) {
                    Text(
                        text = cartItem.product.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                     if (!variationText.isNullOrBlank()) {
                         Spacer(
                             Modifier.height(4.dp)
                         )

                         Text(
                             text = variationText,
                             fontSize = 14.sp
                         )
                     }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                    Text(
                        text = "Quantity: ${cartItem.quantity}",
                        fontSize = 14.sp
                    )


                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                    Text(
                        text = "৳$unitPrice × ${cartItem.quantity} = ৳$lineTotal",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        TextButton(
                            onClick = {
                                onDecrease(cartItem)
                            }
                        ) {
                            Text("-")
                        }

                        Text(
                            text = cartItem.quantity.toString(),
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = {
                                onIncrease(cartItem)
                            }
                        ) {
                            Text("+")
                        }

                        TextButton(
                            onClick = {
                                onRemove(cartItem)
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
        }
    }
}