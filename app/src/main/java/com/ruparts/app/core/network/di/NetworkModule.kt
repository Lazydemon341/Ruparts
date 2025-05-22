package com.ruparts.app.core.network.di

// Remove BuildConfig import as it's causing issues
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.network.interceptor.AuthInterceptor
import com.ruparts.app.features.authorization.data.network.AuthRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://stage.ruparts.ru/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideAuthRetrofitService(retrofit: Retrofit): AuthRetrofitService {
        return retrofit.create(AuthRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun provideEndpointRetrofitService(retrofit: Retrofit): EndpointRetrofitService {
        return retrofit.create(EndpointRetrofitService::class.java)
    }
}
