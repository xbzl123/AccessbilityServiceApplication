package com.raysharp.accessbilityserviceapplication.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.raysharp.accessbilityserviceapplication.Command
import com.raysharp.accessbilityserviceapplication.LocalBroadcastReceiver
import java.lang.reflect.Method
import kotlin.collections.ArrayList


data class ScrollData(
    val isScroll:Boolean,
    val scrollX:Float,
    val scrollY:Float)

data class TaskInfo(val callback: () -> Unit, val timespec: Long, val id:Int)

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * AccessbilityServiceImp
 * @author longyanghe
 * @date 2022-06-23
 */
@RequiresApi(Build.VERSION_CODES.P)
class AccessbilityServiceImp: AccessibilityService() {
    var isExecuteDepend = false
    var width = 0f
    var height = 0f
    var timespec = 2000L
    val changeData = MutableLiveData(0)

    override fun onServiceConnected() {
        Log.e("AccessbilityServiceImp","AccessbilityServiceImp")
//        val serviceInfo = AccessibilityServiceInfo()
//        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
//        serviceInfo.packageNames = arrayOf("com.tencent.mm")
//        serviceInfo.notificationTimeout = 100
//        setServiceInfo(serviceInfo)
    }
    private val localBroadcastReceiver = LocalBroadcastReceiver()

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Command.ACTION_MODIFTY)
        intentFilter.addAction(Command.ACTION_START)
        intentFilter.addAction(Command.ACTION_STOP)

        intentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        intentFilter.addAction("android.net.ethernet.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(localBroadcastReceiver,intentFilter)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AccessbilityServiceImp","onStartCommand")
        val result = 0
        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(localBroadcastReceiver)
    }
    val mHandler:TaskHandler = TaskHandler()

    class TaskHandler:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            val callback = msg.obj as ()->Unit
            callback.invoke()
        }
    }

    fun slidingScreen(scrollData: ScrollData){
        val callback = {
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/2,scrollData,62)
                }
            }
        }
        mHandler.postDelayed(callback,0,timespec)
        timespec+=1000
    }


    fun slidingScreen(scrollData: ScrollData, delayTime: Long){
        val callback = {
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/2,scrollData,62)
                }
            }
        }
        timespec = delayTime
        mHandler.postDelayed(callback,-1,delayTime)
    }
    fun clickScreen(x:Float,y: Float){
        val callback = {
            ActionDeal(x,y,null,62)
        }
        mHandler.postDelayed(callback,-1,timespec)
        timespec+=1000
        Log.e("AccessbilityServiceImp","timespec = "+timespec)

    }

    fun clickScreen(x:Float,y: Float,delayTime: Long){
        val callback = {
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(x,y,null,62)
                }
            }
        }
        mHandler.postDelayed(callback,62,delayTime)
    }

    var executedStep = 0
    var taskList = ArrayList<TaskInfo>()
    fun openDailyTask(){
        //打开日常任务
        val callback = {
            ActionDeal(width-10f,height/2,null,0)
        }
        taskList.add(TaskInfo(callback,timespec,0))
        timespec+=1000
    }


    fun commonInviteTask(){
        //打开普通邀请任务
        taskList.add(TaskInfo({ActionDeal(width / 4 * 3, height / 2, null, 1)},timespec,1))
        timespec+=1000

        taskList.add(TaskInfo({ActionDeal(width/4+10,height/4*3+10,null,2)},timespec,2))
        timespec+=3000
        //点击确定

        taskList.add(TaskInfo({ActionDeal(width/3,height/4*3+50,null,3)},timespec,3))
        timespec+=1000

        //点击返回
        taskList.add(TaskInfo({ActionDeal(width/10,height/10,null,4)},timespec,4))
        timespec+=1000

        //领取普通邀请
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,5)},timespec,5))
        timespec+=1000

    }

    fun surviveTask(){
        //开始幸存奖励
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,6)},timespec,6))
        timespec+=1000

        //开始免费幸存奖励1
        taskList.add(TaskInfo({ActionDeal(width/2,height/4*3+50,null,7)},timespec,7))
        timespec+=1000

        //确定
        taskList.add(TaskInfo({ActionDeal(width/2,height/4*3+50,null,8)},timespec,8))
        timespec+=1000

        //开始幸存奖励20钻
        taskList.add(TaskInfo({ActionDeal(width/3*2,height/4*3+50,null,9)},timespec,9))
        timespec+=1000

        //确定
        taskList.add(TaskInfo({ActionDeal(width/2,height/4*3+50,null,10)},timespec,10))
        timespec+=1000

        //关闭幸存奖励
        taskList.add(TaskInfo({ActionDeal(width/6*5.1f,height/7,null,11)},timespec,11))
        timespec+=1000

        //领取幸存奖励
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,12)},timespec,12))
        timespec+=1000

    }



    fun friendTask(){
        //开始赠送爱心
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,13)},timespec,13))
        timespec+=1000

        //一键领取和发送
        taskList.add(TaskInfo({ActionDeal(width/6*4.5f,height/4.5f,null,14)},timespec,14))
        timespec+=1000

        //关闭好友列表
        taskList.add(TaskInfo({ActionDeal(width/6*5,height/6,null,15)},timespec,15))
        timespec+=1000

        //领取好友奖励
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,16)},timespec,16))
        timespec+=1000

    }



    fun lunckwheelTask(){
        //幸运转盘抽奖
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,17)},timespec,17))
        timespec+=1000

        //点击普通幸运转盘抽奖
        taskList.add(TaskInfo({ActionDeal(width/1.8f,height/2.5f,null,18)},timespec,18))
        timespec+=6000

        //再一次普通幸运转盘抽奖
        taskList.add(TaskInfo({ActionDeal(width/5*3,height/4*3.2f,null,19)},timespec,19))
        timespec+=1000

        //点击确定
        taskList.add(TaskInfo({ActionDeal(width/5*2,height/4*3.2f,null,20)},timespec,20))
        timespec+=2000

        //点击返回
        taskList.add(TaskInfo({ActionDeal(width/10.5f,height/10.5f,null,21)},timespec,21))
        timespec+=1000

        //领取幸运转盘抽奖
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,22)},timespec,22))
        timespec+=1000

    }


    fun challengeInstanceZonesTask(){
        //前往挑战副本
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,23)},timespec,23))
        timespec+=1500

        //金币挑战
        taskList.add(TaskInfo({ActionDeal(width/3,height/4*3.5f,null,24)},timespec,24))
        timespec+=1000

        //金币挑战1
        taskList.add(TaskInfo({ActionDeal(width/3*2.2f,height/4*3.3f,null,25)},timespec,25))
        timespec+=1000

        //金币挑战2
        taskList.add(TaskInfo({ActionDeal(width/3*2f,height/4*3.3f,null,26)},timespec,26))
        timespec+=1000

        //确定
        taskList.add(TaskInfo({ActionDeal(width/2f,height/4*3.3f,null,27)},timespec,27))
        timespec+=1000

        //返回挑战副本
        taskList.add(TaskInfo({ActionDeal(width/3*2.5f,height/6.5f,null,28)},timespec,28))
        timespec+=1000

        //绿药挑战
        taskList.add(TaskInfo({ActionDeal(width/2,height/4*3.5f,null,29)},timespec,29))
        timespec+=1000

        //绿药挑战1
        taskList.add(TaskInfo({ActionDeal(width/3*2.2f,height/4*3.3f,null,30)},timespec,30))
        timespec+=1000

        //绿药挑战2
        taskList.add(TaskInfo({ActionDeal(width/3*2f,height/4*3.3f,null,31)},timespec,31))
        timespec+=1000

        //确定
        taskList.add(TaskInfo({ActionDeal(width/2f,height/4*3.3f,null,32)},timespec,32))
        timespec+=1000

        //返回挑战副本
        taskList.add(TaskInfo({ActionDeal(width/3*2.5f,height/6.5f,null,33)},timespec,33))
        timespec+=1000

        //碎片挑战
        taskList.add(TaskInfo({ActionDeal(width/3*2.2f,height/4*3.5f,null,34)},timespec,34))
        timespec+=1000

        //碎片挑战1
        taskList.add(TaskInfo({ActionDeal(width/3*2.2f,height/4*3.3f,null,35)},timespec,35))
        timespec+=1000

        //碎片挑战2
        taskList.add(TaskInfo({ActionDeal(width/3*2.2f,height/4*3.3f,null,36)},timespec,36))
        timespec+=1000

        //确定
        taskList.add(TaskInfo({ActionDeal(width/2f,height/4*3.3f,null,37)},timespec,37))
        timespec+=1000

        //返回挑战副本
        taskList.add(TaskInfo({ActionDeal(width/3*2.5f,height/9,null,38)},timespec,38))
        timespec+=1000

        //关闭挑战副本
        taskList.add(TaskInfo({ActionDeal(width/3*2.6f,height/9,null,39)},timespec,39))
        timespec+=1000

        //打开日常任务
        taskList.add(TaskInfo({ActionDeal(width-10f,height/2,null,40)},timespec,40))
        timespec+=1000

        //领取挑战副本奖励
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,41)},timespec,41))
        timespec+=1000

    }

    fun highlevelInviteTask(){
        //高级邀请
        taskList.add(TaskInfo({ActionDeal(width*0.7714f,height*0.798f,null,42)},timespec,42))
        timespec+=1000

        //点击邀请
        taskList.add(TaskInfo({ActionDeal(width/2,height/4*3+20,null,43)},timespec,43))
        timespec+=5000

        //点击确定
        taskList.add(TaskInfo({ActionDeal(width/3,height/4*3.2f,null,44)},timespec,44))
        timespec+=1000

        //点击返回
        taskList.add(TaskInfo({ActionDeal(width/10.5f,height/10.5f,null,45)},timespec,45))
        timespec+=1000

        //领取啤酒邀请
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,46)},timespec,46))
        timespec+=1000

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun sportsArenaTaskAI(){
        //竞技场
        if (!isExecuteDepend){

            taskList.add(TaskInfo({ActionDeal(width*0.7714f,height*0.798f,null,47)},timespec,47))
            timespec+=1000
        }

        //点击战斗1
        taskList.add(TaskInfo({ActionDeal(width/4*3.5f,height/1.5f,null,48)},timespec,48))
        timespec+=1000

        taskList.add(TaskInfo({localBroadcastReceiver.startSnapShoot()},timespec,48))

        if (!isExecuteDepend){
            //点击返回
            taskList.add(TaskInfo({ActionDeal(width/10.5f,height/10.5f,null,54)},timespec,54))
            timespec+=1000

            //领取战斗奖励
            taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,55)},timespec,55))
            timespec+=1000
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun sportsArenaRefreshAndSnapShoot(){
        taskList.map {
            mHandler.removeCallbacksAndMessages(it.id)
        }
        timespec = 1000L
        taskList.clear()
        //点击刷新
        taskList.add(TaskInfo({ActionDeal(width/4*3.3f,height/3.9f,null,480)},timespec,48))

        taskList.add(TaskInfo({localBroadcastReceiver.startSnapShoot()},timespec,48))
        timespec+=1000
        taskList.map {
            Log.e("AccessbilityServiceImp","timespec = "+it.timespec)
            mHandler.postDelayed(it.callback,it.timespec)
        }
    }

    fun sportsArenaTaskAI2(pos:Int,callback: () -> Unit){
        taskList.map {
            mHandler.removeCallbacksAndMessages(it.id)
        }
        timespec = 1000L
        taskList.clear()

        //选择一名点击战斗
        taskList.add(TaskInfo({ActionDeal(width/4*3.2f,height/5*(pos+1),null,49)},timespec,49))
        timespec+=500

        //英雄出战点击战斗
        taskList.add(TaskInfo({ActionDeal(width/4*3.2f,height/2.2f,null,50)},timespec,50))
        timespec+=2000

        //翻牌中间
        taskList.add(TaskInfo({ActionDeal(width/1.8f,height/2f,null,51)},timespec,51))
        timespec+=3000

        taskList.add(TaskInfo({ActionDeal(width/2,height/2f,null,52)},timespec,52))
        timespec+=1000

        //点击确定
        Log.e("timespec","timespec222 = "+timespec)
        taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
        timespec+=1000

        callback.invoke()

        taskList.map {
            Log.e("AccessbilityServiceImp","timespec = "+it.timespec)
            mHandler.postDelayed(it.callback,it.timespec)
        }

    }

    fun sportsArenaTask(){
        //竞技场
        if (!isExecuteDepend){

            taskList.add(TaskInfo({ActionDeal(width*0.7714f,height*0.798f,null,47)},timespec,47))
            timespec+=1000
        }

        var count = 0
        while (count < 3){
            //点击战斗1
            taskList.add(TaskInfo({ActionDeal(width/4*3.5f,height/1.5f,null,48)},timespec,48))
            timespec+=1000


            //选择最后一名点击战斗
            taskList.add(TaskInfo({ActionDeal(width/4*3.2f,height/5*4,null,49)},timespec,49))
            timespec+=1000

            //英雄出战点击战斗
            taskList.add(TaskInfo({ActionDeal(width/4*3.2f,height/2.2f,null,50)},timespec,50))
            timespec+=3000

            //翻牌中间
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/2f,null,51)},timespec,51))
            timespec+=3000

            taskList.add(TaskInfo({ActionDeal(width/2,height/2f,null,52)},timespec,52))
            timespec+=1000

            //点击确定
            Log.e("timespec","timespec222 = "+timespec)
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
            timespec+=1000

            count+=1
        }

        //点击返回
        taskList.add(TaskInfo({ActionDeal(width/10.5f,height/10.5f,null,54)},timespec,54))
        timespec+=1000

        if (!isExecuteDepend){
            //领取战斗奖励
            taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,55)},timespec,55))
            timespec+=1000
        }

    }

    fun searchLevelTask(){
        //点击搜索关卡
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/5*4,null,56)},timespec,56))
        timespec+=1000

        //点击宝箱
        taskList.add(TaskInfo({ActionDeal(width/5*4.5f,height/5*4,null,57)},timespec,57))
        timespec+=1000

        //点击领取
        taskList.add(TaskInfo({ActionDeal(width*0.5f,height/5*4.5f,null,58)},timespec,58))
        timespec+=1000

        //点击返回
        taskList.add(TaskInfo({ActionDeal(width/10.5f,height/10.5f,null,59)},timespec,59))
        timespec+=1000

        //领取搜索奖励
        taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,60)},timespec,60))
        timespec+=1000

    }

    fun startTaskBar(callback: ()->Unit){
        //打开任务栏
        if (!isExecuteDepend){
            taskList.add(TaskInfo({ActionDeal(width/4*3,height/2,null,61)},timespec,61))
            timespec+=1000
        }
        taskList.add(TaskInfo(callback,timespec,62))

    }


    fun winnerSportsArenaTask(num: Int) {
        taskList.clear()
        //冠军的试炼
        var count = 0
        while (count < num){
            //点击战斗1
            taskList.add(TaskInfo({ActionDeal(width/4*3.5f,height/1.5f,null,48)},timespec,48))
            timespec+=1000

            //点击刷新
            taskList.add(TaskInfo({ActionDeal(width/4*3.3f,height/3.9f,null,480)},timespec,48))
            timespec+=1000
            //点击刷新
            taskList.add(TaskInfo({ActionDeal(width/4*3.3f,height/3.9f,null,481)},timespec,48))
            timespec+=1000
            //点击刷新
            taskList.add(TaskInfo({ActionDeal(width/4*3.3f,height/3.9f,null,482)},timespec,48))
            timespec+=1000
//            (0..3).map {
//                //点击刷新
//                taskList.add(TaskInfo({ActionDeal(width/4*3.5f,height/3.9f,null,48)},timespec,48))
//                timespec+=1000
//            }

            //选择最后一名点击战斗
            taskList.add(TaskInfo({ActionDeal(width/4*3.2f,height/5*4,null,49)},timespec,49))
            timespec+=1000

            //英雄出战点击战斗
            taskList.add(TaskInfo({ActionDeal(width/4*3f,height/3.5f,null,50)},timespec,50))
            timespec+=1000


            //点击确定
            Log.e("timespec","timespec222 = "+timespec)
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
            timespec+=1000

            //点击确定
            Log.e("timespec","timespec222 = "+timespec)
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
            timespec+=1000

            //点击确定
            Log.e("timespec","timespec222 = "+timespec)
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
            timespec+=1000


            //翻牌中间
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/2f,null,51)},timespec,51))
            timespec+=3000

            taskList.add(TaskInfo({ActionDeal(width/2,height/2f,null,52)},timespec,52))
            timespec+=1000

            //点击确定
            Log.e("timespec","timespec222 = "+timespec)
            taskList.add(TaskInfo({ActionDeal(width/1.8f,height/5*4,null,53)},timespec,53))
            timespec+=1000

            count+=1
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootInActiveWindow = this.rootInActiveWindow
        Log.e("AccessbilityServiceImp","onAccessibilityEvent = "+event?.packageName
        +"，action = "+event?.action+"，eventType = "+event?.eventType+",rootInActiveWindow = "+rootInActiveWindow)

