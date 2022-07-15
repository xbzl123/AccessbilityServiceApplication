package com.raysharp.accessbilityserviceapplication.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.raysharp.accessbilityserviceapplication.Command
import com.raysharp.accessbilityserviceapplication.LocalBroadcastReceiver
import java.lang.reflect.Method


data class ScrollData(
    val isScroll:Boolean,
    val scrollX:Float,
    val scrollY:Float)


/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * AccessbilityServiceImp
 * @author longyanghe
 * @date 2022-06-23
 */
@RequiresApi(Build.VERSION_CODES.P)
class AccessbilityServiceImp: AccessibilityService() {

    var width = 0f
    var height = 0f
    var timespec = 2000L

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

//    private fun startSnapShoot(): Bitmap {
//        val view1 = Dialog(this, R.style.Theme_AppCompat).window?.decorView
//        val bitmap = view1?.let { Bitmap.createBitmap(it.width, view1.height, Bitmap.Config.ARGB_8888) }
//        val canvas = Canvas(bitmap!!)
//        view1?.draw(canvas)
//        return bitmap
//    }
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
                    ActionDeal(width/2,height/2,scrollData)
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
                    ActionDeal(width/2,height/2,scrollData)
                }
            }
        }
        timespec = delayTime
        mHandler.postDelayed(callback,-1,delayTime)
    }
    fun clickScreen(x:Float,y: Float){
        val callback = {
            ActionDeal(x,y,null)
        }
        mHandler.postDelayed(callback,-1,timespec)
        timespec+=1000
        Log.e("AccessbilityServiceImp","timespec = "+timespec)

    }

    fun clickScreen(x:Float,y: Float,delayTime: Long){
        val callback = {
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(x,y,null)
                }
            }
        }
        mHandler.postDelayed(callback,-1,delayTime)
    }

    fun openDailyTask(){
        //打开日常任务
        val callback = {
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width-10f,height/2,null)
                }
            }
        }
        mHandler.postDelayed(callback,0,timespec)
        timespec+=1000
    }


    fun commonInviteTask(){
        //打开普通邀请任务
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)
                }
            }
        },1,timespec)
        timespec+=1000

        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4+10,height/4*3+10,null)
                }
            }
        },2,timespec)
        timespec+=3000

        //点击确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3,height/4*3+50,null)
                }
            }
        },3,timespec)
        timespec+=1000

        //点击返回
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/10,height/10,null)
                }
            }
        },4,timespec)
        timespec+=1000

        //领取普通邀请
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },5,timespec)
        timespec+=1000
    }

    fun surviveTask(){
        //开始幸存奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)
                }
            }
        },6,timespec)
        timespec+=1000
        //开始免费幸存奖励1
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/4*3+50,null)
                }
            }
        },7,timespec)
        timespec+=1000

        //确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/4*3+50,null)
                }
            }
        },8,timespec)
        timespec+=1000

        //开始幸存奖励20钻
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2,height/4*3+50,null)
                }
            }
        },9,timespec)
        timespec+= 1000
        //确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/4*3+50,null)
                }
            }
        },10,timespec)
        timespec+=1000

        //关闭幸存奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/6*5+50,height/7,null)
                }
            }
        },11,timespec)
        timespec+=1000

        //领取幸存奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },12,timespec)
        timespec+=1000
    }



    fun friendTask(){
        //开始赠送爱心
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)
                }
            }
        },13,timespec)
        timespec+=1000
        //一键领取和发送
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/6*4.5f,height/4.5f,null)
                }
            }
        },14,timespec)
        timespec+=1000
        //关闭好友列表
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/6*5,height/6,null)
                }
            }
        },15,timespec)
        timespec+=1000
        //领取好友奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },16,timespec)
        timespec+=1000
    }



    fun lunckwheelTask(){
        //幸运转盘抽奖
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },17,timespec)
        timespec+=1500
        //点击普通幸运转盘抽奖
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/1.8f,height/2.5f,null)                            }
            }
        },18,timespec)
        timespec+=5500
        //再一次普通幸运转盘抽奖
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/5*3,height/4*3.2f,null)                            }
            }
        },19,timespec)
        timespec+=1000
        //点击确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/5*2,height/4*3.2f,null)                            }
            }
        },20,timespec)
        timespec+=2000
        //点击返回
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/10.5f,height/10.5f,null)
                }
            }
        },21,timespec)
        timespec+=1000

        //领取幸运转盘抽奖
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },22,timespec)
        timespec+=1000
    }


    fun challengeInstanceZonesTask(){
        //前往挑战副本
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },23,timespec)
        timespec+=1500
        //金币挑战
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3,height/4*3.5f,null)                            }
            }
        },24,timespec)
        timespec+=1000
        //金币挑战1
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
            }
        },25,timespec)
        timespec+=1000
        //金币挑战2
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2f,height/4*3.3f,null)                            }
            }
        },26,timespec)
        timespec+=1000
        //确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2f,height/4*3.3f,null)                            }
            }
        },27,timespec)
        timespec+=1000
        //返回挑战副本
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.5f,height/6.5f,null)                            }
            }
        },28,timespec)
        timespec+=1000
        //绿药挑战
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/4*3.5f,null)                            }
            }
        },29,timespec)
        timespec+=1000

        //绿药挑战1
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
            }
        },30,timespec)
        timespec+=1000
        //绿药挑战2
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2f,height/4*3.3f,null)                            }
            }
        },31,timespec)
        timespec+=1000
        //确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2f,height/4*3.3f,null)                            }
            }
        },32,timespec)
        timespec+=1000
        //返回挑战副本
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.5f,height/6.5f,null)                            }
            }
        },33,timespec)
        timespec+=1000
        //碎片挑战
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.2f,height/4*3.5f,null)                            }
            }
        },34,timespec)
        timespec+=1000

        //碎片挑战1
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
            }
        },35,timespec)
        timespec+=1000
        //碎片挑战2
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2f,height/4*3.3f,null)                            }
            }
        },36,timespec)
        timespec+=1000
        //确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2f,height/4*3.3f,null)                            }
            }
        },37,timespec)
        timespec+=1000
        //返回挑战副本
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.5f,height/9,null)                            }
            }
        },38,timespec)
        timespec+=1000

        //关闭挑战副本
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3*2.6f,height/9,null)                            }
            }
        },39,timespec)
        timespec+=1000

        //打开日常任务
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width-10f,height/2,null)
                }
            }
        },40,timespec)
        timespec+=1000

        //领取挑战副本奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },41,timespec)
        timespec+=1000
    }



    fun highlevelInviteTask(){
        //高级邀请
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width*0.7714f,height*0.798f,null)                            }
            }
        },42,timespec)
        timespec+=1000
        //点击邀请
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/2,height/4*3+10,null)
                }
            }
        },43,timespec)
        timespec+=5000
        //点击确定
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/3,height/4*3.2f,null)
                }
            }
        },44,timespec)
        timespec+=1000
        //点击返回
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/10.5f,height/10.5f,null)
                }
            }
        },45,timespec)
        timespec+=1000
        //领取啤酒邀请
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },46,timespec)
        timespec+=1000
    }

    fun sportsArenaTask(){
        //竞技场
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width*0.7714f,height*0.798f,null)                            }
            }
        },47,timespec)
        timespec+=1000
        var count = 0
        while (count < 3){
            //点击战斗1
            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/4*3.5f,height/1.5f,null)                            }
                }
            },48,timespec)
            timespec+=1000

