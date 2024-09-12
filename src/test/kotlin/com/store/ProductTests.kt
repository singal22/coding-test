package com.store.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.store.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ProductsTest {

    private lateinit var productService: ProductService
    private lateinit var objectMapper: ObjectMapper
    private lateinit var productsController: Products

    @BeforeEach
    fun setUp() {
        productService = mockk()
        objectMapper = ObjectMapper()
        productsController = Products(productService, objectMapper)
    }

    @Test
    fun `getProductsByType should return OK with products when type is valid`() {
        // Arrange
        val productType = "book"
        val products = listOf(mockk<Product>())
        every { productService.getProductsByType(productType) } returns products

        // Act
        val response = productsController.getProductsByType(productType)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(products, response.body)
        verify { productService.getProductsByType(productType) }
    }

    @Test
    fun `getProductsByType should return Bad Request with error when type is invalid`() {
        // Arrange
        val invalidType = "invalid"
        val expectedError = "Invalid product type: $invalidType"

        // Act
        val response = productsController.getProductsByType(invalidType)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body is ErrorResponseBody)
        assertEquals(expectedError, (response.body as ErrorResponseBody).error)
    }

    @Test
    fun `createProduct should return Created with product ID when request is valid`() {
        // Arrange
        val requestBody = """{"name": "iPhone", "type": "gadget", "inventory": 100, "cost": 999}"""
        val productDetails = ProductDetails("iPhone", "gadget", 100, 999)
        val productId = ProductId(1)

        every { productService.createProduct(productDetails) } returns productId

        // Act
        val response = productsController.createProduct(requestBody)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(productId, response.body)
        verify { productService.createProduct(productDetails) }
    }

    @Test
    fun `createProduct should return Bad Request with error when request is invalid`() {
        // Arrange
        val invalidRequestBody = """{"name": 123, "type": "gadget", "inventory": "one hundred"}"""
        val expectedError = "Name must be provided and must be a string"

        // Act
        val response = productsController.createProduct(invalidRequestBody)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body is ErrorResponseBody)
        assertEquals(expectedError, (response.body as ErrorResponseBody).error)
    }
}
