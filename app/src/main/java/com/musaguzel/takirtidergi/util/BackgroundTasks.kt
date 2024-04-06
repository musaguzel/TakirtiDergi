package com.musaguzel.takirtidergi.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.lang.Exception

class BackgroundTasks(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {


    override fun doWork(): Result {
        downloadVideo()
        return Result.success()
    }


    @SuppressLint("CommitPrefEdits")
    private fun downloadVideo() {

        val getData = inputData
        val videoUri = getData.getString("videoFirebaseUri")
        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.getReferenceFromUrl(videoUri.toString())
        val sharedPreferences = context.getSharedPreferences("com.musaguzel.takirtidergi", Context.MODE_PRIVATE)
        val controlUri = sharedPreferences.getString("controlUri", "")
        val cacheFolder = context.applicationContext.cacheDir
        println("lastindexx " + cacheFolder.list().lastIndex)
        if (videoUri != controlUri || cacheFolder.list().lastIndex == 0) {  //ikinci koşul kullanıcının cache silmesinden kaynaklı hata olmaması için
            try {
                val localFile: File = File.createTempFile("AraGundemVideolari", "mp4")
                videoRef.getFile(localFile).addOnSuccessListener {
                    Toast.makeText(context, "indirme başarılı", Toast.LENGTH_SHORT).show()
                    sharedPreferences.edit().putString("controlUri", videoUri.toString()).apply()
                    sharedPreferences.edit().putString("localFile", localFile.toString()).apply()
                }.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "indirme başarısız: " + it.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    println(it.localizedMessage)
                }

            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        } else {
            println("bu video zaten indirilmiş")
        }

    }


}