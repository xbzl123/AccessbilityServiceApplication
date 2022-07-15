package com.raysharp.accessbilityserviceapplication

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * com.raysharp.accessbilityserviceapplication.MyApplication
 * @author longyanghe
 * @date 2022-07-12
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}