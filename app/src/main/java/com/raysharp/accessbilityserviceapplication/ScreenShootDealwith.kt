package com.raysharp.accessbilityserviceapplication

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Core.bitwise_not
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import org.opencv.core.Mat
import org.opencv.core.CvType

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

data class NumberInfo(
    val minValue: Double,val index:Int
)
object ScreenShootDealwith {

    fun getStarsNum(bitmap: Bitmap): List<TaskInfo> {
        return cutStarsPictrue(bitmap,detectFristStarsRect(bitmap))
    }

    fun getSkipFight(bitmap: Bitmap): Boolean {
        return cutSkipFightRect(bitmap)
    }

    private fun cutSkipFightRect(bitmap: Bitmap): Boolean {
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val mRgb = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, mRgb)
            val rect = Rect(655, 236, 35, 35)

            val src = Mat(mRgb, rect)

            val b = Bitmap.createBitmap(
                src!!.cols(), src.rows(),
                Bitmap.Config.ARGB_8888
            )
        //归一化
        Imgproc.resize(src,src,Size(rect.width.toDouble(), rect.height.toDouble()))
        //灰色化
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY)
        //模糊化
        Imgproc.GaussianBlur(src, src, Size(1.0, 1.0), 0.0, 0.0)
        //二値化
        Imgproc.threshold(
            src, src, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )
        //比特反转
        val hierarchy = Mat.zeros(Size(40.0, 10.0), CvType.CV_8UC1)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()

        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
            Utils.matToBitmap(src, b)
        contours.map {
            if (it.rows() > 30){
                return true
            }
        }
            Log.e("AccessbilityServiceImp", " num =" + contours.size)
        return false
    }


    fun detectNumberContent(bitmap: Bitmap):List<Mat>{
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
//        Imgproc.GaussianBlur(src, src, Size(1.0, 1.0), 0.0, 0.0)
        //二値化
        Imgproc.threshold(
            src, src, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )

        //比特反转
        val hierarchy = Mat.zeros(Size(0.0, 0.0), CvType.CV_8UC1)
//
        Core.bitwise_not(src, src)
//        Imgproc.erode(src,src,hierarchy)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()

        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        Imgproc.drawContours(dst, contours, -1, color, 1)

        val list = ArrayList<Point>()

        var box: Rect?
        var boxs = HashSet<Rect>()
        for (i in contours.indices) {

            val ptmat: MatOfPoint = contours[i]
            // 轮廓的重心的绘制 (Red)
            color = Scalar(255.0, 0.0, 0.0)
            val ptmat2 = MatOfPoint2f(*ptmat.toArray())
            val bbox = Imgproc.minAreaRect(ptmat2)
            box = bbox.boundingRect()

            if (box.width < box.height) {

                Imgproc.circle(dst, bbox.center, 5, color, -1)
                list.add(bbox.center)

                // 周围轮廓四角形绘制 (Green)
                color = Scalar(0.0, 255.0, 0.0)
                Imgproc.rectangle(dst, box.tl(), box.br(), color, 2)

                boxs.add(box)
            }
        }

        val temp = ArrayList<Rect>()
        temp.addAll(boxs)
        //去包含的
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
        Utils.matToBitmap(dst,bitmapNew)
        temp.sortBy {
            it.x
        }

        val result1 = cutTemplateNumber(src,temp)

//        val result = Mat()
//        Imgproc.matchTemplate(result1[1],result1[0],result,Imgproc.TM_CCORR)
//        val bitmap1 = Bitmap.createBitmap(
//            result1[1]!!.cols(), result1[1].rows(),
//            Bitmap.Config.ARGB_8888
//        )
//        Utils.matToBitmap(result1[1],bitmap1)
//        Utils.matToBitmap(result1[1],bitmap1)
//        Log.e("AccessbilityServiceImp", " result =" + result)

        return result1
    }

    fun detectNumberRect(bitmap: Bitmap, list: List<Mat>): Int {
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val src = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, src)


//        val rect = Rect(bitmapNew.width/400*102,bitmapNew.height*97/200,50,25)
        val rect = Rect(bitmapNew.width/400*102,bitmapNew.height*261/399,50,25)