//        if (event?.packageName.contentEquals("com.tencent.mm") && !isClick){
//            isClick = true
//            showToast()
//        }
    }

    fun collapseStatusBar(context: Context) {
        val service = context.getSystemService("statusbar") ?: return
        try {
            val clazz = Class.forName("android.app.StatusBarManager")
            val sdkVersion = Build.VERSION.SDK_INT
            var collapse: Method? = null
            collapse = if (sdkVersion <= 16) {
                clazz.getMethod("collapse")
            } else {
                clazz.getMethod("collapsePanels")
            }
            collapse.setAccessible(true)
            collapse.invoke(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun ActionDeal(x:Float, y:Float, scrollData: ScrollData?,taskid:Int) {
        if (x <= 0 || y <= 0){
            return
        }
        var path = android.graphics.Path()
        path.moveTo(x,y)
        var duration = 5L
        if (scrollData?.isScroll == true){
            path.lineTo(x + scrollData?.scrollX!!,y + scrollData?.scrollY)
            duration = 500L
        }
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        dispatchGesture(gestureDescription,object : AccessibilityService.GestureResultCallback(){
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.e("AccessbilityServiceImp","gestureDescription = onCancelled")
            }

            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.e("AccessbilityServiceImp","gestureDescription = onCompleted")
            }
        },null)

//        taskList.remove(taskList.get(taskid))
    }

    //滑动屏幕返回
    fun ActionBack() {
        val action:()->Unit = {
            var path = android.graphics.Path()
            path.moveTo(10f,110f)
            path.lineTo(110f ,110f)
            val gestureDescription = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 500L))
                .build()
            dispatchGesture(gestureDescription,object : AccessibilityService.GestureResultCallback(){
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.e("AccessbilityServiceImp","gestureDescription = onCancelled")
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.e("AccessbilityServiceImp","gestureDescription = onCompleted")
                }

            },null)
        }
        mHandler.postDelayed(action,1000)
    }

    override fun onInterrupt() {
        Log.e("AccessbilityServiceImp","gestureDescription = onInterrupt")
    }

    fun showToast(){
        mHandler.post({
            Toast.makeText(getApplicationContext() ,"显示Toast在屏幕上！",Toast.LENGTH_LONG).show();
        });
    }

    //借箭（点击）
    private fun performClick(resourceId: String) {
        Log.i("mService", "点击执行")
        val nodeInfo = this.rootInActiveWindow
        var targetNode: AccessibilityNodeInfo? = null
        targetNode = findNodeInfosById(nodeInfo, "com.youmi.android.addemo:id/$resourceId")
        if (targetNode!!.isClickable) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }


    //调用兵力（通过id查找）
    fun findNodeInfosById(nodeInfo: AccessibilityNodeInfo, resId: String?): AccessibilityNodeInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(
                resId!!
            )
            if (list != null && !list.isEmpty()) {
                return list[0]
            }
        }
        return null
    }

    //调用船只（通过文本查找）
    fun findNodeInfosByText(
        nodeInfo: AccessibilityNodeInfo,
        text: String?
    ): AccessibilityNodeInfo? {
        val list = nodeInfo.findAccessibilityNodeInfosByText(text)
        return if (list == null || list.isEmpty()) {
            null
        } else list[0]
    }
}

