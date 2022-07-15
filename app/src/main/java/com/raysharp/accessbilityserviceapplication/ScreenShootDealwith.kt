package com.raysharp.accessbilityserviceapplication

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * ScreenShootDealwith
 * @author longyanghe
 * @date 2022-07-12
 */

data class TaskInfo(
    val rect: Rect,val stars:Int
)
object ScreenShootDealwith {

    fun getStarsNum(bitmap: Bitmap): List<TaskInfo> {
        return cutStarsPictrue(bitmap,detectFristStarsRect(bitmap))
    }

    private fun detectFristStarsRect(bitmap: Bitmap):ArrayList<Rect>{
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
        val runningOrovers = ArrayList<Rect>()

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

            if (box.width > box.height * 5 && box.width < box.height * 12 /*&& box.height > 50*/) {

                Imgproc.circle(dst, bbox.center, 5, color, -1)
                list.add(bbox.center)

                // 周围轮廓四角形绘制 (Green)
                color = Scalar(0.0, 255.0, 0.0)
                Imgproc.rectangle(dst, box.tl(), box.br(), color, 2)

                boxs.add(box)
                if(box.height < 50){
                    runningOrovers.add(box)
                }
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

        //排除正在进行和完成的任务
        val lastLists = ArrayList<Rect>()
        lastLists.addAll(temp)
        temp.map {
            val rect = it
            temp.map {
                if (!it.equals(rect) && Math.abs(it.tl().x -rect.tl().x) < 10)
                    lastLists.remove(it)
            }
        }

        Log.e("test","bitmap1 boxs= "+lastLists.size)
        return lastLists
    }

    private fun cutStarsPictrue(bitmap: Bitmap, list: ArrayList<Rect>): List<TaskInfo> {
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val mRgb = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, mRgb)
        var starsNum = ArrayList<TaskInfo>()
        list.map {
            val rect = Rect(it.x + 15, it.y + 8, it.width - 35, it.height - 18)

            val src = Mat(mRgb, rect)

            val b = Bitmap.createBitmap(
                src!!.cols(), src.rows(),
                Bitmap.Config.ARGB_8888
            )

            Utils.matToBitmap(src, b)

            val num = returnStarsNum(b)
            starsNum.add(TaskInfo(rect,num))

            Log.e("AccessbilityServiceImp", "bitmap1 = num " + num)
        }
        return starsNum
    }

    private fun returnStarsNum(bitmap:Bitmap): Int {
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
        Utils.matToBitmap(src,bitmapNew)
        var count = 0
//        if(contours.size > 7){
//            return count
//        }
        for (i in contours.indices) {
            // 去除小轮廓 (ノイズ除去)
            if (contours.get(i).rows() < 23) {
                continue
            }
            count+=1
        }

        return count
    }
}