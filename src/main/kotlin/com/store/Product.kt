package com.store

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Min
import javax.validation.constraints.Max

data class Product(
    val id: Int,
    val name: String?,
    val type: String,
    val inventory: Int
)

data class ProductDetails @JsonCreator constructor(
    @JsonProperty("name") @field:NotBlank(message = "Name must be provided") val name: String,
    @JsonProperty("type") @field:NotBlank(message = "Type must be provided") val type: String,
    @JsonProperty("inventory") @field:Min(1, message = "Inventory must be at least 1") @field:Max(9999, message = "Inventory must be at most 9999") val inventory: Int
) {
    init {
        val allowedTypes = listOf("book", "food", "gadget", "other")
        require(type in allowedTypes) { "Type must be one of $allowedTypes" }
    }
}

data class ErrorResponseBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val path: String
)

data class ProductId(
    val id: Int
)