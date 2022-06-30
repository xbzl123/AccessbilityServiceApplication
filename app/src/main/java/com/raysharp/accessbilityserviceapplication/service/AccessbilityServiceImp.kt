package com.raysharp.accessbilityserviceapplication.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.raysharp.accessbilityserviceapplication.SocketClient
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
class AccessbilityServiceImp: AccessibilityService() {

    override fun onServiceConnected() {
        val displayMetrics = resources.displayMetrics

        Log.e("AccessbilityServiceImp","onServiceConnected")
//        val serviceInfo = AccessibilityServiceInfo()
//        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
//        serviceInfo.packageNames = arrayOf("com.tencent.mm")
//        serviceInfo.notificationTimeout = 100
//        setServiceInfo(serviceInfo)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("AccessbilityServiceImp","onStartCommand")

        val localBroadcastReceiver = LocalBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("start")
        intentFilter.addAction("stop")
        registerReceiver(localBroadcastReceiver,intentFilter)
        val result = 0
        return result
    }

    class LocalBroadcastReceiver: BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.e("AccessbilityServiceImp","onReceive = "+p1?.action)
            when(p1?.action){

                "start"->{
                    val displayMetrics = p0?.resources?.displayMetrics
                    val width = displayMetrics?.widthPixels?.toFloat()
                    val height = displayMetrics?.heightPixels?.toFloat()
                    Log.e("AccessbilityServiceImp","width ="+width+",height = "+height)
                    val accessbilityServiceImp = p0 as AccessbilityServiceImp
                    //消息栏收起
                    accessbilityServiceImp.collapseStatusBar(context = p0)
                    var timespec = 2000L

                    //打开日常任务
                    Handler().postDelayed({
                        if (height != null) {
                            if (width != null) {
                                accessbilityServiceImp.ActionDeal(width-10f,height/2,null)
                            }
                        }
                    },timespec)
                    timespec+=500

                   fun commonInviteWork(){
                       //打开普通邀请任务
                       Handler().postDelayed({
                           if (height != null) {
                               if (width != null) {
                                   accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)
                               }
                           }
                       },timespec)
                       timespec+=2000

                       Handler().postDelayed({
                           if (height != null) {
                               if (width != null) {
                                   accessbilityServiceImp.ActionDeal(width/4+10,height/4*3+10,null)
                               }
                           }
                       },timespec)
                       timespec+=500

                       //点击确定
                       Handler().postDelayed({
                           if (height != null) {
                               if (width != null) {
                                   accessbilityServiceImp.ActionDeal(width/3,height/4*3+50,null)
                               }
                           }
                       },timespec)
                       timespec+=1000

                       //点击返回
                       Handler().postDelayed({
                           if (height != null) {
                               if (width != null) {
                                   accessbilityServiceImp.ActionDeal(width/10,height/10,null)
                               }
                           }
                       },timespec)
                       timespec+=500

                       //领取普通邀请
                       Handler().postDelayed({
                           if (height != null) {
                               if (width != null) {
                                   accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                           }
                       },timespec)
                       timespec+=500
                   }
                    fun surviveWork(){
                        //开始幸存奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)
                                }
                            }
                        },timespec)
                        timespec+=500
                        //开始免费幸存奖励1
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/4*3+50,null)
                                }
                            }
                        },timespec)
                        timespec+=1000

                        //确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/4*3+50,null)
                                }
                            }
                        },timespec)
                        timespec+=1000

                        //开始幸存奖励20钻
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2,height/4*3+50,null)
                                }
                            }
                        },timespec)
                        timespec+= 1000
                        //确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/4*3+50,null)
                                }
                            }
                        },timespec)
                        timespec+=500

                        //关闭幸存奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/6*5+50,height/7,null)
                                }
                            }
                        },timespec)
                        timespec+=500

                        //领取幸存奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=500
                    }


                    fun friendWork(){
                        //开始赠送爱心
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)
                                }
                            }
                        },timespec)
                        timespec+=500
                        //一键领取和发送
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/6*4.5f,height/4.5f,null)
                                }
                            }
                        },timespec)
                        timespec+=1000
                        //关闭好友列表
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/6*5,height/6,null)
                                }
                            }
                        },timespec)
                        timespec+=500
                        //领取好友奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=500
                    }


                    fun lunckwheelWork(){
                        //幸运转盘抽奖
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //点击普通幸运转盘抽奖
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/3,null)                            }
                            }
                        },timespec)
                        timespec+=5500
                        //再一次普通幸运转盘抽奖
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/5*3,height/4*3.2f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //点击确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/5*2,height/4*3.2f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //点击返回
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/10.5f,height/10.5f,null)
                                }
                            }
                        },timespec)

                        //领取幸运转盘抽奖
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=500
                    }

                    fun challengeInstanceZones(){
                        //前往挑战副本
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //金币挑战
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3,height/4*3.5f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //金币挑战1
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //金币挑战2
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //返回挑战副本
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.5f,height/6.5f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //绿药挑战
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/4*3.5f,null)                            }
                            }
                        },timespec)
                        timespec+=500

                        //绿药挑战1
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //绿药挑战2
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //返回挑战副本
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.5f,height/6.5f,null)                            }
                            }
                        },timespec)
                        timespec+=500
                        //碎片挑战
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.2f,height/4*3.5f,null)                            }
                            }
                        },timespec)
                        timespec+=1000

                        //碎片挑战1
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //碎片挑战2
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2f,height/4*3.3f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //返回挑战副本
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.5f,height/6.5f,null)                            }
                            }
                        },timespec)
                        timespec+=1000

                        //关闭挑战副本
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3*2.6f,height/6.5f,null)                            }
                            }
                        },timespec)
                        timespec+=1000

                        //领取挑战副本奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                    }


                    fun highlevelInvite(){
                        //高级邀请
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/5*4,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/2,height/4*3+10,null)
                                }
                            }
                        },timespec)
                        timespec+=5000
                        //点击确定
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/3,height/4*3.2f,null)
                                }
                            }
                        },timespec)
                        timespec+=500
                        //点击返回
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/10.5f,height/10.5f,null)
                                }
                            }
                        },timespec)
                        timespec+=500
                        //领取啤酒邀请
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=500
                    }

                    fun sportsArena(){
                        //竞技场
//                        Handler().postDelayed({
//                            if (height != null) {
//                                if (width != null) {
//                                    accessbilityServiceImp.ActionDeal(width/4*3,height/5*4,null)                            }
//                            }
//                        },timespec)
//                        timespec+=1000
                        var count = 0
                        while (count < 3){
                            //点击战斗1
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/4*3.5f,height/1.5f,null)                            }
                                }
                            },timespec)
                            timespec+=1000

                            //点击刷新
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/4*3.3f,height/3.9f,null)                            }
                                }
                            },timespec)
                            timespec+=1000

                            //点击刷新
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/4*3.3f,height/3.9f,null)                            }
                                }
                            },timespec)
                            timespec+=1000
                            //选择最后一名点击战斗
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/4*3.2f,height/5*4,null)                            }
                                }
                            },timespec)
                            timespec+=1000

                            //英雄出战点击战斗
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/4*3.2f,height/2.2f,null)                            }
                                }
                            },timespec)
                            timespec+=3000

                            //翻牌中间
                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/1.8f,height/2f,null)
                                        Log.e("翻盘1","widyh = "+width/1.8f+",height = "+height/2f)
                                    }
                                }
                            },timespec)
                            timespec+=1000
                            Log.e("timespec","timespec111 = "+timespec)

                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/2,height/2f,null)                            }
                                }

                            },timespec)
                            timespec+=1000
                            //点击确定
                            Log.e("timespec","timespec222 = "+timespec)

                            Handler().postDelayed({
                                if (height != null) {
                                    if (width != null) {
                                        accessbilityServiceImp.ActionDeal(width/1.8f,height/5*4,null)                            }
                                }

                            },timespec)
                            timespec+=1000
                            count+=1
                        }

