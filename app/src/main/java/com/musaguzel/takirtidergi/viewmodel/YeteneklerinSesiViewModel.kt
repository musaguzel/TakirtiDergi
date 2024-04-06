package com.musaguzel.takirtidergi.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.musaguzel.takirtidergi.firestore.Repository
import com.musaguzel.takirtidergi.model.VideoModel
import com.musaguzel.takirtidergi.util.createDirectory
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

class YeteneklerinSesiViewModel(application: Application) : BaseViewModel(application) {

    private val repository = Repository()
    val songVideos = MutableLiveData<List<VideoModel>>()
    val videoLoading = MutableLiveData<Boolean>()
    var videoLocation = MutableLiveData<String>()
    val videoError = MutableLiveData<Boolean>()

    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext
    val sharedPreferences =
        context.getSharedPreferences("com.musaguzel.takirtidergi", Context.MODE_PRIVATE)

    fun refreshSongVideos(){
        fetchSongVideos()
    }

    fun fetchSongVideos(){
        val oldVideoUrl = sharedPreferences.getString("oldMusicUrl", "")
        videoLoading.value = false
        repository.getVideoData().observeForever { videoDataList ->
            songVideos.value = videoDataList
            val lastVideoUrl = videoDataList[0].videourl

            val directory = File(context.applicationContext.cacheDir , "/YeteneklerinSesi" )

            if (lastVideoUrl != oldVideoUrl || !directory.exists()) {  //ikinci koşul kullanıcının cache silmesinden kaynaklı hata olmaması için
                videoLoading.value = true
                launch {

                    val storage = FirebaseStorage.getInstance()
                    val videoRef = storage.getReferenceFromUrl(lastVideoUrl)

                    try {

                        createDirectory(directory)
                        val localFile: File = File.createTempFile("SarkiVideolari", "mp4",directory)

                        videoRef.getFile(localFile).addOnSuccessListener {

                            showVideo(localFile.toString())

                            sharedPreferences.edit()
                                .putString("oldMusicUrl", lastVideoUrl).apply()
                            sharedPreferences.edit()
                                .putString("musicVideoLocation", localFile.toString()).apply()

                            Toast.makeText(context, "Music Video indirme başarılı", Toast.LENGTH_SHORT).show()

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
                videoLocation.value = sharedPreferences.getString("musicVideoLocation", "")
            }
        }
    }

    private fun showVideo(localFile: String) {
        videoLocation.value = localFile
        videoLoading.value = false
        videoError.value = false
    }

}