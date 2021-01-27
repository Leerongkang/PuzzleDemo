package com.puzzle.quality

import android.os.Build
import com.puzzle.template.Baggage
import com.puzzle.template.PuzzleLabel
import com.puzzle.template.PuzzleMetric
import com.puzzle.template.PuzzleQuality
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
/**
 * 拼图质量上报单例
 */
object PuzzleQualityManager {

    val puzzleQuality = PuzzleQuality(
        label = PuzzleLabel(),
        metric =  PuzzleMetric(),
        baggage = Device.systemInfo()
    )

    object Device {

        fun systemInfo() = Baggage(
            Build.VERSION.SDK_INT,
            (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt(),
            getCpuName(),
            Build.MANUFACTURER,
            Build.SUPPORTED_ABIS.size,
            getTotalMem()
        )

        private fun getTotalMem(): Int {
            try {
                val fr = FileReader("/proc/meminfo")
                val br = BufferedReader(fr)
                val text: String = br.readLine()
                val array = text.split("\\s+".toRegex()).toTypedArray()
                // 单位为KB
//        val readLine = FileReader("/proc/cpuinfo").buffered().readText()
//        Log.e("kkl", readLine)
                return (array[1].toLong() / 1024).toInt()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return -1
        }

        private fun getCpuName(): String {
            val str1 = "/proc/cpuinfo"
            var str2 = ""
            try {
                val fr = FileReader(str1)
                val localBufferedReader = BufferedReader(fr)
                while (localBufferedReader.readLine().also { str2 = it } != null) {
                    if (str2.contains("Hardware")) {
                        return str2.split(":".toRegex()).toTypedArray()[1]
                    }
                }
                localBufferedReader.close()
            } catch (e: IOException) {
            }
            return "other"
        }
    }

}