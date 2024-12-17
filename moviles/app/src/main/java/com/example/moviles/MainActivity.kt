package mx.sinsel.salesapplication

import ApiService
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.moviles.ui.theme.MovilesTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


// Retrofit Client para conectar con el backend
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000" // Cambia esto si usas una IP diferente

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class MainActivity : ComponentActivity() {

    // Configuración del escáner QR
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val qrData = result.contents
            Log.d("QR", "Data: $qrData")
            // Procesa los datos del QR aquí y envíalos a Retrofit
            sendTransactionToServer(qrData)
        } else {
            Log.d("QR", "Escaneo cancelado")
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Configuración adicional para Material3

        // Layout de XML y botón funcional
        setContentView(R.layout.activity_main)

        // Botón para iniciar el escáner QR
        findViewById<Button>(R.id.scanButton).setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Escanea el código QR")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            barcodeLauncher.launch(options)
        }

        // Lanza Jetpack Compose al iniciar la app
        setContent {
            MovilesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                )
            }
        }
    }

    // Función para enviar datos del QR al backend usando Retrofit
    private fun sendTransactionToServer(qrData: String) {
        // Aquí puedes convertir el contenido del QR a tu modelo de datos
        val transaction = Transaction(
            warehouse_id = 1,
            product_id = 2, // Esto puede depender del contenido del QR
            quantity = 10,
            type = "entrada",
            date_time = "2024-07-05T10:00:00",
            origin_destination = qrData, // Usa los datos del QR
            employee_id = 5
        )

        // Llamada POST usando Retrofit
        RetrofitClient.apiService.saveTransaction(transaction).enqueue(object :
            retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API", "Transacción enviada correctamente")
                } else {
                    Log.e("API", "Error al enviar transacción")
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e("API", "Fallo: ${t.message}")
            }
        })
    }
}

// Composable para Jetpack Compose
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovilesTheme {
        Greeting("Android")
    }
}

// Modelo para Retrofit
data class Transaction(
    val warehouse_id: Int,
    val product_id: Int,
    val quantity: Int,
    val type: String,
    val date_time: String,
    val origin_destination: String,
    val employee_id: Int
)


