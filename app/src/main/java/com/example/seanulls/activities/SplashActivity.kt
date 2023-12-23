package com.example.seanulls.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R

/**
 * Активность SplashActivity, отображает экран заставки с логотипом приложения перед запуском главной активности.
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 1500L //Время отображения заставки в миллисекундах
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Находим ImageView для анимации логотипа
        val tv = findViewById<ImageView>(R.id.imageView)

        // Получаем Drawable из ImageView
        val logo = tv.drawable

        //Проверяем, является ли Drawable анимацией
        if (logo is Animatable) {
            //Если является, запускаем анимацию
            (logo as Animatable).start()
            //Задержка перед переходом к MainActivity
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_DISPLAY_LENGTH)
        }
    }
}