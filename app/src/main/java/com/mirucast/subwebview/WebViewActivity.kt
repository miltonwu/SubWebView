package com.mirucast.subwebview

import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.LinearLayout

class WebViewActivity : AppCompatActivity() {

    private var mMainWebView: WebView? = null
    private var mSubWebView: WebView? = null
    private var mSubViewGroup: LinearLayout? = null
    private var mSubCloseBtn: ImageButton? = null

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {

        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            if (null == mSubWebView) {

                val subWebView = WebView(this@WebViewActivity)
                subWebView.setWebViewClient(WebViewClient())
                subWebView.setWebChromeClient(WebChromeClient())
                subWebView.settings.javaScriptEnabled = true
                subWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                subWebView.settings.setSupportMultipleWindows(true)
                mSubWebView = subWebView

                mSubViewGroup!!.addView(subWebView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
                mSubViewGroup!!.visibility = View.VISIBLE
                subWebView.requestFocus()

                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = mSubWebView
                resultMsg.sendToTarget()
                return true
            }
            return false
        }

        override fun onCloseWindow(window: WebView?) {
            mSubViewGroup!!.visibility = View.GONE
            mSubViewGroup!!.removeView(mSubWebView)
            mSubWebView?.onPause()
            mSubWebView = null
            mMainWebView!!.requestFocus()
            super.onCloseWindow(window)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        mMainWebView = findViewById(R.id.main_webview) as WebView?
        mMainWebView!!.setWebViewClient(mWebViewClient)
        mMainWebView!!.setWebChromeClient(mWebChromeClient)
        mMainWebView!!.settings.javaScriptEnabled = true
        mMainWebView!!.settings.javaScriptCanOpenWindowsAutomatically = true
        mMainWebView!!.settings.setSupportMultipleWindows(true)
        mMainWebView!!.loadUrl("file:///android_asset/popup.htm")

        mSubViewGroup = findViewById(R.id.sub_group) as LinearLayout?

        mSubCloseBtn = mSubViewGroup!!.findViewById(R.id.sub_close_btn) as ImageButton?
        mSubCloseBtn!!.setOnClickListener({ v -> mWebChromeClient.onCloseWindow(mSubWebView)} )
    }

    override fun onStart() {
        super.onStart()
        mMainWebView!!.onResume()
        mMainWebView!!.requestFocus()
        mSubWebView?.onResume()
        mSubWebView?.requestFocus()
    }

    override fun onStop() {
        mMainWebView!!.onPause()
        mSubWebView?.onPause()
        super.onStop()
    }

    override fun onBackPressed() {
        if (null != mSubWebView) {
            if (mSubWebView!!.canGoBack()) {
                mSubWebView!!.goBack()
                return
            } else {
                mSubCloseBtn!!.performClick()
                return
            }
        } else if (mMainWebView!!.canGoBack()) {
            mMainWebView!!.goBack()
            return
        }
        super.onBackPressed()
    }
}
