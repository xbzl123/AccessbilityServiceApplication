package com.raysharp.accessbilityserviceapplication

import java.util.*
import kotlin.concurrent.schedule

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * TaskTimer
 * @author longyanghe
 * @date 2022-08-04
 */
object TaskTimer {
    val timer = Timer()

    fun setTaskDelayExecute(callback:()->Unit,time:Long){
        timer.schedule(time){
            callback.invoke()
        }
    }

    fun cancelAllTask(){
        timer.cancel()
    }
}