//                            //点击刷新
//                            mHandler.postDelayed({
//                                if (height != 0f) {
//                                    if (width != 0f) {
//                                        ActionDeal(width/4*3.3f,height/3.9f,null)                            }
//                                }
//                            },timespec)
//                            timespec+=1000
//
//                            //点击刷新
//                            mHandler.postDelayed({
//                                if (height != 0f) {
//                                    if (width != 0f) {
//                                        ActionDeal(width/4*3.3f,height/3.9f,null)                            }
//                                }
//                            },timespec)
//                            timespec+=1000
            //选择最后一名点击战斗
            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/4*3.2f,height/5*4,null)                            }
                }
            },49,timespec)
            timespec+=1000

            //英雄出战点击战斗
            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/4*3.2f,height/2.2f,null)                            }
                }
            },50,timespec)
            timespec+=3000

            //翻牌中间
            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/1.8f,height/2f,null)
                        Log.e("翻盘1","widyh = "+width/1.8f+",height = "+height/2f)
                    }
                }
            },51,timespec)
            timespec+=3000
            Log.e("timespec","timespec111 = "+timespec)

            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/2,height/2f,null)                            }
                }

            },52,timespec)
            timespec+=1000
            //点击确定
            Log.e("timespec","timespec222 = "+timespec)

            mHandler.postDelayed({
                if (height != 0f) {
                    if (width != 0f) {
                        ActionDeal(width/1.8f,height/5*4,null)                            }
                }

            },53,timespec)
            timespec+=1000
            count+=1
        }

        //点击返回
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/10.5f,height/10.5f,null)
                }
            }
        },54,timespec)
        Log.e("timespec","timespec333 = "+timespec)

        timespec+=1000

        //领取战斗奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },55,timespec)
        timespec+=1000

    }

    fun searchLevelTask(){
        //点击搜索关卡
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/5*4,null)                            }
            }
        },56,timespec)
        timespec+=1000
        //点击宝箱
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/5*4.5f,height/5*4,null)                            }
            }
        },57,timespec)
        timespec+=1500
        //点击领取
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width*0.5f,height/5*4.5f,null)                            }
            }
        },58,timespec)
        timespec+=1000
        //点击返回
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/10.5f,height/10.5f,null)                            }
            }
        },59,timespec)
        timespec+=1000

        //领取搜索奖励
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)                            }
            }
        },60,timespec)
        timespec+=1000
    }

    fun startTaskBar(callback: ()->Unit){
        //打开任务栏
        mHandler.postDelayed({
            if (height != 0f) {
                if (width != 0f) {
                    ActionDeal(width/4*3,height/2,null)
                }
            }
        },61,timespec)
        timespec+=1000

        mHandler.postDelayed({
                    callback.invoke()
        },62,timespec)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("AccessbilityServiceImp","onAccessibilityEvent = "+event?.packageName
        +"，action = "+event?.action)
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



    private fun ActionDeal(x:Float, y:Float, scrollData: ScrollData?) {
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

