package com.gerasimenko.alias.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.gerasimenko.alias.R

class SplashActivity : AppCompatActivity() {

    private var icLogo: ImageView? = null
    var activity: SplashActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        activity = this
        icLogo = findViewById(R.id.ic_logo)
        icLogo!!.startAnimation(AnimationUtils.loadAnimation(this,
            R.anim.splash_in
        ))
        Handler().postDelayed({
            icLogo!!.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.splash_out
            ))
            Handler().postDelayed({
                icLogo!!.visibility = View.GONE
                startActivity(Intent(activity, MainActivity::class.java))
                finish()
            }, 500)
        }, 1500)
    }
}
