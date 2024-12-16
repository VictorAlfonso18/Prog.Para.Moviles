package com.example.moviles

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.moviles.ui.theme.MovilesTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import mx.sinsel.salesapplication.R

class MainActivity : ComponentActivity() {

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val qrData = result.contents
            Log.d("QR", "Data: $qrData")
            // Procesa los datos del QR y llama a Retrofit
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Botón para iniciar el escáner QR
        findViewById<Button>(R.id.scanButton).setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Escanea el código QR")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            barcodeLauncher.launch(options)
        }
    }
}
    object RetrofitClient {
        private const val BASE_URL = "http://<10.0.2.2>:3000"

        val apiService: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

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

