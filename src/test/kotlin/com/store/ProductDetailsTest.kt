import com.store.ProductDetails
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductDetailsTest {

    @Test
    fun `should create ProductDetails when inputs are valid`() {
        val productDetails = ProductDetails(
            name = "iPhone",
            type = "gadget",
            inventory = 10,
            cost = 1000
        )

        assertEquals("iPhone", productDetails.name)
        assertEquals("gadget", productDetails.type)
        assertEquals(10, productDetails.inventory)
        assertEquals(1000, productDetails.cost)
    }


    @Test
    fun `should throw exception when type is invalid`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            ProductDetails(
                name = "iPhone",
                type = "invalid-type",
                inventory = 10
            )
        }

        assertEquals("Type must be one of [book, food, gadget, other]", exception.message)
    }



    @Test
    fun `should allow null cost`() {
        val productDetails = ProductDetails(
            name = "iPhone",
            type = "gadget",
            inventory = 10,
            cost = null
        )

        assertEquals(null, productDetails.cost)
    }
}
