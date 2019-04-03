package com.zzx.lib.exectors

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DiskIOThreadExecutor: Executor {

    private val diskIO: Executor = Executors.newSingleThreadExecutor()

    override fun execute(command: Runnable?) {
        diskIO.execute(command)
    }
}