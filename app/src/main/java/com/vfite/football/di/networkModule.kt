package com.vfite.football.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vfite.football.api.ApiInterface
import com.vfite.football.widget.WidgetMode
import com.vfite.football.widget.renderer.IRemoteViewRenderer
import com.vfite.football.widget.renderer.LatestNewsRenderer
import com.vfite.football.widget.renderer.MatchItemRenderer
import com.vfite.football.widget.renderer.MatchDayRenderer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val NEWS_URL_TAG = "NEWS_URL_TAG"
fun networkModule(
    context: Context,
    apiUrl: String,
    newsUrl: String
) = DI.Module("BaseNetwork") {

    bind<Moshi>() with singleton {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    bind<Retrofit>() with singleton {

        var mHttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        var mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor)
            .build()


        retrofit2.Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(MoshiConverterFactory.create(instance()))
            .client(mOkHttpClient)
            .build()
    }

    bind<String>(NEWS_URL_TAG) with provider {
        newsUrl
    }

    bind<ApiInterface>() with singleton {
        instance<Retrofit>().create(ApiInterface::class.java)
    }

    bind<IRemoteViewRenderer>(WidgetMode.NEWS) with provider {
        LatestNewsRenderer(context, instance(), instance(), newsUrl)
    }

    bind<IRemoteViewRenderer>(WidgetMode.MACTHES) with provider {
        MatchDayRenderer(context, instance(), instance())
    }

    bind<MatchItemRenderer>() with provider {
        MatchItemRenderer(context)
    }
}