package com.example.retro_hardware.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.example.retro_hardware.R

class SplashScreen : AppCompatActivity() {

    companion object {

        // Time out
        const val SPLASH_TIME_OUT = 250L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)
        setTheme(R.style.SplashScreen)

        Handler().postDelayed(Runnable{
            val mainIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, (SPLASH_TIME_OUT))
    }
}