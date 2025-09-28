package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.api.CatApiService
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy { findViewById(R.id.api_response) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Request failed: ${t.message}"
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    apiResponseView.text = response.body()
                } else {
                    apiResponseView.text = "Error: ${response.errorBody()?.string().orEmpty()}"
                    Log.e(MAIN_ACTIVITY, "Failed: ${response.code()}")
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}
