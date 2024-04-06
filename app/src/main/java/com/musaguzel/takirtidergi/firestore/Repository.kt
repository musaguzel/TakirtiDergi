package com.musaguzel.takirtidergi.firestore


import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.musaguzel.takirtidergi.model.Posts
import com.musaguzel.takirtidergi.viewmodel.AnaSayfaViewModel
import com.google.firebase.storage.ktx.component1
import com.musaguzel.takirtidergi.model.VideoModel

class Repository {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    var storege = Firebase.storage
    var storageRef = storege.reference

    val selectedIndex = AnaSayfaViewModel.SelectedPosition
    val selectedList = selectedIndex.selectedIndexList

    //Post Data çekme
    fun getPostData(): MutableLiveData<List<Posts>> {
        val mutableData = MutableLiveData<List<Posts>>()

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    //Doldurulacak
                    println(exception.localizedMessage)
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            val postList: List<Posts> = snapshot.toObjects(Posts::class.java)

                            if (selectedList.isNotEmpty()) {
                                //println("deneme" + selectedList.indices)
                                for (i in postList.lastIndex downTo 0) {
                                    if (selectedList.contains(postList[i].documentId)) {
                                        postList[i].selectCommentIndex = true
                                    }
                                }
                            }
                            mutableData.value = postList
                        }
                    }
                }
            }
        return mutableData
    }

    fun getVideoUrl(): MutableLiveData<String> {
        val mutableVideoUrl = MutableLiveData<String>()
        val listRef = storageRef.child("videosAraGundem/")
        listRef.listAll()
            .addOnSuccessListener { (items) ->
                val lastUrl = items[items.lastIndex]
                println(lastUrl)
                mutableVideoUrl.value = lastUrl.toString()

            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
        return mutableVideoUrl

    }

    //Anket tıklama oranlarını güncelleme
    fun updateField(documentID: String, fieldString: String, newValue: Double) {

        firebaseFirestore.collection("Posts").document(documentID)
            .update(fieldString, newValue)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                println(it.localizedMessage)
            }
    }



    fun getVideoData(): MutableLiveData<List<VideoModel>>{
        val mutableLiveData = MutableLiveData<List<VideoModel>>()
        firebaseFirestore.collection("YeteneklerinSesi").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if (error != null){
                println(error.localizedMessage)
            }else {
                if (snapshot != null){
                    val videoList: List<VideoModel> = snapshot.toObjects(VideoModel::class.java)
                    mutableLiveData.value = videoList
                }
            }
        }
        return mutableLiveData
    }


}


/* fun getVideoData():MutableLiveData<List<Videos>>{
        val mutableVideoData = MutableLiveData<List<Videos>>()
        firebaseFirestore.collection("Videos").addSnapshotListener { snapshot, exception ->
            if (exception != null){
                println(exception.localizedMessage)
            }else{
                if (snapshot != null) {
                    if (!snapshot.isEmpty){
                        val videoList: List<Videos> = snapshot.toObjects(Videos::class.java)
                        mutableVideoData.value = videoList
                    }
                }
            }
        }
        return mutableVideoData
    }*/