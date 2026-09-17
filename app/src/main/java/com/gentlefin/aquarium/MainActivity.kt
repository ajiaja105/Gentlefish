package com.gentlefin.aquarium

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var errorText: TextView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                runOnUiThread {
                    showError("[CRASH] ${sw}")
                }
                Thread.sleep(15000)
            } catch (e: Exception) {
            }
        }

        try {
            setupUi()
        } catch (t: Throwable) {
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            setContentView(buildErrorOnlyView("[CRASH SAAT SETUP] ${sw}"))
        }
    }

    private fun setupUi() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val root = FrameLayout(this)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this)
        root.addView(
            webView,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )

        val scroll = ScrollView(this)
        errorText = TextView(this).apply {
            setTextColor(Color.YELLOW)
            setBackgroundColor(Color.argb(220, 0, 0, 0))
            textSize = 11f
            setPadding(16, 16, 16, 16)
            text = ""
        }
        scroll.addView(errorText)
        root.addView(
            scroll,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP
            }
        )

        setContentView(root)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d("GentlefinWeb", "${cm.messageLevel()}: ${cm.message()} (baris ${cm.lineNumber()})")
                if (cm.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                    showError("[JS-ERROR] ${cm.message()} (baris ${cm.lineNumber()})")
                }
                return true
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                showError("[LOAD-FAIL] ${request.url} -> ${error.description}")
            }
        }

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    private fun showError(msg: String) {
        if (::errorText.isInitialized) {
            errorText.append(msg + "\n\n")
        }
    }

    private fun buildErrorOnlyView(msg: String): View {
        val scroll = ScrollView(this)
        val tv = TextView(this).apply {
            setTextColor(Color.YELLOW)
            setBackgroundColor(Color.BLACK)
            textSize = 12f
            setPadding(24, 24, 24, 24)
            text = msg
        }
        scroll.addView(tv)
        return scroll
    }

    override fun onPause() {
        super.onPause()
        if (::webView.isInitialized) {
            webView.onPause()
            webView.pauseTimers()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::webView.isInitialized) {
            webView.onResume()
            webView.resumeTimers()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.destroy()
        }
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher"))
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
