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
        }

        val intent = Intent(this,AccessbilityServiceImp::class.java)
        startService(intent)
        GameNotification.createNotificaton(this)
//        val test = test()
//        val open = resources.assets.open("temp.jpg")
//        val bitmap:Bitmap = BitmapFactory.decodeStream(open)
//        cutStarsPictrue(bitmap,detectFristStarsRect(bitmap))
//        binding.show.setImageBitmap(bitmap)
        binding.accessibilityService.setOnClickListener {
            var intentb = Intent()
            // 指定传播该值的广播名称：必须是已经注册过的才可能传值成功
            intentb.setAction(Command.ACTION_STOP)
            // 发送广播
            sendBroadcast(intentb)
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

    private fun enabled(name: String): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val serviceInfos =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        val installedAccessibilityServiceList = am.installedAccessibilityServiceList
        for (info in installedAccessibilityServiceList) {
            Log.d("MainActivity", "all -->" + info.id)
            if (name == info.id) {
                return true
            }
        }
        return false
    }

    private fun detectFristStarsRect(bitmap:Bitmap):ArrayList<Rect>{
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val src = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, src)

        val dst = Mat.zeros(Size(src.width().toDouble(), src.height().toDouble()), CvType.CV_8UC3)
        // 輪郭を描画 (Yellow)
        var color = Scalar(255.0, 255.0, 0.0)

        //灰色化
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY)
        //Gaussian Filters
        Imgproc.GaussianBlur(src, src, Size(1.0, 1.0), 0.0, 0.0)
        //二値化
        Imgproc.threshold(
            src, src, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )
        //比特反转
        val hierarchy = Mat.zeros(Size(1.0, 1.0), CvType.CV_8UC1)

        Core.bitwise_not(src, src)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()

        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_TC89_L1
        )

        Imgproc.drawContours(dst, contours, -1, color, 1)

        val list = ArrayList<Point>()

        var box: Rect?
        val boxs = ArrayList<Rect>()

        for (i in contours.indices) {

            // 去除小轮廓 (ノイズ除去)
            if (Imgproc.contourArea(contours.get(i)) < dst.size().area() / 1000) {
                continue
            }

            val ptmat: MatOfPoint = contours[i]
            // 轮廓的重心的绘制 (Red)
            color = Scalar(255.0, 0.0, 0.0)
            val ptmat2 = MatOfPoint2f(*ptmat.toArray())
            val bbox = Imgproc.minAreaRect(ptmat2)
            box = bbox.boundingRect()
            if (box.width > box.height * 5 && box.width < box.height * 9 && box.height > 50) {

                Imgproc.circle(dst, bbox.center, 5, color, -1)
                list.add(bbox.center)

                // 周围轮廓四角形绘制 (Green)
                color = Scalar(0.0, 255.0, 0.0)
                Imgproc.rectangle(dst, box.tl(), box.br(), color, 2)

                boxs.add(box)
            }
        }

        //去重复的
        val temp = ArrayList<Rect>()
        temp.addAll(boxs)
        boxs.map {
            val rect1 = it
            var c = 0
            boxs.map {
                val isContainer = it.contains(rect1.tl()) || it.contains(rect1.br())
                if(isContainer){
                    c+=1
                    if(c > 1){
                        temp.remove(rect1)
                    }
                }
            }
        }
        Log.e("test","bitmap1 boxs= "+temp.size)
        return temp
    }

    fun cutPictrue():Bitmap{
        val open = resources.assets.open("temp.jpg")
        val bitmap:Bitmap = BitmapFactory.decodeStream(open)

        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val mRgb = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, mRgb)
        //灰色化
        val rect = Rect(80, 30, 340, 390)

        Imgproc.cvtColor(mRgb, mRgb, Imgproc.COLOR_BGR2RGB)
        val rectMat = Mat()
         mRgb.copyTo(rectMat)
        val bgdModel = Mat.zeros(1, 65, CvType.CV_64FC1)
        val fgdModel = Mat.zeros(1, 65, CvType.CV_64FC1)
        val mask = Mat.zeros(mRgb.size(), CvType.CV_8UC1)
        Imgproc.grabCut(mRgb, mask, rect, bgdModel, fgdModel, 3, Imgproc.GC_INIT_WITH_RECT)

        val result = Mat()
        for (i in 0 until mask.rows()) {
            for (j in 0 until mask.cols()) {
                val value = mask.get(i, j)[0].toInt()
                if (value == 1 || value == 3) {
                    mask.put(i, j, 255.0)
                } else {
                    mask.put(i, j, 0.0)
                }
            }
        }
        Core.bitwise_and(mRgb, mRgb, result, mask)
        val bitmapTemp = Bitmap.createBitmap(rect.width,rect.height,Bitmap.Config.ARGB_8888)

        Utils.matToBitmap(result, bitmapNew)
//        bitmaps.add(bitmapTemp)
        Log.e("test","bitmap1 = bitmapTemp "+bitmapNew)
        return bitmapNew

    }

    private fun reSizeMat(src: Mat): Mat? {
        val rect = Rect(0, 0, 500, 500)
        return Mat(src, rect)
    }

    fun cutStarsPictrue(bitmap:Bitmap,list: ArrayList<Rect>):Int{
        var num = 0

        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val mRgb = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, mRgb)
        list.map {
            val rect = Rect(it.x+15, it.y+8, it.width-35, it.height-18)
            val src = Mat(mRgb,rect)

            val b = Bitmap.createBitmap(
                src!!.cols(), src.rows(),
                Bitmap.Config.ARGB_8888
            )

            Utils.matToBitmap(src, b)

            num = returnStarsNum(b)
            Log.e("test","bitmap1 = num "+num)
        }
        return num
    }

    fun returnStarsNum(bitmap:Bitmap): Int {
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //Mat変換
        val src = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, src)
        //灰色化
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY)
        //Gaussian Filters
        Imgproc.GaussianBlur(src, src, Size(1.0, 1.0), 0.0, 0.0)
        //二値化
        Imgproc.threshold(
            src, src, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )
        //比特反转
        val hierarchy = Mat.zeros(Size(1.0, 1.0), CvType.CV_8UC1)

        Core.bitwise_not(src, src)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()
        val dst = Mat.zeros(Size(src.width().toDouble(), src.height().toDouble()), CvType.CV_8UC3)

        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_TC89_L1
        )

        var count = 0
        if(contours.size > 7){
            return count
        }
        for (i in contours.indices) {

            // 去除小轮廓 (ノイズ除去)
            if (contours.get(i).rows() < 25) {
                continue
            }
            count+=1
        }
//        Utils.matToBitmap(src,bitmapNew)
        return count
    }

    private fun startSnapShoot(): Bitmap {
        val view1 = window.decorView
        val bitmap = Bitmap.createBitmap(view1.width, view1.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap!!)
        view1.draw(canvas)
        return bitmap
    }
}