import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class Transaction(
    val warehouse_id: Int,
    val product_id: Int,
    val quantity: Int,
    val type: String,
    val date_time: String,
    val origin_destination: String,
    val employee_id: Int
)

data class StockResponse(val stock: Int)

interface ApiService {
    @POST("/transactions")
    fun saveTransaction(@Body transaction: Transaction): Call<Void>

    @GET("/stock")
    fun getStock(
        @Query("productId") productId: Int,
        @Query("warehouseId") warehouseId: Int?
    ): Call<StockResponse>
}