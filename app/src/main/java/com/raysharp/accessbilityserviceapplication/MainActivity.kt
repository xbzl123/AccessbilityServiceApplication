package com.raysharp.accessbilityserviceapplication
//    implementation 'com.quickbirdstudios:opencv:3.4.1'
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raysharp.accessbilityserviceapplication.databinding.ActivityMainBinding
import com.raysharp.accessbilityserviceapplication.service.AccessbilityServiceImp
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.Settings
import android.util.Log

import android.view.accessibility.AccessibilityManager
import android.widget.CheckBox
import org.opencv.imgproc.Imgproc

import org.opencv.android.OpenCVLoader

import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Environment
import android.provider.Settings.SettingNotFoundException
import android.view.SurfaceControl
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Mat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.text.TextUtils
import android.text.TextUtils.SimpleStringSplitter
import com.blankj.utilcode.util.ToastUtils


class MainActivity : AppCompatActivity() {

    private lateinit var mRgb: Mat
    private val serviceConn: ServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("MainActivity", "onServiceConnected -->" )
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("MainActivity", "onServiceDisconnected -->" )
        }

    }
    private lateinit var binding: ActivityMainBinding

    var allCheckBoxs = arrayListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allCheckBoxs.add(binding.dialyTask)
        allCheckBoxs.add(binding.commonInvite)
        allCheckBoxs.add(binding.surviveTask)
        allCheckBoxs.add(binding.friendTask)
        allCheckBoxs.add(binding.luckTask)
        allCheckBoxs.add(binding.challengeTask)
        allCheckBoxs.add(binding.hightInvite)
        allCheckBoxs.add(binding.sportsarenaTask)
        allCheckBoxs.add(binding.searchTask)
        allCheckBoxs.add(binding.taskBar)


        AutoTouch.width = this.windowManager.defaultDisplay.width
        AutoTouch.height = this.windowManager.defaultDisplay.height
//        val order = arrayOf("input", "tap", "" + 500.0, "" + 1500.0)
        Log.d("MainActivity", "checkRootPermission -->" +
                AutoTouch.width+",AutoTouch.height = "+AutoTouch.height)
        if (!isAccessibilitySettingsOn(this)){
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.allCheckboxStates.setOnCheckedChangeListener { compoundButton, b ->
            allCheckBoxs.forEach {
                it.setChecked(b)
            }
        }

        binding.uploadModifty.setOnClickListener {
            val status = getAllCheckBoxState()
            var intentb = Intent()
            // 设置传播的键值对：
            intentb.putIntegerArrayListExtra("status", status)
            // 指定传播该值的广播名称：必须是已经注册过的才可能传值成功
            intentb.setAction(Command.ACTION_MODIFTY)
            // 发送广播
            sendBroadcast(intentb)
            ToastUtils.showShort("提交成功")
        }

        val intent = Intent(this,AccessbilityServiceImp::class.java)
        startService(intent)
        GameNotification.createNotificaton(this)
        binding.accessibilityService.setOnClickListener {
            var intentb = Intent()
            // 指定传播该值的广播名称：必须是已经注册过的才可能传值成功
            intentb.setAction(Command.ACTION_STOP)
            // 发送广播
            sendBroadcast(intentb)
            ToastUtils.showShort("停止成功")
        }
    }


    override fun onResume() {
        super.onResume()
    }
    private fun getAllCheckBoxState(): ArrayList<Int>{
        val states = ArrayList<Int>()
        if(!allCheckBoxs.isEmpty()){
            states.addAll(allCheckBoxs.map {
                if (it.isChecked)
                    1
                else
                    0
            }.toList())
        }
        return states
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConn)
    }


    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        // TestService为对应的服务
        val service = packageName + "/" + AccessbilityServiceImp::class.java.getCanonicalName()
        Log.i(TAG, "service:$service")
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.v(
                        TAG,
                        "-------------- > accessibilityService :: $accessibilityService $service"
                    )
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(
                            TAG,
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }

    private fun startSnapShoot(): Bitmap {
        val view1 = window.decorView
        val bitmap = Bitmap.createBitmap(view1.width, view1.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap!!)
        view1.draw(canvas)
        return bitmap
    }
}