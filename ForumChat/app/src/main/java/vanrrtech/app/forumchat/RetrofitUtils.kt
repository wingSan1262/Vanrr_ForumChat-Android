package vanrrtech.app.forumchat

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtils {

    fun create(): Retrofit {
        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://vanrrbackend.000webhostapp.com") // change this IP for testing by your actual machine IP
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit
    }

    fun<T> buildService(service: Class<T>, retrofit : Retrofit): T{
        return retrofit.create(service)
    }
}