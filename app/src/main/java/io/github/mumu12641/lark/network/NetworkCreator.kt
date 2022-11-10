package io.github.mumu12641.lark.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "NetworkCreator"
object NetworkCreator {

    // TODO https://github.com/square/retrofit/issues/3005
    // https://github.com/Mr-lin930819/ComposeMany
    //"https://netease-cloud-music-api-self-ten.vercel.app/"

    private const val BASE_URL = "https://www.orientsky.xyz/"

    //    private val client: OkHttpClient = OkHttpClient.Builder()
//        .addInterceptor(AddCookiesInterceptor())
//        .addInterceptor(ReceivedCookiesInterceptor())
//        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
        .build()

    val networkService: NetworkService = retrofit.create(NetworkService::class.java)


}

//class ReceivedCookiesInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalResponse = chain.proceed(chain.request())
//        if (originalResponse.headers("Set-Cookie")
//                .isNotEmpty() && kv.decodeLong("neteaseId") == 0L
//        ) {
//            val cookies: HashSet<String> = HashSet()
//            for (header in originalResponse.headers("Set-Cookie")) {
//                cookies.add(header)
//            }
//            if (kv.decodeStringSet("cookie") == null) {
//                kv.encode("cookie", cookies)
//            }
//        }
//        return originalResponse
//    }
//}
//
//class AddCookiesInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val builder: Request.Builder = chain.request().newBuilder()
//        val cookies = kv.decodeStringSet("cookie")
//        if (cookies != null) {
//            for (cookie in cookies) {
//                builder.addHeader("Cookie", cookie)
//            }
//        }
//        return chain.proceed(builder.build())
//    }
//}