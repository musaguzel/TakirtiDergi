package com.musaguzel.takirtidergi.viewmodel

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.musaguzel.takirtidergi.firestore.Repository
import com.musaguzel.takirtidergi.model.Posts

class AnaSayfaViewModel : ViewModel() {

    private val repository = Repository()

    var listState: Parcelable? = null

    val posts = MutableLiveData<List<Posts>>()
    val postError = MutableLiveData<Boolean>()
    val postLoading = MutableLiveData<Boolean>()


    fun refreshData() {
        fetchPostData()
    }



    fun fetchPostData():MutableLiveData<List<Posts>>{
        postLoading.value = true
        repository.getPostData().observeForever { postData ->
            //println(posts.value)

            posts.value = postData
            showPosts(posts)
        }
            return posts
    }
    private fun showPosts(postList: MutableLiveData<List<Posts>>){
        //posts.value = postList.value
        postError.value = false
        postLoading.value = false
    }


    companion object SelectedPosition{
        var selectedIndexList = hashSetOf<String>()
       // var selectedIndexList : ArrayList<Int> = ArrayList()
    }

}



