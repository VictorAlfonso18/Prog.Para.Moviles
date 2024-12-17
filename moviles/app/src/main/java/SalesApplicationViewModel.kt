package mx.sinsel.salesapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Base64

// Clase personalizada para Product (si la librería externa no tiene las propiedades necesarias)
data class Product(
    var upc: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var category: Category? = null,
    var image: String = ""
)

data class Category(
    var id: Int = 0,
    var name: String = ""
)

class SalesApplicationViewModel : ViewModel() {

    private val _productState = mutableStateOf(ProductState())
    var productState: State<ProductState> = _productState

    var bitmap: Bitmap? = null

    private val _categoriesState = mutableStateOf(CategoryState())
    val categoriesState: State<CategoryState> = _categoriesState

    val productView = Product()
    var productDescription = mutableStateOf("")
    var productPrice = mutableStateOf("")
    var productQuantity = mutableStateOf("")
    var productUpc = mutableStateOf("")
    var productImage = mutableStateOf<Bitmap?>(null)
    var productCategory = mutableStateOf(Category())
    var messageProductDescError = mutableStateOf(false)

    init {
        fetchCategories()
        println("fetching categories")
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = listOf<Category>() // Simulando respuesta del servicio
                _categoriesState.value = _categoriesState.value.copy(
                    categories = response,
                    loading = false,
                    error = null
                )
                println("categories fetched")
            } catch (e: Exception) {
                _categoriesState.value = _categoriesState.value.copy(
                    loading = false,
                    error = "Error fetching Categories ${e.message}"
                )
                println("categories fetch failed: ${e.message}")
            }
        }
    }

    data class ProductState(
        val loading: Boolean = false,
        val product: Product? = null, // Permitir valores nulos
        val error: String? = null
    )

    data class CategoryState(
        val loading: Boolean = true,
        val categories: List<Category> = emptyList(),
        val error: String? = null
    )

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun consultProduct() {
        if (productUpc.value.isNotEmpty()) {
            try {
                val response = Product(
                    upc = productUpc.value,
                    description = "Sample Description",
                    price = 100.0,
                    quantity = 10,
                    category = Category(1, "Sample Category"),
                    image = ""
                ) // Simulando una respuesta
                _productState.value = _productState.value.copy(
                    product = response,
                    loading = false,
                    error = null
                )
                println("Product fetched successfully")
            } catch (e: Exception) {
                _productState.value = _productState.value.copy(
                    loading = false,
                    error = e.message
                )
                println("Product fetched failed: ${e.message}")
            }
        } else {
            messageProductDescError.value = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createProduct() {
        productView.upc = productUpc.value
        productView.description = productDescription.value
        productView.price = productPrice.value.toDoubleOrNull() ?: 0.0
        productView.quantity = productQuantity.value.toIntOrNull() ?: 0
        productView.category = productCategory.value

        when {
            productImage.value != null -> {
                val byteArrayOutputStream = ByteArrayOutputStream()
                productImage.value?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val encoded: String = Base64.getEncoder().encodeToString(byteArray)
                productView.image = encoded
                println("Image: with image")
            }
            else -> {
                productView.image = ""
                println("Image: without image")
            }
        }

        try {
            println("Trying product creation: ${productView}")
            // Simula el servicio
            println("Product created successfully")
        } catch (e: Exception) {
            println("Product creation failed: ${e.message}")
        }
    }
}

// Funciones auxiliares para conversión de imágenes
fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
