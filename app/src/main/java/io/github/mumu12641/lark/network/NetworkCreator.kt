package io.github.mumu12641.lark.network

import android.util.Log
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkCreator {

    //TODO https://github.com/square/retrofit/issues/3005
    // https://github.com/Mr-lin930819/ComposeMany

    private const val BASE_URL = "https://netease-cloud-music-api-self-ten.vercel.app/"
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor())
        .addInterceptor(ReceivedCookiesInterceptor())
        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    val networkService:NetworkService = retrofit.create(NetworkService::class.java)
}

class ReceivedCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty() && kv.decodeLong("neteaseId") == 0L) {
            val cookies: HashSet<String> = HashSet()
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            if (kv.decodeStringSet("cookie") == null) {
                kv.encode("cookie", cookies)
            }
            Log.d("Retrofit", "intercept: ")
        }
        return originalResponse
    }
}

class AddCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val cookies = kv.decodeStringSet("cookie")
        if (cookies != null) {
            for (cookie in cookies) {
                builder.addHeader("Cookie", cookie)
            }
            Log.d("Retrofit", "intercept: add cookie")
        }
        return chain.proceed(builder.build())
    }
}