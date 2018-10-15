package com.yoshizuka.mymovies.models.services

import com.yoshizuka.mymovies.BuildConfig
import com.yoshizuka.mymovies.models.entities.Movie
import com.yoshizuka.mymovies.models.entities.Response
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



interface MovieApiService {

    @GET("/3/search/multi")
    fun getMovies(@Query("query") query: String,
                  @Query("language") language: String = "fr-FR",
                  @Query("api_key") apiKey: String = "f84b24b67ea33c06d611313aeb6d2f4e")
            : Observable<Response>

    companion object {
        fun create() : MovieApiService {
            val httpClient = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY

                httpClient.addInterceptor(logging)
            }

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .baseUrl("https://api.themoviedb.org/")
                .build()
            return retrofit.create(MovieApiService::class.java)
        }
    }
}