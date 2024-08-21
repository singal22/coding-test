package com.store

import org.springframework.stereotype.Service

@Service
class ProductService {

    private val products = mutableListOf<Product>()

    fun getProductsByType(type: String?): List<Product> {
        return products.filter { type == null || it.type == type }
    }

    fun createProduct(productDetails: ProductDetails): ProductId {
        val newId = (products.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = Product(
            id = newId,
            name = productDetails.name,
            type = productDetails.type,
            inventory = productDetails.inventory,
            cost = productDetails.cost
        )
        products.add(newProduct)
        return ProductId(id = newId)
    }
}
