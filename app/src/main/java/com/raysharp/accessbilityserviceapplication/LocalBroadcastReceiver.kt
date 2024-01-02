package com.raysharp.accessbilityserviceapplication

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Display
import androidx.annotation.RequiresApi
import com.raysharp.accessbilityserviceapplication.service.AccessbilityServiceImp
import com.raysharp.accessbilityserviceapplication.service.ScrollData
import org.opencv.core.Mat
import java.util.*
import kotlin.collections.ArrayList


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
    var list = ArrayList<Mat>()
    var fightedPlayers = ArrayList<Long>()
    var person_challege_count: Int = 0


    @RequiresApi(Build.VERSION_CODES.R)
    var mCallBack:AccessibilityService.TakeScreenshotCallback = object :AccessibilityService.TakeScreenshotCallback{

        override fun onSuccess(screenshotResult: AccessibilityService.ScreenshotResult) {

            val nodeInfo = accessbilityServiceImp?.rootInActiveWindow
            val result = nodeInfo?.let { accessbilityServiceImp?.findNodeInfosByText(it,"任务") }
            Log.e("AccessbilityServiceImp","onSuccess = "+screenshotResult+",result = "+result)

            var timespec = 500L
            accessbilityServiceImp!!.timespec = 1000L
            val hardwareBuffer = screenshotResult.hardwareBuffer
            val colorSpace = screenshotResult.colorSpace
            if (hardwareBuffer.width > 0 && hardwareBuffer.height > 0 && colorSpace != null) {
                val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, colorSpace)
                if (bitmap != null) {
                    if (status?.get(0) == 200){
                        if (fightedPlayers.size >= status?.get(1)!!){
                            return
                        }
//                        val skipFight = ScreenShootDealwith.getSkipFight(bitmap)
//                        Log.e("AccessbilityServiceImp","skipFight = "+skipFight)
                        val result = ScreenShootDealwith.detectNumberRect(bitmap,list).sortedBy { it.strength }
                        Log.e("AccessbilityServiceImp", "result=" + result+",fightedPlayers ="+fightedPlayers)
                        if (result.isNotEmpty()){
                            for (index in result.indices){
                                val item = result[index]
                                val filterlist = fightedPlayers.filter { it == item.strength }
                                if (filterlist.size < 6){
                                    fightedPlayers.add(item.strength)
                                    accessbilityServiceImp?.winnerSportsArenaTask2(item.index)
                                } else {
                                    accessbilityServiceImp?.sportsArenaRefreshAndSnapShoot()
                                    continue
                                }
                            }
                        }else{
                            accessbilityServiceImp?.sportsArenaRefreshAndSnapShoot()
                        }
                        return
//                        ScreenShootDealwith.detectColorRect(bitmap)
                    }else if (status?.get(7) != 0){
                        if (fightedPlayers.size >= person_challege_count){
                            return
                        }

                        val result = ScreenShootDealwith.detectNumberRect(bitmap,list)
                        Log.e("AccessbilityServiceImp", "result =" +  result)

                        if (result.isNotEmpty()){
                            for (index in result.indices){
                                val item = result[index]
                                val filterlist = fightedPlayers.filter { it == item.strength }
                                if (filterlist.size < 6){
                                    fightedPlayers.add(item.strength)
                                    accessbilityServiceImp?.sportsArenaTaskAI2(item.index)
                                    return
                                }else {
                                    accessbilityServiceImp?.sportsArenaRefreshAndSnapShoot()
                                }
                            }
                        }else{
                            accessbilityServiceImp?.sportsArenaRefreshAndSnapShoot()
                        }
                        Log.e("AccessbilityServiceImp", "result=" + result+",fightedPlayers ="+fightedPlayers)
                        return
                    }
                    val starsNum = ScreenShootDealwith.getStarsNum(bitmap)
                    var canStartTasks = ScreenShootDealwith.cutTaskContent(bitmap,starsNum.filter { it.stars == 3 })
                    canStartTasks += starsNum.filter { it.stars > 3 }
                    canStartTasks -= ScreenShootDealwith.cutTaskContent(bitmap,starsNum.filter { it.stars == 4})
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
                        if (accessbilityServiceImp?.isExecuteDepend == false)
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
            (0..9).map {
                list.add(ScreenShootDealwith.detectNumberContent(BitmapFactory.decodeStream(p0.assets.open("$it.jpg"))))
            }
        }

        if (p1?.action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            val connectivityManager = p0?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            accessbilityServiceImp!!.mHandler.removeCallbacksAndMessages(null)
        }
        accessbilityServiceImp!!.timespec = 2000L
        when(p1?.action){
            Command.ACTION_MODIFTY->{
                person_challege_count = p1.getIntExtra("person_challage",0);
                status = p1.getIntegerArrayListExtra("status")
                accessbilityServiceImp!!.vipStatus = p1.getBooleanExtra("vip_status",true)
                accessbilityServiceImp!!.taskList.clear()

                if (status?.get(0) == 200){
                    accessbilityServiceImp!!.winnerSportsArenaTask()
                }
            }
            Command.ACTION_MODEL_CHANGE->{
                //冠军的试炼启动
                status = arrayListOf(200,50)
                accessbilityServiceImp!!.vipStatus = true
                accessbilityServiceImp!!.taskList.clear()

                slidingValue = -380f
                fightedPlayers.clear()
                accessbilityServiceImp?.timespec = 2000L

                val displayMetrics = p0?.resources?.displayMetrics
                accessbilityServiceImp!!.width = displayMetrics?.widthPixels?.toFloat()!!
                accessbilityServiceImp!!.height = displayMetrics?.heightPixels?.toFloat()!!
                Log.e("AccessbilityServiceImp","width = "+accessbilityServiceImp!!.width+",height="+accessbilityServiceImp!!.height)

                //消息栏收起
                accessbilityServiceImp!!.collapseStatusBar(context = p0)
                accessbilityServiceImp!!.winnerSportsArenaTask()
                accessbilityServiceImp!!.taskList.map {
                    Log.e("AccessbilityServiceImp","timespec = "+it.timespec)
                    accessbilityServiceImp!!.mHandler.postDelayed(it.callback,it.timespec)
                }
                return
            }

                Command.ACTION_START->{
                slidingValue = -380f
                fightedPlayers.clear()
                accessbilityServiceImp?.timespec = 2000L

                val displayMetrics = p0?.resources?.displayMetrics
                accessbilityServiceImp!!.width = displayMetrics?.widthPixels?.toFloat()!!
                accessbilityServiceImp!!.height = displayMetrics?.heightPixels?.toFloat()!!
                Log.e("AccessbilityServiceImp","width = "+accessbilityServiceImp!!.width+",height="+accessbilityServiceImp!!.height)

                //消息栏收起
                accessbilityServiceImp!!.collapseStatusBar(context = p0)
                val nodeInfo = accessbilityServiceImp!!.rootInActiveWindow


                if(status?.get(0) == 1){
                    accessbilityServiceImp!!.openDailyTask()
                } else if (status?.get(0) == 0) {
                    accessbilityServiceImp!!.isExecuteDepend = true
                }else if (status?.get(0) == 200){
//                    accessbilityServiceImp!!.mHandler.post {
//                        startSnapShoot()
//                    }
//                    accessbilityServiceImp!!.winnerSportsArenaTask(status?.get(1)!!)
                    accessbilityServiceImp!!.taskList.map {
                        Log.e("AccessbilityServiceImp","timespec = "+it.timespec)
                        accessbilityServiceImp!!.mHandler.postDelayed(it.callback,it.timespec)
                    }
                    return
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
                if(status?.get(7) != 0){
                    accessbilityServiceImp!!.sportsArenaTask(person_challege_count)
                }
                if(status?.get(8) == 1){
                    accessbilityServiceImp!!.searchLevelTask()
                }

                if(status?.get(9) == 1){
                    accessbilityServiceImp!!.startTaskBar { startSnapShoot() }
                }
                accessbilityServiceImp!!.taskList.map {
                    Log.e("AccessbilityServiceImp","timespec = "+it.timespec)
                    accessbilityServiceImp!!.mHandler.postDelayed(it.callback,it.timespec)
                }
            }
            Command.ACTION_STOP->{
                accessbilityServiceImp!!.mHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun startSnapShoot() {
        accessbilityServiceImp!!.mHandler.postDelayed({
            accessbilityServiceImp!!.takeScreenshot(
                Display.DEFAULT_DISPLAY,
                accessbilityServiceImp!!.mainExecutor,mCallBack)
        },1000)
    }
}