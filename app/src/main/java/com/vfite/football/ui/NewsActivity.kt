package com.vfite.football.ui

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.vfite.football.di.NEWS_URL_TAG
import org.kodein.di.DI
import org.kodein.di.conf.DIGlobalAware
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance
import timber.log.Timber

class NewsActivity : AppCompatActivity(), DIGlobalAware {

    companion object {
        const val CATEGORY_NEWS_TAG = "category"
        const val NEWS_ID_TAG = "news"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myWebView = WebView(this)
        setContentView(myWebView)

        val category = intent.extras?.getString(CATEGORY_NEWS_TAG) ?: ""
        val newsId = intent.extras?.getInt(NEWS_ID_TAG) ?: 0
        if (category.isEmpty()) {
            finish()
        }
        val url = DI.global.direct.instance<String>(NEWS_URL_TAG)
            .format(category, newsId)
        Timber.d("onCreate load url = $url")
        myWebView.loadUrl(url)
    }
}