//        val rect = Rect(bitmapNew.width/400*102,bitmapNew.height*329/400,50,25)

        val mRgb = Mat(src, rect)

        val b = Bitmap.createBitmap(
            mRgb!!.cols(), mRgb.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(mRgb,b)

        val dst = Mat.zeros(Size(src.width().toDouble(), src.height().toDouble()), CvType.CV_8UC3)
        // 輪郭を描画 (Yellow)
        var color = Scalar(255.0, 255.0, 0.0)

        //灰色化
        Imgproc.cvtColor(mRgb, mRgb, Imgproc.COLOR_RGB2GRAY)
//        //Gaussian Filters
//        Imgproc.GaussianBlur(mRgb, mRgb, Size(1.0, 1.0), 0.0, 0.0)

        bitwise_not(mRgb,mRgb);
        //二値化
        Imgproc.threshold(
            mRgb, mRgb, 0.0, 250.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )
        Utils.matToBitmap(mRgb, b)
        //比特反转
        val hierarchy = Mat.zeros(Size(0.0, 0.0), CvType.CV_8UC1)

        val erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(10.0, 1.0))
        Imgproc.erode(src,src,erodeKernel)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()

        Imgproc.findContours(
            mRgb,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        Imgproc.drawContours(dst, contours, -1, color, 1)

        var box: Rect?
        var boxs = ArrayList<Rect>()
        for (i in contours.indices) {

            val ptmat: MatOfPoint = contours[i]
            // 轮廓的重心的绘制 (Red)
            color = Scalar(255.0, 0.0, 0.0)
            val ptmat2 = MatOfPoint2f(*ptmat.toArray())
            val bbox = Imgproc.minAreaRect(ptmat2)
            box = bbox.boundingRect()

            if (box.width < box.height  && box.height < 30 && box.width > 10
            /*&& box.height > 70 && box.height < 75*/) {

                Imgproc.circle(dst, bbox.center, 5, color, -1)

                // 周围轮廓四角形绘制 (Green)
                color = Scalar(0.0, 255.0, 0.0)
                Imgproc.rectangle(dst, box.tl(), box.br(), color, 2)

                boxs.add(box)
            }
        }
        boxs.sortBy {
            it.x
        }

        val result = ArrayList<NumberInfo>()
        val tmp = Mat(mRgb, boxs[0])
            list.mapIndexed { index,it->
                val result_cols: Int = abs(tmp.cols() - it.cols()) + 1
                val result_rows: Int = abs(tmp.rows() - it.rows()) + 1
                val res = Mat(result_rows, result_cols, CvType.CV_32FC1)
                //归一化
                var item = it
                Imgproc.resize(it,it,Size(tmp.width().toDouble(), tmp.height().toDouble()))
                Imgproc.matchTemplate(tmp,item,res,Imgproc.TM_SQDIFF)

                //获得最可能点，MinMaxLocResult是其数据格式，包括了最大、最小点的位置x、y
                val mlr = Core.minMaxLoc(res);
                result.add(NumberInfo(mlr.minVal,index))


                Log.e("AccessbilityServiceImp", "mlr result minVal="
                        + mlr.minVal+",maxVal="+mlr.maxVal+",minLoc="+mlr.minLoc+",maxLoc="+mlr.maxLoc)

            }
        result.sortBy {
            it.minValue
        }
//        }
        return result[0].index
    }
    fun detectSkipFightRect(bitmap: Bitmap):Boolean{
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
        val hierarchy = Mat.zeros(Size(40.0, 10.0), CvType.CV_8UC1)
//
//        Core.bitwise_not(src, src)
        Imgproc.erode(src,src,hierarchy)

        // 輪郭抽出
        val contours:List<MatOfPoint> = ArrayList()

        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
//        Imgproc.drawContours(dst, contours, -1, color, 1)

        //相似度对比，接近1为完全相同
//        Imgproc.compareHist(dst,src,Imgproc.HISTCMP_INTERSECT)
//        val result = Mat()
//        Imgproc.matchTemplate(dst,src,result,Imgproc.TM_CCORR)

        val list = ArrayList<Point>()

        var box: Rect?
        val boxs = ArrayList<Rect>()
        for (i in contours.indices) {

            val ptmat: MatOfPoint = contours[i]
            // 轮廓的重心的绘制 (Red)
            color = Scalar(255.0, 0.0, 0.0)
            val ptmat2 = MatOfPoint2f(*ptmat.toArray())
            val bbox = Imgproc.minAreaRect(ptmat2)
            box = bbox.boundingRect()

            if (box.width > box.height * 2 && box.width < box.height * 3
                && box.height > 70 && box.height < 75) {

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

        Utils.matToBitmap(dst,bitmapNew)
        return temp.isEmpty()
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

            if (box.width > box.height * 5 && box.width < box.height * 12/* && box.height > 50*/) {

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

        //排除正在进行和完成的任务
        val lastLists = ArrayList<Rect>()

        lastLists.addAll(temp)
        temp.map { it ->
            val rect = it
            temp.map {
                if (it != rect && abs(it.tl().x -rect.tl().x) < 10)
                    lastLists.remove(it)
            }
        }
//        Utils.matToBitmap(dst,bitmapNew)
//        Log.e("test","bitmap1 = "+bitmapNew)
        return lastLists
    }
    private fun cutTemplateNumber(mRgb: Mat, list: ArrayList<Rect>): List<Mat> {
        var starsNum = ArrayList<Mat>()
        list.map {
            val rect = Rect(it.x , it.y , it.width, it.height)

            val src = Mat(mRgb, rect)

            val b = Bitmap.createBitmap(
                src!!.cols(), src.rows(),
                Bitmap.Config.ARGB_8888
            )

            Utils.matToBitmap(src, b)
            starsNum.add(src)
        }
        return starsNum
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

            Log.e("AccessbilityServiceImp", " num =" + num)
        }
        return starsNum
    }

    fun cutTaskContent(bitmap: Bitmap, list: List<TaskInfo>): List<TaskInfo> {
        val bitmapNew: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        //opencvプリプロセッサ
        if (!OpenCVLoader.initDebug()) {
            Log.d("--------opencv--------", "OpenCVLoader error")
        }
        //Mat変換
        val mRgb = Mat(bitmapNew.height, bitmapNew.width, CvType.CV_8UC4)
        Utils.bitmapToMat(bitmapNew, mRgb)
        var starsNum = ArrayList<TaskInfo>()
        var rect:Rect
        list.map {
            //如果是3星任务截图区域
            if (it.stars < 4){
                 rect = Rect(it.rect.x + bitmapNew.width/23, it.rect.y - 3 + bitmapNew.height/32*9, it.rect.width/3 , it.rect.height/2 )
            }else {
                //如果是4-5星任务截图区域
                 rect = Rect(
                    it.rect.x + bitmapNew.width / 60,
                    it.rect.y + bitmapNew.height / 33 * 6,
                    it.rect.width / 5,
                     it.rect.width / 3
                )
            }
                val src = Mat(mRgb, rect)

                val b = Bitmap.createBitmap(
                    src!!.cols(), src.rows(),
                    Bitmap.Config.ARGB_8888
                )

                Utils.matToBitmap(src, b)

                val num = returnTaskStarsNum(b)
            /*
            如果是3星任务返回5是英雄碎片
            如果是4，5星任务返回5是竞技场挑战券
             */
            if (it.stars < 4) {
                if (num >= 4) {
                    starsNum.add(it)
                }
            }else{
                if (num in 5..9) {
                    starsNum.add(it)
                }
            }

            Log.e("AccessbilityServiceImp", " starsNum =" + starsNum)
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
        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_TC89_L1
        )
//        Utils.matToBitmap(src,bitmapNew)
        var count = 0
        for (i in contours.indices) {
            // 去除小轮廓 (ノイズ除去)
            if (contours.get(i).rows() < 23) {
                continue
            }
            count+=1
        }

        return count
    }

    private fun returnTaskStarsNum(bitmap:Bitmap): Int {
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
        Imgproc.findContours(
            src,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_TC89_L1
        )
//        Utils.matToBitmap(src,bitmapNew)
        Log.e("AccessbilityServiceImp", " returnTaskStarsNum =" + contours.size)

        return contours.size
    }
}