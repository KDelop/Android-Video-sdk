package com.developer.scancardview

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal interface MyCallback {
    fun onSuccess()
}


class ScanCardView : AppCompatActivity() {
    lateinit var webView :WebView
    private val PERMISSONS_REQUEST_CODE = 1240
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_card_view)
        isPermissionGranted()
    }
    private fun isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                initView()
            } else {
                requestPermission(this@ScanCardView)
            }
        }
    }

    fun checkPermission(): Boolean {
        val cameraResult = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        )
        val recordAudioResult = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        val StorageResult = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val storageReadResult = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return cameraResult == PackageManager.PERMISSION_GRANTED && recordAudioResult ==
                PackageManager.PERMISSION_GRANTED && StorageResult == PackageManager.PERMISSION_GRANTED && storageReadResult == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSONS_REQUEST_CODE
        )
        initView()
    }
    fun initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(false)
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                Log.d("CardScan", request.toString())
                request.grant(request.resources)
            }

            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Log.d("CardScan JS", "${message.message()} -- From line " +
                        "${message.lineNumber()} ")
                return true
            }
        })

        //request camera permission
        val permission = Manifest.permission.CAMERA
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView, weburl: String) {
                Log.d("CardScan", "Webview is loaded")
                //Load view with session token, live and debug flag
                webView.post {
                    webView.evaluateJavascript("window.A2R.loadView('secret_test_1KdPhlL22cyiL2EB', false, false)") {
                        Log.d("CardScan:", "evaluateJavascript $it")
                    }
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }
        }

        webView.addJavascriptInterface(WebAppInterface(this), "R2A")
        val versionAPI = Build.VERSION.SDK_INT
        val versionRelease = Build.VERSION.RELEASE
        webView.loadUrl("https://sandbox.cardscan.ai/v1/android?os=$versionRelease&api=$versionAPI&package=1.5")
    }

}