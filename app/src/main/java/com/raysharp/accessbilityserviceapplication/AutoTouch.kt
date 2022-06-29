package com.raysharp.accessbilityserviceapplication

import android.app.Activity
import android.util.Log
import java.io.IOException


/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * AutoTouch
 * @author longyanghe
 * @date 2022-06-23
 */
object AutoTouch {
    var width = 0
        set(value) {
            field = value
        }
    var height = 0
        set(value) {
            field = value
        }

    /**
     * 传入在屏幕中的比例位置，坐标左上角为基准
     *
     * @param act    传入Activity对象
     * @param ratioX 需要点击的x坐标在屏幕中的比例位置
     * @param ratioY 需要点击的y坐标在屏幕中的比例位置
     */
    fun autoClickRatio(ratioX: Double, ratioY: Double) {

        Thread { // 线程睡眠0.1s
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            // 生成点击坐标
            val x = (width * ratioX).toInt()
            val y = (height * ratioY).toInt()

            // 利用ProcessBuilder执行shell命令
            val order = arrayOf("input", "tap", "" + x, "" + y)
            try {
                ProcessBuilder(*order).start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 传入在屏幕中的坐标，坐标左上角为基准
     *
     * @param act 传入Activity对象
     * @param x   需要点击的x坐标
     * @param y   需要点击的x坐标
     */
    fun autoClickPos(x: Double, y: Double) {
        Thread { // 线程睡眠0.1s
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Log.e("AccessbilityServiceImp","autoClickPos = ")

            // 利用ProcessBuilder执行shell命令
            val order = arrayOf("input", "tap", "" + x, "" + y)
            try {
                ProcessBuilder(*order).start()
            } catch (e: IOException) {
                Log.e("AccessbilityServiceImp","autoClickPos =IOException ")
                e.printStackTrace()
            }
        }.start()
    }
}