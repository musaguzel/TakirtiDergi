package com.musaguzel.takirtidergi.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.musaguzel.takirtidergi.R
import com.musaguzel.takirtidergi.util.ClearCache
import com.musaguzel.takirtidergi.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_yeteneklerin_sesi.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var sharedPreferences: SharedPreferences? = null

    val selectedIndex = AnaSayfaViewModel.SelectedPosition
    val selectedList = selectedIndex.selectedIndexList

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = this.applicationContext?.getSharedPreferences(
            "com.musaguzel.takirtidergi",
            Context.MODE_PRIVATE
        )
        val nightMode = sharedPreferences?.getBoolean("NightMode",false)
        if (nightMode == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_Dark)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_Light)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sharedPreferences?.edit()?.remove("seekTime")?.apply()
        sharedPreferences?.edit()?.remove("seekTimeVoice")?.apply()


//nightMode = sharedPreferences?.getBoolean("NightMode",false)

        val myWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ClearCache>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueue(myWorkRequest)

        val hashset = HashSet<String>()
        var kontrol: HashSet<String>? = null

        kontrol = sharedPreferences?.getStringSet("uuidset", hashset) as HashSet<String>?
        if (kontrol != null) {
            selectedList.clear()
            sharedPreferences?.getStringSet("uuidset", hashset)?.let { selectedList.addAll(it) }
        }

        setSupportActionBar(toolactionbar)

        //BottomNavigation ile fragment bağlama '' ActionBarın fragment adlarına dönüştürme


        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        //navController = findNavController(R.id.fragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)



        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.anaSayfaFragment,
                R.id.araGundemFragment,
                R.id.dundenDuneFragment,
                R.id.yeteneklerinSesiFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)


        modeswitch.isChecked = nightMode == true

        modeswitch.setOnCheckedChangeListener { buttonView, isChecked ->
            //check condition
            if (isChecked){
                //set night mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences?.edit()?.putBoolean("NightMode",true)?.apply()
            }else{
                //set light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences?.edit()?.putBoolean("NightMode",false)?.apply()
                //voiceLayout.setBackgroundResource(R.drawable.perde)
            }
        }

    }

    /*override fun onSupportNavigateUp(): Boolean {
      return navController.navigateUp(appBarConfiguration)
   }*/
}