//                        //点击返回
//                        Handler().postDelayed({
//                            if (height != null) {
//                                if (width != null) {
//                                    accessbilityServiceImp.ActionDeal(width/10.5f,height/10.5f,null)
//                                }
//                            }
//                        },timespec)
//                        timespec+=1000

//                        //领取战斗奖励
//                        Handler().postDelayed({
//                            if (height != null) {
//                                if (width != null) {
//                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
//                            }
//                        },timespec)
//                        timespec+=1000

                    }

                    fun searchLevel(){
                        //点击搜索关卡
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/5*4,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //点击宝箱
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/5*4.5f,height/5*4,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //点击领取
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width*0.5f,height/5*4.5f,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                        //点击返回
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/10.5f,height/10.5f,null)                            }
                            }
                        },timespec)
                        timespec+=1000

                        //领取搜索奖励
                        Handler().postDelayed({
                            if (height != null) {
                                if (width != null) {
                                    accessbilityServiceImp.ActionDeal(width/4*3,height/2,null)                            }
                            }
                        },timespec)
                        timespec+=1000
                    }
//                    commonInviteWork()
//                    surviveWork()
//                    friendWork()
//                    lunckwheelWork()
//                    challengeInstanceZones()
//                    highlevelInvite()
                    sportsArena()
//                    searchLevel()
                }
            }
        }
    }
    var isClick = false
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("AccessbilityServiceImp","onAccessibilityEvent = "+event?.eventType)

        if (event?.packageName.contentEquals("com.tencent.mm") && !isClick){
//            AutoTouch.autoClickPos(550.0,1500.0)
            val order = arrayOf("input", "tap", "" + 500.0, "" + 1500.0)
//            runShell(Arrays.toString(order))
//            ActionDeal()
            isClick = true
            showToast()
        }
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


    @RequiresApi(Build.VERSION_CODES.N)
    private fun ActionDeal(x:Float, y:Float, scrollData: ScrollData?) {
        var path = android.graphics.Path()
        path.moveTo(x,y)
        if (scrollData?.isScroll == true){
            path.lineTo(x+ scrollData?.scrollX!!,y+scrollData?.scrollY)
        }
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 5))
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

    private fun runShell(cmd: String) {
        if (TextUtils.isEmpty(cmd)) return
        Thread {
            SocketClient(cmd, object : SocketClient.onServiceSend {
                override fun getSend(result: String?) {
                    Log.e("AccessbilityServiceImp"," = "+result)

                }
            })
        }.start()
    }

    override fun onInterrupt() {
    }

    fun showToast(){
        val handlerThree= Handler(Looper.getMainLooper());
        handlerThree.post({
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

