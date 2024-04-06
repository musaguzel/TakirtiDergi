package com.musaguzel.takirtidergi.view
import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.musaguzel.takirtidergi.R
import com.musaguzel.takirtidergi.viewmodel.YeteneklerinSesiViewModel
import kotlinx.android.synthetic.main.fragment_ara_gundem.*
import kotlinx.android.synthetic.main.fragment_yeteneklerin_sesi.*
import java.lang.Exception


class YeteneklerinSesiFragment : Fragment() , SeekBar.OnSeekBarChangeListener,View.OnClickListener,View.OnTouchListener{

    private lateinit var viewmodel: YeteneklerinSesiViewModel
    lateinit var songvideoView: VideoView

    private var sharedPreferences: SharedPreferences? = null

    lateinit var mediaPlayer: MediaPlayer
    var runnable : Runnable = Runnable {  }
    var handler = Handler(Looper.myLooper()!!)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yeteneklerin_sesi, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = context?.applicationContext?.getSharedPreferences(
            "com.musaguzel.takirtidergi",
            Context.MODE_PRIVATE
        )

        viewmodel = ViewModelProviders.of(this).get(YeteneklerinSesiViewModel::class.java)
        viewmodel.refreshSongVideos()

        songvideoView = songVideoView

        mediaPlayer = MediaPlayer()

        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.anim)
        instagramLabel.startAnimation(animation)
        youtubeLabel.startAnimation(animation)

        observeLiveData()

        swiperereshYetenek.setOnRefreshListener {
            songVideoView.visibility = View.GONE
            //playPauseImage.visibility = View.GONE
            viewmodel.refreshSongVideos()
            //paused = false
            //sharedPreferences?.edit()?.putInt("seekTime", 0)?.apply()
            swiperereshYetenek.isRefreshing = false
        }

        setHandler()
        initializeSeekBarAndListeners()
        hideControllers()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initializeSeekBarAndListeners() {
        songVideoSeekBar.setProgress(0)
        songVideoSeekBar.setOnSeekBarChangeListener(this)
        songvideoView.setOnTouchListener(this)
        playPauseImageVoice.setOnClickListener(this)
        replayImageVoice.setOnClickListener(this)
        forwardImageVoice.setOnClickListener(this)
    }


fun setHandler(){
        runnable = object: Runnable{
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (songvideoView.duration > 0 ){
                    val currentVideoDuration = songvideoView.currentPosition
                    songVideoSeekBar.progress = currentVideoDuration / 1000
                    startTime.text = "" + convertTime(currentVideoDuration)
                    endTime.text = "-" + convertTime(songvideoView.duration - currentVideoDuration)
                }
                handler.postDelayed(this, 0)

            }
        }
    handler.postDelayed(runnable, 500)

}
    private fun convertTime(ms: Int) : String{
        val time: String
        var x: Int = ms / 1000
        val seconds: Int = x % 60
        x /= 60
        val minutes: Int = x % 60
        time = String.format("%02d", minutes)+ ":" + String.format("%02d", seconds)
        return time
    }
    private fun releaseMediaPlayer() {
        /*
        * Remove Callback from the handler...Important
        * */
        handler.removeCallbacks(runnable)
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        releaseMediaPlayer()
        sharedPreferences?.edit()?.putInt("seekTimeVoice",songvideoView.currentPosition)?.apply()
    }



    fun observeLiveData(){

        viewmodel.videoLocation.observe(viewLifecycleOwner, Observer { videoLocation ->
            videoLocation?.let { it ->
                songvideoView.setVideoPath(it)
                songvideoView.visibility = View.VISIBLE


                val seeking = sharedPreferences?.getInt("seekTimeVoice", 0)

                /*if (seeking != null && seeking != 0) {
                    videoView.seekTo(seeking.toInt())
                    savePlayPauseImageState()
                } else {
                    videoView.setOnPreparedListener {
                        it.isLooping = true;
                        videoView.start();
                    }
                }*/
                if (seeking != null && seeking != 0){
                    songvideoView.seekTo(seeking.toInt())
                    songvideoView.pause()
                    playPauseImageVoice.setImageResource(R.drawable.play_image)
                }else{
                    songvideoView.setOnPreparedListener {
                        songVideoSeekBar.max = songvideoView.duration / 1000
                        it.isLooping = true
                        songvideoView.start()
                    }
                }
            }
        })
        viewmodel.songVideos.observe(viewLifecycleOwner, Observer { videoList ->
            videoList?.let { it ->

                instagramLabel.setText(it[0].instagram)
                youtubeLabel.setText(it[0].youtube)

            }
        })

        viewmodel.videoLoading.observe(viewLifecycleOwner, Observer { videoLoading ->
            videoLoading?.let { it ->
                if (it) {
                    shimmerLayoutYetenek.visibility = View.VISIBLE
                    songVideoView.visibility = View.GONE
                } else {
                    shimmerLayoutYetenek.stopShimmer()
                    shimmerLayoutYetenek.visibility = View.GONE
                }

            }
        })



    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        if (seekBar != null) {
            when(seekBar.id){
                R.id.songVideoSeekBar -> {
                    if (fromUser) {
                        songvideoView.seekTo(progress * 1000)
                        val currentVideoDuration = songvideoView.currentPosition
                        startTime.text = "" + convertTime(currentVideoDuration)
                        endTime.text = "" + convertTime(songvideoView.duration)
                    }
                }
            }
        }
    }


    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onClick(v: View?) {
        when (v?.id){

            R.id.playPauseImageVoice -> {
                if (!songvideoView.isPlaying){
                    playPauseImageVoice.setImageResource(R.drawable.pause_image)
                    songvideoView.start()
                }else{
                    songvideoView.pause()
                    playPauseImageVoice.setImageResource(R.drawable.play_image)
                }

            }
            R.id.replayImageVoice -> {
                var seekTime = songvideoView.currentPosition
                seekTime -= 5000
                songvideoView.seekTo(seekTime)
            }
            R.id.forwardImageVoice -> {
                var seekTime = songvideoView.currentPosition
                seekTime += 5000
                songvideoView.seekTo(seekTime)
            }

        }
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
       when(v?.id){
           R.id.songVideoView -> {
               if (event != null) {
                   if (event.action == MotionEvent.ACTION_DOWN) {
                       controllerLayout.visibility = View.VISIBLE
                       val timer = object : CountDownTimer(7000, 1000) {
                           override fun onTick(millisUntilFinished: Long) {
                           }

                           override fun onFinish() {
                               try {
                                   controllerLayout.visibility = View.INVISIBLE
                               } catch (e: Exception) {
                                   println(e)
                               }
                           }
                       }
                       timer.start()
                   }

               }
           }
       }
        return true
    }

    private fun hideControllers(){
        val timer = object : CountDownTimer(5000,1000){
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                try {
                    controllerLayout.visibility = View.INVISIBLE
                }catch (e: Exception){

                }
            }
        }
        timer.start()
    }
}