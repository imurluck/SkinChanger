package com.zzx.lib.exectors

import java.util.concurrent.Executors

object SkinExecutor {

    private const val THREAD_COUNT = 3

    val diskIO = Executors.newSingleThreadExecutor()
    val networkIO = Executors.newFixedThreadPool(THREAD_COUNT)
    val mainThread = MainThreadExecutor()

}