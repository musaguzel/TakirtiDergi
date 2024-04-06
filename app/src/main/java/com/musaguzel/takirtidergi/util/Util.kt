package com.musaguzel.takirtidergi.util

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.musaguzel.takirtidergi.R
import kotlinx.android.synthetic.main.fragment_ara_gundem.view.*
import java.io.File
import java.lang.IllegalStateException

fun ImageView.getImageFromFirebase(url: String?, shimmerDrawable: ShimmerDrawable) {

    val options = RequestOptions()
        .placeholder(shimmerDrawable)
        .error(R.mipmap.ic_launcher_round)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)

}

fun createDirectory(dir: File) {
    if (!dir.exists()){
        if (!dir.mkdirs()){
           dir.mkdirs()
        }
    }
}

fun placeholderShimmer(context: Context): ShimmerDrawable {

    val shimmer =
        Shimmer.ColorHighlightBuilder()
            .setBaseColor(Color.parseColor("#FFFFFF"))
            .setHighlightColor(Color.parseColor("#E8E8E8"))
            .setDuration(600)
            .setBaseAlpha(0.7F)
            .setHighlightAlpha(0.8f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

    return ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

}

/*fun placeholderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}*/


//controller yapılacak , her zaman yeni videonun gösterildiğinden emin olunacak, belki video üstüne "Yeteneklerin Sesi" diye başlık yapılacak.
//her fragmenta yüzdesel ağırlık lar eklenecek normal telefonda apk denenecek
