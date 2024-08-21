package com.store.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.store.ErrorResponseBody
import com.store.ProductDetails
import com.store.ProductId
import com.store.ProductService
import java.time.LocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.ValidationException

@RestController
@RequestMapping("/products")
class Products(private val productService: ProductService) {

    private val objectMapper = ObjectMapper()

    @GetMapping
    fun getProductsByType(@RequestParam(required = false) type: String?): ResponseEntity<Any> {
        return try {
            if (type != null) {
                val allowedTypes = listOf("book", "food", "gadget", "other")
                if (type !in allowedTypes) {
                    throw IllegalArgumentException("Invalid product type: $type")
                }
            }
            val products = productService.getProductsByType(type)
            ResponseEntity.ok(products)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ErrorResponseBody(
                    timestamp = LocalDateTime.now().toString(),
                    status = 400,
                    error = e.message ?: "Invalid type parameter",
                    path = "/products"
                )
            )
        }
    }

    @PostMapping
    fun createProduct(@RequestBody requestBody: String): ResponseEntity<Any>{
        return try {
            val jsonNode: JsonNode = objectMapper.readTree(requestBody)

            // Validate the name field
            val nameNode = jsonNode.get("name")
            val type = jsonNode.get("type")?.asText() ?: throw IllegalArgumentException("Type must be provided")
            val inventoryNode = jsonNode.get("inventory")


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

            // Create ProductDetails
            val productDetails = ProductDetails(name = name, type = type, inventory = inventory)
            val productId = productService.createProduct(productDetails)

            ResponseEntity.status(201).body(productId)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ErrorResponseBody(
                    timestamp = LocalDateTime.now().toString(),
                    status = 400,
                    error = e.message ?: "Invalid product details",
                    path = "/products"
                )
            )
        }
    }
}
