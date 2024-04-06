package com.musaguzel.takirtidergi.adapter

import com.musaguzel.takirtidergi.model.Videos

class VideoAdapter (val videoList: ArrayList<Videos>) {

    fun updateVideoList(newVideoList: ArrayList<Videos>){
        videoList.clear()
        videoList.addAll(newVideoList)
    }
}