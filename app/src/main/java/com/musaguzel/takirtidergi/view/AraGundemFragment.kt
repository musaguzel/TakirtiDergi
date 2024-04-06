package com.musaguzel.takirtidergi.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.musaguzel.takirtidergi.R
import com.musaguzel.takirtidergi.viewmodel.AraGundemViewModel
import kotlinx.android.synthetic.main.fragment_ara_gundem.*
import java.lang.Exception

class AraGundemFragment : Fragment() {

    private lateinit var viewModel: AraGundemViewModel

    lateinit var videoView: VideoView
    private var sharedPreferences: SharedPreferences? = null

    //videoView
    private var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ara_gundem, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AraGundemViewModel::class.java)
        viewModel.refreshData()

        sharedPreferences = context?.applicationContext?.getSharedPreferences(
            "com.musaguzel.takirtidergi",
            Context.MODE_PRIVATE
        )
        videoView = video_view


        /*val mediaController = MediaController(view.context)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)*/


        observeLiveData()
        playPauseSettings()

        swipeRefresh.setOnRefreshListener {
            video_view.visibility = View.GONE
            playPauseImage.visibility = View.GONE
            viewModel.refreshData()
            //paused = false
            sharedPreferences?.edit()?.putInt("seekTime", 0)?.apply()
            swipeRefresh.isRefreshing = false
        }
    }

    fun observeLiveData() {

        viewModel.videoLocation.observe(viewLifecycleOwner, Observer { videoLocation ->
            videoLocation?.let { it ->
                video_view.visibility = View.VISIBLE
                videoView.setVideoPath(it)
                val seeking = sharedPreferences?.getInt("seekTime", 0)

                if (seeking != null && seeking != 0) {
                    videoView.seekTo(seeking.toInt())
                    savePlayPauseImageState()
                } else {
                    videoView.setOnPreparedListener {
                        it.isLooping = true;
                        videoView.start();
                    }
                }
            }
        })

        viewModel.videoLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    shimmerLayout.visibility = View.VISIBLE
                    video_view.visibility = View.GONE
                } else {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                }
            }
        })
    }

    fun showCustomController(){
        try {
            replayImage.visibility = View.VISIBLE
            playPauseImage.visibility = View.VISIBLE
            forwardImage.visibility = View.VISIBLE
        }catch (e: Exception){
            println(e.localizedMessage)
        }
    }
    fun hideCustomController(){
        try {
            replayImage.visibility = View.GONE
            playPauseImage.visibility = View.GONE
            forwardImage.visibility = View.GONE
        }catch (e: Exception){
            println(e.localizedMessage)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun playPauseSettings() {
        videoView.setOnTouchListener { v, event ->
            //Video kontrolleri için ekrana dokunma ayarları
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    showCustomController()

                    if (videoView.isPlaying){
                        playPauseImage.setImageResource(R.drawable.pause_image)
                        val timer = object : CountDownTimer(3000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                hideCustomController()
                            }
                        }
                        timer.start()
                    }


                }
            }
            true
        }
        playPauseImage.setOnClickListener {
            if (!videoView.isPlaying) {
                playPauseImage.setImageResource(R.drawable.pause_image)
                videoView.start()
                //paused = false
                //Pause ikonunun kaybolması için süre ayarlama
                val timer = object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        try {
                            hideCustomController()
                        } catch (e: Exception) {
                            println(e.localizedMessage)
                        }
                    }
                }
                timer.start()

            } else {
                videoView.pause()
                playPauseImage.setImageResource(R.drawable.play_image)
                //paused = true
                showCustomController()
            }

        }

        replayImage.setOnClickListener {
            var seekTime = videoView.currentPosition
            seekTime -= 5000
            videoView.seekTo(seekTime)
        }
        forwardImage.setOnClickListener {
            var seekTime = videoView.currentPosition
            seekTime += 5000
            videoView.seekTo(seekTime)
        }
    }

    fun savePlayPauseImageState() {

        videoView.pause()
        //playPauseImage.visibility = View.VISIBLE  ya bu
        showCustomController() // ya da bu
        //paused = true
        playPauseImage.setImageResource(R.drawable.play_image)
        val timer = object : CountDownTimer(1800, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                try {
                    hideCustomController()
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }
        }
        timer.start()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.edit()?.putInt("seekTime", videoView.currentPosition)?.apply()
        println(videoView.currentPosition)
    }
}

