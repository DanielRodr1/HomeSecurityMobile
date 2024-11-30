package com.upao.HomeSecurity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class CamaraVigilanciaFragment : Fragment() {

    private lateinit var webView: WebView
    private val streamUrl = "https://fe66-200-121-6-232.ngrok-free.app/stream?ngrok-skip-browser-warning=1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camara_vigilancia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.webview_stream)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.loadUrl(streamUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.stopLoading()
    }
}