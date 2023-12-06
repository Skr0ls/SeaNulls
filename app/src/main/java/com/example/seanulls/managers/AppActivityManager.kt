package com.example.seanulls.managers

import androidx.appcompat.app.AppCompatActivity

class AppActivityManager {

    /**
     * Класс AppActivityManager представляет менеджер активностей приложения.
     * Он используется для хранения ссылок на различные активности приложения: главное меню, этап размещения кораблей,
     * игровой процесс и экран результатов.
     */

    companion object {
        private var mainMenu: AppCompatActivity? = null
        private var shipPlacingStep: AppCompatActivity? = null
        private var game: AppCompatActivity? = null
        private var result: AppCompatActivity? = null

        // Установка ссылки на активность главного меню
        fun setMenuActivity(activity: AppCompatActivity?) {
            mainMenu = activity
        }

        // Получение ссылки на активность главного меню
        fun getMenuActivity(): AppCompatActivity? {
            return mainMenu
        }

        // Установка ссылки на активность размещения кораблей
        fun setPlacingActivity(activity: AppCompatActivity?) {
            shipPlacingStep = activity
        }

        // Получение ссылки на активность размещения кораблей
        fun getPlacingActivity(): AppCompatActivity? {
            return shipPlacingStep
        }

        // Установка ссылки на игровую активность
        fun setGameActivity(activity: AppCompatActivity?) {
            game = activity
        }

        // Получение ссылки на игровую активность
        fun getGameActivity(): AppCompatActivity? {
            return game
        }

        // Установка ссылки на активность с результатами игры
        fun setResultActivity(activity: AppCompatActivity?) {
            result = activity
        }

        // Получение ссылки на активность с результатами игры
        fun getResultActivity(): AppCompatActivity? {
            return result
        }
    }
}
