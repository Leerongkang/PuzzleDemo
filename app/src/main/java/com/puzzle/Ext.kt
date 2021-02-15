package com.puzzle

import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File

/**
 * Int的扩展方法，转换dp到px
 */
fun Int.dp2px() = (this * app.resources.displayMetrics.density + 0.5f).toInt()

/**
 * 用于在下载路径中提取文件名称，如："https://meitu.com/xiuxiu.apk", 将返回 "xiuxiu.apk"
 */
fun String.parsePathFileName(): String {
    val lastIndexOfFileName = lastIndexOf('/')
    return subSequence(lastIndexOfFileName + 1, length).toString()
}

/**
 * 将字节流保存到文件中，工作在 [Dispatchers.IO] 协程上下文中
 * @param body 网络请求响应体
 * @param onPostProgress 在主线程 [Dispatchers.Main] 运行的回调，提供下载进度:[0,100]
 */
suspend fun File.download(body: ResponseBody, onPostProgress : (progress: Int)-> Unit) = withContext(Dispatchers.IO){
    outputStream().use {
        val fileLength = body.contentLength()
        var downloadLength = 0L
        val buffer = ByteArray(10 * 1024)
        val byteStream = body.byteStream()
        var progress: Int
        var currentLength: Int
        currentLength = byteStream.read(buffer)
        while (currentLength != -1) {
            it.write(buffer, 0, currentLength)
            downloadLength += currentLength
            progress = (100 * downloadLength / fileLength).toInt()
            withContext(Dispatchers.Main) {
                onPostProgress(progress)
            }
            currentLength = byteStream.read(buffer)
        }
    }
}

private val toast = Toast.makeText(app, "", Toast.LENGTH_SHORT)

fun showAppToast(message: String) {
    toast.setText(message)
    toast.show()
}