package com.raysharp.accessbilityserviceapplication

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Display
import androidx.annotation.RequiresApi
import com.raysharp.accessbilityserviceapplication.service.AccessbilityServiceImp
import com.raysharp.accessbilityserviceapplication.service.ScrollData
import java.util.ArrayList


/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * LocalBroadcastReceiver
 * @author longyanghe
 * @date 2022-07-11
 */
class LocalBroadcastReceiver: BroadcastReceiver() {
    private var isSlidingScreen = false
    private var status: ArrayList<Int>? = null
    var accessbilityServiceImp: AccessbilityServiceImp? = null
    var slidingValue = -380f

    @RequiresApi(Build.VERSION_CODES.R)
    var mCallBack:AccessibilityService.TakeScreenshotCallback = object :AccessibilityService.TakeScreenshotCallback{


        override fun onSuccess(screenshotResult: AccessibilityService.ScreenshotResult) {

            val nodeInfo = accessbilityServiceImp?.rootInActiveWindow
            val result = nodeInfo?.let { accessbilityServiceImp?.findNodeInfosByText(it,"任务") }
            Log.e("AccessbilityServiceImp","onSuccess = "+screenshotResult+",result = "+result)

            var timespec = 500L
            val hardwareBuffer = screenshotResult.hardwareBuffer
            val colorSpace = screenshotResult.colorSpace
            if (hardwareBuffer.width > 0 && hardwareBuffer.height > 0 && colorSpace != null) {
                val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, colorSpace)
                if (bitmap != null) {
                    val starsNum = ScreenShootDealwith.getStarsNum(bitmap)
                    var canStartTasks = ScreenShootDealwith.cutTaskContent(bitmap,starsNum.filter { it.stars == 3 })
                    canStartTasks += starsNum.filter { it.stars > 3 }
                    canStartTasks -= ScreenShootDealwith.cutTaskContent(bitmap,starsNum.filter { it.stars > 3 && it.stars < 6})
                    Log.e("AccessbilityServiceImp","starsNum = "+starsNum+",canStartTasks = "+canStartTasks)
                    canStartTasks.map {
                        accessbilityServiceImp?.clickScreen(
                            it.rect.x.toFloat(),
                            it.rect.y.toFloat(),timespec
                        )
                        timespec+=1000L
                        //点击一键上阵
                        accessbilityServiceImp?.clickScreen(
                            accessbilityServiceImp!!.width / 2,
                            accessbilityServiceImp!!.height / 10 * 9,timespec
                        )
                        timespec+=1000L

                        //点击开始任务
                        accessbilityServiceImp?.clickScreen(
                            accessbilityServiceImp!!.width / 3 * 2,
                            accessbilityServiceImp!!.height / 10 * 9,timespec
                        )
                        timespec+=1000L
                    }

                    if(starsNum.size > 4){
                        accessbilityServiceImp?.slidingScreen(
                            ScrollData(true,slidingValue,0f)
                        ,timespec)
                        timespec+=3500L

                        slidingValue = slidingValue + canStartTasks.size *110f
                        isSlidingScreen = true
                        Handler().postDelayed(
                            {startSnapShoot()}, timespec
                        )
                        return
                    }
                    if (isSlidingScreen){
                            //点击刷新
                            accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/3*2
                                ,accessbilityServiceImp!!.height/10*9.3f,timespec)
                        timespec+=3500L

                        Handler().postDelayed(
                                {startSnapShoot()}, timespec
                            )
                        isSlidingScreen = false
                        return
                    }
                    if(starsNum.size <= 4 && starsNum.size > 0) {
                        //点击刷新
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/3*2,
                            accessbilityServiceImp!!.height/10*9.3f,timespec)
                        timespec+=3500L
                        Handler().postDelayed(
                            {startSnapShoot()}, timespec
                        )
                        return
                    }

                    if (starsNum.size == canStartTasks.size){
                        //点击一键领取
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/3*2.2f,accessbilityServiceImp!!.height/7)
                        //确定
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/3*1.8f,accessbilityServiceImp!!.height/3*2.1f)
                        //收取
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/10.5f,accessbilityServiceImp!!.height/10.5f)
                        //返回
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/10.5f,accessbilityServiceImp!!.height/10.5f)
                        //领取奖励
                        accessbilityServiceImp?.clickScreen(accessbilityServiceImp!!.width/4*3,accessbilityServiceImp!!.height/2)
                    }
                }
            }
        }

        override fun onFailure(p0: Int) {
            Log.e("AccessbilityServiceImp","onFailure = "+p0)
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("AccessbilityServiceImp","onReceive = "+p1?.action)
        if(accessbilityServiceImp == null ){
            accessbilityServiceImp = p0 as AccessbilityServiceImp
        }
        accessbilityServiceImp!!.timespec = 2000L
        when(p1?.action){
            Command.ACTION_MODIFTY->{
                status = p1.getIntegerArrayListExtra("status")

            }
            Command.ACTION_START->{
                slidingValue = -380f
                val displayMetrics = p0?.resources?.displayMetrics
                accessbilityServiceImp!!.width = displayMetrics?.widthPixels?.toFloat()!!
                accessbilityServiceImp!!.height = displayMetrics?.heightPixels?.toFloat()!!

                //消息栏收起
                accessbilityServiceImp!!.collapseStatusBar(context = p0)
                val nodeInfo = accessbilityServiceImp!!.rootInActiveWindow


                if(status?.get(0) == 1){
                    accessbilityServiceImp!!.openDailyTask()
                }
                if(status?.get(1) == 1){
                    accessbilityServiceImp!!.commonInviteTask()
                }
                if(status?.get(2) == 1){
                    accessbilityServiceImp!!.surviveTask()
                }
                if(status?.get(3) == 1){
                    accessbilityServiceImp!!.friendTask()
                }
                if(status?.get(4) == 1){
                    accessbilityServiceImp!!.lunckwheelTask()
                }
                if(status?.get(5) == 1){
                    accessbilityServiceImp!!.challengeInstanceZonesTask()
                }
                if(status?.get(6) == 1){
                    accessbilityServiceImp!!.highlevelInviteTask()
                }
                if(status?.get(7) == 1){
                    accessbilityServiceImp!!.sportsArenaTask()
                }
                if(status?.get(8) == 1){
                    accessbilityServiceImp!!.searchLevelTask()
                }

                if(status?.get(9) == 1){
                    accessbilityServiceImp!!.startTaskBar({startSnapShoot()})
                }
            }
            Command.ACTION_STOP->{
                for (i in 0..65){
                    accessbilityServiceImp!!.mHandler.removeCallbacksAndMessages(i)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startSnapShoot() {
        accessbilityServiceImp!!.mHandler.postDelayed({
            accessbilityServiceImp!!.takeScreenshot(
                Display.DEFAULT_DISPLAY,
                accessbilityServiceImp!!.mainExecutor,mCallBack)
        },500)
    }
}