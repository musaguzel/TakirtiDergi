package com.musaguzel.takirtidergi.model

import com.google.firebase.firestore.DocumentId

data class VideoModel(
    @DocumentId
    val documentId: String = "",
    val instagram : String = "",
    val videourl : String = "",
    val youtube : String = ""
)
