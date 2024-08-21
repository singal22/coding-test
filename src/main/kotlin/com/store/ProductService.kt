package com.store

import org.springframework.stereotype.Service

@Service
class ProductService {

    private val products = mutableListOf<Product>()

    fun getProductsByType(type: String?): List<Product> {
        val allowedTypes = listOf("book", "food", "gadget", "other")
        if (type != null && type !in allowedTypes) {
            throw IllegalArgumentException("Type must be one of $allowedTypes")
        }
        return products.filter { it.type == type }
    }

    fun createProduct(productDetails: ProductDetails): ProductId {
        val newId = (products.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = Product(
            id = newId,
            name = productDetails.name,
            type = productDetails.type,
            inventory = productDetails.inventory
        )
        products.add(newProduct)
        return ProductId(id = newId)
    }
}
