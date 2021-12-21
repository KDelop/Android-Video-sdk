package com.developer.cardscansdkdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.developer.cardscansdkdemo.Models.SessionResponse
import com.developer.cardscansdkdemo.Service.RetrofitClient
import com.developer.scancardview.CardData
import com.developer.scancardview.ScanCardView
import com.kaopiz.kprogresshud.KProgressHUD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(){
    lateinit var txtMemberID:TextView
    lateinit var txtGroupID: TextView
    lateinit var btnScanCard:RelativeLayout
    private var kProgressHUD: KProgressHUD? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (PermissionManager.instance.checkPermission(
                this, arrayOf(
                    Manifest.permission.INTERNET
                ), PermissionManager.INTERNET_PERMISSION
            )
        ) {
            initView()
            onScan()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionManager.INTERNET_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView()
                } else {
                    return
                }
            }
        }
    }
    fun initView(){
        txtGroupID = findViewById(R.id.txtGroupNum)
        txtMemberID = findViewById(R.id.txtMemberID)
        btnScanCard = findViewById(R.id.btnScanCard)
    }
    fun onScan(){
        btnScanCard.setOnClickListener(View.OnClickListener { view ->
            kProgressHUD = KProgressHUD.create(this@MainActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Checking Session...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
            RetrofitClient.instance.session()
                .enqueue(object: Callback<SessionResponse> {
                    override fun onResponse(call: Call<SessionResponse>, response: Response<SessionResponse>) {
                        kProgressHUD?.dismiss()
                       if (response.code() == 200){
                           val sessionResponse: SessionResponse? = response.body()
                           val session: String? = sessionResponse?.session;
                           val sessionID: String? = sessionResponse?.session_id;
                           val intent = Intent(this@MainActivity, ScanCardView::class.java)
                           CardViewRequest.launch(intent)
                       }else{
                           val sessionResponse: SessionResponse? = response.body()
                           Toast.makeText(applicationContext, sessionResponse?.message, Toast.LENGTH_LONG).show()
                       }
                    }
                    override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                        kProgressHUD?.dismiss()
                        Toast.makeText(applicationContext, "Severi is not working now, please try again.", Toast.LENGTH_LONG).show()
                    }
                })
        })
    }
    val CardViewRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val status = it.data?.getStringExtra("RESULT")
                if (status.equals("SUCCESS")){
                    val resultVal = CardData.result
                    val memberNum = resultVal?.details?.memberNumber?.value
                    val groupNum = resultVal?.details?.groupNumber?.value
                    txtMemberID.setText(memberNum)
                    txtGroupID.setText(groupNum)
                    Log.e("asdfasdf","adfasdf")
                } else if (status.equals("ERROR")){
                    Toast.makeText(applicationContext, "Card Scan is failed", Toast.LENGTH_LONG).show()
                } else if (status.equals("CANCEL")){
                    Toast.makeText(applicationContext, "Card Scan is cancled", Toast.LENGTH_LONG).show()
                }
            }
        }
}