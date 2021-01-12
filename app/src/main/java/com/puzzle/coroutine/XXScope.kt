package com.puzzle.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    throwable.printStackTrace()
}
/**
 * 可以用来做后台IO任务的协程Scope
 */
val WorkScope = CoroutineScope(SupervisorJob()
        + Dispatchers.IO
        + exceptionHandler
)


// 默认是主线程。
// 目的是：在Activity 或 Fragment里，都只操作ui线程，耗时的子任务应该都应该放到 ViewModel 里去执行。
// 协程：
// launch {
//      start loading dialog  : ui thread
//          .. ..
//      do some bg work       : withContext(Dispatcher.IO | Dispatcher.DEFAULT)
//          .. ..
//      dismiss dialog        : ui thread
// }
// 耗时任务需要 *手动* 切到工作线程。
// 切换方式包括：withContext(IO) 或 async(Dispatcher.IO | Dispatcher.DEFAULT)
// 注意：如果在XXMainScope里直接调用  async ，默认仍是主线程，不是异步的工作线程。
fun XXMainScope() = CoroutineScope(SupervisorJob()
        + Dispatchers.Main
        + exceptionHandler
)


