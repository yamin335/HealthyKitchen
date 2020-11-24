package com.rtchubs.restohubs.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rtchubs.restohubs.api.*
import com.rtchubs.restohubs.api.Api.consumerKey
import com.rtchubs.restohubs.api.Api.consumerSecret
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    @Named("RestoHubs")
    fun provideHttpLoggingInterceptorForRestoHubs(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    @Named("RestoHubs")
    fun provideGsonBuilderForRestoHubs(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    @Provides
    @Singleton
    @Named("RestoHubs")
    fun provideOkHttpClientForRestoHubs(
        @Named("RestoHubs") httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(consumerKey, consumerSecret))
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("RestoHubs")
    fun provideGsonConverterFactoryForRestoHubs(@Named("RestoHubs") gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    @Named("RestoHubs")
    fun provideRetrofitBuilderForRestoHubs(
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory,
        nullOrEmptyConverterFactory: Converter.Factory,
        scalarsConverterFactory: ScalarsConverterFactory,
        @Named("RestoHubs") gsonConverterFactory: GsonConverterFactory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(Api.baseUrl)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addConverterFactory(nullOrEmptyConverterFactory)
            .addCallAdapterFactory(liveDataCallAdapterFactory)

    @Provides
    @Singleton
    fun provideRestoHubsApiService(
        @Named("RestoHubs") okHttpClient: OkHttpClient,
        @Named("RestoHubs") retrofitBuilder: Retrofit.Builder
    ): RestoHubsApiService {
        return retrofitBuilder
            .client(okHttpClient).build()
            .create(RestoHubsApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)

        /*if (BuildConfig.Server.equals("san", true))
            setUnsafeSslFactoryForClient(clientBuilder)  *//* Disabled TLS for development *//*
        else
            setSafeSslFactoryForClient(clientBuilder)  //Enabled TLS for Production
        */

        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideLiveDataCallAdapterFactory(): LiveDataCallAdapterFactory = LiveDataCallAdapterFactory()

    @Provides
    @Singleton
    fun provideScalarsConverterFactory(): ScalarsConverterFactory = ScalarsConverterFactory.create()

    //    @Provides
//    @Singleton
//    fun provideGson(): Gson =
//        GsonBuilder()
//            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//            .setLenient()
//            .create()

//    @Provides
//    @Singleton
//    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideNullOrEmptyConverterFactory(): Converter.Factory =
        object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit
            ): Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(
                    this,
                    type,
                    annotations
                )

                return Converter { body: ResponseBody ->
                    if (body.contentLength() == 0L) null
                    else nextResponseBodyConverter.convert(body)
                }
            }
        }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory,
        nullOrEmptyConverterFactory: Converter.Factory,
        scalarsConverterFactory: ScalarsConverterFactory,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(Api.API_ROOT_URL)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addConverterFactory(nullOrEmptyConverterFactory)
            .addCallAdapterFactory(liveDataCallAdapterFactory)


    @Provides
    @Singleton
    @Named("header")
    fun provideHeaderRequestInterceptor(
        preferencesHelper: PreferencesHelper
    ): Interceptor =
        Interceptor { chain ->
            val request = chain.request()

            val newBuilder = request.newBuilder()
            // let's add token if we got one
            preferencesHelper.accessToken?.let {
                newBuilder.header("AUTH_HEADER_NAME", preferencesHelper.getAccessTokenHeader())
            }

            chain.proceed(newBuilder.build())
        }

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        preferencesHelper: PreferencesHelper,
        apiService: ApiService
    ): TokenAuthenticator {
        return TokenAuthenticator(preferencesHelper, apiService)
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttpClient(
        client: OkHttpClient,
        @Named("header") headerRequestInterceptor: Interceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return client.newBuilder()
            .addInterceptor(headerRequestInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    /* Configurations For Api which doesn't require authentication */

    @Provides
    @Singleton
    fun provideApiService(
        okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): ApiService {
        return retrofitBuilder
            .client(okHttpClient).build()
            .create(ApiService::class.java)
    }

    /* Configurations For Api which requires authentication  */



    /*safe for release */

    /**
     * Enable TLS specific version V.1.2
     * Issue Details : https://github.com/square/okhttp/issues/1934
     */

//    private fun setSafeSslFactoryForClient(clientBuilder: OkHttpClient.Builder) {
//        if (Build.VERSION.SDK_INT in 16..21) {
//            var trustManager: X509TrustManager? = null
//
//            try {
//                val trustManagerFactory =
//                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//                trustManagerFactory.init(null as KeyStore?)
//                val trustManagers = trustManagerFactory.trustManagers
//                if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
//                    throw IllegalStateException(
//                        "Unexpected default trust managers:" + Arrays.toString(
//                            trustManagers
//                        )
//                    )
//                }
//                trustManager = trustManagers[0] as X509TrustManager
//            } catch (e: KeyStoreException) {
//                e.printStackTrace()
//            } catch (e: NoSuchAlgorithmException) {
//                e.printStackTrace()
//            }
//            try {
//                val sc = SSLContext.getInstance("TLSv1.2")
//                sc.init(null, null, null)
//                clientBuilder.sslSocketFactory(TLS12SocketFactory(sc.socketFactory), trustManager!!)
//
//                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .tlsVersions(TlsVersion.TLS_1_2)
//                    .build()
//
//                val specs = listOf(cs, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)
//
//                clientBuilder.connectionSpecs(specs)
//            } catch (exc: Exception) {
//                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
//            }
//
//        }
//
//        /*certificate pinning*/
//
//        /*  val certificatePinner = CertificatePinner.Builder()
//                  .add(BuildConfig.HOST, BuildConfig.SHA256_1)
//                  .add(BuildConfig.HOST, BuildConfig.SHA256_2)
//                  .add(BuildConfig.HOST, BuildConfig.SHA256_3)
//                  .build()
//          clientBuilder.certificatePinner(certificatePinner)*/
//    }

    /*unsafe for debug */
//    fun setUnsafeSslFactoryForClient(clientBuilder: OkHttpClient.Builder) {
//        val trustManager = object : X509TrustManager {
//            @Throws(CertificateException::class)
//            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            @Throws(CertificateException::class)
//            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
//        }
//
//
//        try {
//            val sc = SSLContext.getInstance("SSL")
//            sc.init(null, arrayOf(trustManager), SecureRandom())
//            clientBuilder.sslSocketFactory(TLS12SocketFactory(sc.socketFactory), trustManager)
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        } catch (e: KeyManagementException) {
//            e.printStackTrace()
//        }
//        clientBuilder.hostnameVerifier(HostnameVerifier { s, sslSession -> true })
//    }

}