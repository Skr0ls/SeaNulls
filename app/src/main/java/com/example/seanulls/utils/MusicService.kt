package com.example.seanulls.utils

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.seanulls.R

/**
 * Класс MusicService представляет собой службу для управления проигрыванием музыки в приложении.
 */
class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    //Метод onBind вызывается для привязки к службе. Возвращает null, поскольку служба не поддерживает привязку.
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Метод onCreate вызывается при создании службы и инициализирует воспроизведение музыки из ресурсов.
     */
    override fun onCreate() {
        super.onCreate()
        // Создание MediaPlayer и загрузка музыкального файла для проигрывания
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.xxx_null)
        if (mediaPlayer != null) {
            mediaPlayer!!.isLooping = true
            // Начать воспроизведение музыки после подготовки MediaPlayer
            mediaPlayer!!.setOnPreparedListener { mediaPlayer!!.start() }
        }
    }

    /**
     * Метод onStartCommand вызывается при запуске службы.
     * Возвращает значение START_STICKY, чтобы служба продолжала работу после прерывания системой.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Возвращение флага START_STICKY для повторного запуска службы, если она была остановлена системой
        return START_STICKY
    }

    /**
     * Метод onDestroy вызывается при уничтожении службы и останавливает воспроизведение музыки.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Остановка и освобождение ресурсов MediaPlayer при уничтожении службы
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
    }
}