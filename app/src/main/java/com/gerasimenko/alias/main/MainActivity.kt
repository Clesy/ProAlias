package com.gerasimenko.alias.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.gerasimenko.alias.ConstantsHolder.*
import com.gerasimenko.alias.R
import com.gerasimenko.alias.RulesActivity
import com.gerasimenko.alias.ScoreManagerActivity
import com.gerasimenko.alias.SettingNewGameActivity

class MainActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var resumeGameBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(CURRENT_GAME_DETAILS, MODE_PRIVATE)
        resumeGameBtn = findViewById(R.id.resume_game_btn)
        val newGameBtn: Button = findViewById(R.id.new_game_btn)
        val rulesBtn: Button = findViewById(R.id.rules_btn)
        val logoIv: ImageView = findViewById(R.id.logo_iv)
        val alphaAndRotateAnim = AnimationUtils.loadAnimation(this,
            R.anim.logo_anim
        )
        val alphaShineAnim = AnimationUtils.loadAnimation(this,
            R.anim.shine_anim
        )
        alphaAndRotateAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val logoMp: MediaPlayer = MediaPlayer.create(this@MainActivity,
                    R.raw.bit
                )
                logoMp.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
                logoMp.start()
            }
        })
        logoIv.startAnimation(alphaAndRotateAnim)
        newGameBtn.setOnClickListener {
            val ifNewGame = sharedPreferences!!.getBoolean(IF_NEW_GAME, true)
            if (!ifNewGame) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle(R.string.sure_fore_new_game)
                    .setMessage(R.string.current_will_be_deleted)
                    .setPositiveButton(
                        R.string.yes,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            val editor = sharedPreferences!!.edit()
                            editor.clear()
                            editor.apply()
                            val intent =
                                Intent(this@MainActivity, SettingNewGameActivity::class.java)
                            startActivity(intent)
                        }).setNegativeButton(R.string.no, null).show()
            } else {
                val intent = Intent(this@MainActivity, SettingNewGameActivity::class.java)
                startActivity(intent)
            }
        }
        resumeGameBtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ScoreManagerActivity::class.java)
            intent.putExtra(ON_RESUME, true)
            startActivity(intent)
        }
        rulesBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, RulesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val ifNewGame = sharedPreferences!!.getBoolean(IF_NEW_GAME, true)
        if (!ifNewGame) {
            resumeGameBtn!!.isEnabled = true
            resumeGameBtn!!.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {}
}