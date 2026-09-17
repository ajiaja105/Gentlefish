package com.gentlefin.aquarium

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader

/**
 * App biasa (bukan wallpaper) - WebView di dalam Activity normal.
 * Ini JAUH lebih stabil dibanding pendekatan WallpaperService yang
 * kemarin gagal, karena WebView di sini benar2 attached ke window
 * sungguhan, jadi WebGL/Three.js bisa bikin context dgn normal -
 * sama persis kondisinya kayak waktu kamu buka situs Netlify di
 * browser (yang sudah terbukti lancar 2+ menit tanpa masalah).
 *
 * CATATAN: index.html ini pakai <script type="module"> (import Three.js
 * dkk). Browser/WebView MEMBLOKIR loading module script lewat file://
 * (ini persis error "[ERROR] Access to script blocked" yang muncul di
 * proyek wallpaper kemarin). Makanya di sini dipakai WebViewAssetLoader
 * (cara resmi dari Google) yang menyajikan folder assets/ lewat alamat
 * https://appassets.androidplatform.net/ secara lokal - jadi dianggap
 * browser sebagai origin https biasa, bukan file://, tanpa perlu internet
 * sungguhan (semua tetap diambil dari dalam APK).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen, sembunyikan status bar & nav bar spy pengalaman
        // main lebih immersive (opsional, bisa dihapus kalau tidak mau).
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
        webView.webChromeClient = WebChromeClient() // wajib utk console.log & beberapa fitur WebGL

        // index.html = salinan skrip akuarium (Gfv9.html), sudah tanpa
        // guardedGlbUrl - lihat catatan di ASET_YANG_HARUS_DITAMBAH.md
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        webView.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        webView.resumeTimers()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher"))
    override fun onBackPressed() {
        // Kalau ada history navigasi di dalam WebView, mundur dulu;
        // kalau tidak ada, baru keluar app spt biasa.
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
