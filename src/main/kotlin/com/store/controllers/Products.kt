package com.store.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.store.ErrorResponseBody
import com.store.ProductDetails
import com.store.ProductId
import com.store.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/products")
class Products(private val productService: ProductService, private val objectMapper: ObjectMapper) {

    @GetMapping
    fun getProductsByType(@RequestParam(required = false) type: String?): ResponseEntity<Any> {
        return try {
            type?.let { validateProductType(it) }
            val products = productService.getProductsByType(type)
            ResponseEntity.ok(products)
        } catch (e: IllegalArgumentException) {
            buildErrorResponse(e.message ?: "Invalid type parameter", "/products")
        }
    }

    @PostMapping
    fun createProduct(@Valid @RequestBody requestBody: String): ResponseEntity<Any> {
        return try {
            val productDetails = parseAndValidateRequest(requestBody)
            val productId = productService.createProduct(productDetails)
            ResponseEntity.status(201).body(productId)
        } catch (e: IllegalArgumentException) {
            buildErrorResponse(e.message ?: "Invalid product details", "/products")
        }
    }

    private fun validateProductType(type: String) {
        val allowedTypes = listOf("book", "food", "gadget", "other")
        require(type in allowedTypes) { "Invalid product type: $type" }
    }

    private fun parseAndValidateRequest(requestBody: String): ProductDetails {
        val jsonNode: JsonNode = objectMapper.readTree(requestBody)

        // Validate the name field
        val nameNode = jsonNode.get("name")
        val type = jsonNode.get("type")?.asText() ?: throw IllegalArgumentException("Type must be provided")
        val inventoryNode = jsonNode.get("inventory")
        val costNode = jsonNode.get("cost")


        // Check if name is null or not a string
        if (nameNode == null || !nameNode.isTextual) {
            throw IllegalArgumentException("Name must be provided and must be a string")
        }
        val name = nameNode.asText()

        // Check if inventory is an integer and within the valid range
        if (inventoryNode == null || !inventoryNode.isInt) {
            throw IllegalArgumentException("Inventory must be provided and must be an integer")
        }
        val inventory = inventoryNode.asInt()
        if (inventory < 1 || inventory > 9999) {
            throw IllegalArgumentException("Inventory must be between 1 and 9999")
        }

        // Validate cost
        val cost = when {
            costNode == null || costNode.isNull -> null
            costNode.isInt && costNode.asInt() > 0 -> costNode.asInt()
            else -> throw IllegalArgumentException("Cost must be an integer if provided and greater than 0")
        }

        return ProductDetails(name = name, type = type, inventory = inventory, cost = cost)
    }

    private fun buildErrorResponse(errorMessage: String, path: String): ResponseEntity<Any> {
        val errorResponse = ErrorResponseBody(
            timestamp = LocalDateTime.now().toString(),
            status = 400,
            error = errorMessage,
            path = path
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }
}
