package com.developer.scancardview

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.google.gson.Gson


class WebAppInterface(private val activity: Activity) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun cardScanCancel() {
        Log.d("CardScan:", "cardScanCancel")
        val intent = Intent()
        intent.putExtra("RESULT", "CANCEL")
        activity.setResult(-1, intent)
        activity.finish()
    }

    @JavascriptInterface
    fun cardScanError() {
        Log.d("CardScan:", "cardScanError")
        val intent = Intent()
        intent.putExtra("RESULT", "ERROR")
        activity.setResult(-1, intent)
        activity.finish()
    }

    @JavascriptInterface
    fun cardScanSuccess(card: String) {
        if (!card.equals("undefined")){
            val result = Gson().fromJson(card, CardResult.CardResult::class.java)
            CardData.result = result
            Log.d("CardScan:", "cardScanSuccess - $card")
            val intent = Intent()
            intent.putExtra("RESULT", "SUCCESS")
            activity.setResult(-1, intent)
            activity.finish()
        }
    }
}