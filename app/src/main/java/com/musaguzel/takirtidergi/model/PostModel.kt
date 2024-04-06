package com.musaguzel.takirtidergi.model

import com.google.firebase.firestore.DocumentId
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList
data class Posts(
        @DocumentId
        val documentId: String = "",
        val CommentClick0: Double = 0.0,
        val CommentClick1: Double = 0.0,
        val CommentClick2: Double = 0.0,
        val CommentList: List<String> = ArrayList(),
        val date: Date? = Date(),
        val imageUrl: String? = "",
        var selectCommentIndex: Boolean? = null)








