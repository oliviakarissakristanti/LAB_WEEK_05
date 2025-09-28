package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.api.CatApiService
import com.example.lab_week_05.model.ImageData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // penting supaya Moshi paham nullable default
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private lateinit var apiResponseView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiResponseView = findViewById(R.id.api_response)
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Request failed: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                if (response.isSuccessful) {
                    val imageList = response.body()
                    Log.d(MAIN_ACTIVITY, "Full response: $imageList") // Debug log

                    val first = imageList?.firstOrNull()
                    val url = first?.imageUrl ?: "No URL"
                    val breed = first?.breeds?.firstOrNull()?.name ?: "Unknown"

                    apiResponseView.text = "Breed: $breed\nURL: $url"
                } else {
                    apiResponseView.text = "Error: ${response.errorBody()?.string().orEmpty()}"
                }
            }
        })
    }

    companion object { const val MAIN_ACTIVITY = "MAIN_ACTIVITY" }
}
