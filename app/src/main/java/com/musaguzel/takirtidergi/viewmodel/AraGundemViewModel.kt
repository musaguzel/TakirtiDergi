package com.musaguzel.takirtidergi.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.musaguzel.takirtidergi.firestore.Repository
import com.musaguzel.takirtidergi.util.createDirectory
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

@SuppressLint("StaticFieldLeak")
class AraGundemViewModel(application: Application) : BaseViewModel(application) {


    private val repository = Repository()
    var videoLocation = MutableLiveData<String>()
    val videoLoading = MutableLiveData<Boolean>()
    val videoError = MutableLiveData<Boolean>()


    val context = getApplication<Application>().applicationContext
    val sharedPreferences =
        context.getSharedPreferences("com.musaguzel.takirtidergi", Context.MODE_PRIVATE)


    fun refreshData() {
        //süre eklenecek o süreye göre aralıklı yenilenecek
        fethcUrlAndDownloadVideo()
    }


    fun fethcUrlAndDownloadVideo() {
        val oldVideoUrl = sharedPreferences.getString("oldUrl", "")
        videoLoading.value = false

        repository.getVideoUrl().observeForever { videoUrl ->

           // val cachedir = context.cacheDir.list()?.lastIndex
            val dir = File(context.applicationContext.cacheDir , "/AraGundem" )
            if (videoUrl != oldVideoUrl || !dir.exists()) {  //ikinci koşul kullanıcının cache silmesinden kaynaklı hata olmaması için
                videoLoading.value = true
                launch {

                    val storage = FirebaseStorage.getInstance()
                    val videoRef = storage.getReferenceFromUrl(videoUrl)
                    //println("lastindexx " + cacheFolder.list().lastIndex)

                    try {

                        createDirectory(dir)
                        val localFile: File = File.createTempFile("AraGundemVideolari", "mp4",dir)

                        videoRef.getFile(localFile).addOnSuccessListener {

                            showVideo(localFile.toString())

                            sharedPreferences.edit()
                                .putString("oldUrl", videoUrl).apply()
                            sharedPreferences.edit()
                                .putString("videoLocation", localFile.toString()).apply()

                            Toast.makeText(context, "indirme başarılı", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener {
                            videoLoading.value = false
                            videoError.value = true
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
                }
            } else {
                println("bu video zaten indirilmiş")
                videoLocation.value = sharedPreferences.getString("videoLocation", "")
            }

        }
    }

    private fun showVideo(localFile: String) {
        videoLocation.value = localFile
        videoLoading.value = false
        videoError.value = false
    }
}


