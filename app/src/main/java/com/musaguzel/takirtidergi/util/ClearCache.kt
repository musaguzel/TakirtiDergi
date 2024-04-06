package com.musaguzel.takirtidergi.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class ClearCache(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        if(context.cacheDir.list()?.size!! > 15){

            context.cacheDir.deleteRecursively()
            println("cache deleted")

        }
        return Result.success()
    }

}

