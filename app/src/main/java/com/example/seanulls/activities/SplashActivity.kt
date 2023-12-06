package com.example.seanulls.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val tv = findViewById<ImageView>(R.id.imageView)
        val logo = tv.drawable
        if (logo is Animatable) {
            (logo as Animatable).start()
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_DISPLAY_LENGTH)
        }
    }

    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 1500L
    }
}