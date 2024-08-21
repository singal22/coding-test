package com.store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductServiceTest {

    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productService = ProductService()
    }

    @Test
    fun `getProductsByType should return all products when type is null`() {
        // Arrange
        val products = listOf(
            Product(1, "Book A", "book", 10),
            Product(2, "Gadget A", "gadget", 5)
        )
        productService.createProduct(ProductDetails("Book A", "book", 10))
        productService.createProduct(ProductDetails("Gadget A", "gadget", 5))

        // Act
        val result = productService.getProductsByType(null)

        // Assert
        assertEquals(products.size, result.size)
    }

    @Test
    fun `getProductsByType should filter products by type`() {
        // Arrange
        val book = Product(1, "Book A", "book", 10)
        val gadget = Product(2, "Gadget A", "gadget", 5)
        productService.createProduct(ProductDetails("Book A", "book", 10))
        productService.createProduct(ProductDetails("Gadget A", "gadget", 5))

        // Act
        val result = productService.getProductsByType("book")

        // Assert
        assertEquals(1, result.size)
        assertEquals(book, result.first())
    }

    @Test
    fun `createProduct should return a valid ProductId after creation`() {
        // Arrange
        val productDetails = ProductDetails("Book B", "book", 15)

        // Act
        val productId = productService.createProduct(productDetails)

        // Assert
        assertEquals(1, productId.id)
    }
}
