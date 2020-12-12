package com.zxj.netlibrary


import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * desc   :
 * date   : 2020/08/05
 * version: 1.0
 */
open class BaseRetrofitClient {

    companion object {
        private const val TIME_OUT = 5

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BaseRetrofitClient() }
    }

    val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            handleBuilder(builder)
//            val logging = HttpLoggingInterceptor(HttpLogger())
//            if (BuildConfig.DEBUG) {
//                logging.level = HttpLoggingInterceptor.Level.BODY
//            } else {
//                logging.level = HttpLoggingInterceptor.Level.BASIC
//            }
//            builder.addInterceptor(logging)
            return builder.build()
        }

    protected open fun handleBuilder(builder: OkHttpClient.Builder) {
        val httpCacheDirectory = File(NetContext.instance.getContext().cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)
        builder.cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                if (!NetWorkUtils.isNetworkAvailable(NetContext.instance.getContext())) {
                    request = request.newBuilder()
                        // CacheControl.FORCE_CACHE:强制使用缓存,如果没有缓存数据,则抛出504(only-if-cached)
                        // CacheControl.FORCE_NETWORK:强制使用网络,不使用任何缓存.
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build()
                }
                val response = chain.proceed(request)
                if (!NetWorkUtils.isNetworkAvailable(NetContext.instance.getContext())) {
                    val maxAge = 60 * 60
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }
                response
            }
    }

    fun <S> getService(serviceClass: Class<S>, baseUrl: String): S {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build().create(serviceClass)
    }
}
