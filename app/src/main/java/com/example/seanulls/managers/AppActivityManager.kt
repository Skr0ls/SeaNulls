package com.example.seanulls.managers

import androidx.appcompat.app.AppCompatActivity

class AppActivityManager {
    companion object {
        private var mainMenu: AppCompatActivity? = null
        private var shipPlacingStep: AppCompatActivity? = null
        private var game: AppCompatActivity? = null
        private var result: AppCompatActivity? = null

        fun setMenuActivity(activity: AppCompatActivity?) {
            mainMenu = activity
        }

        fun getMenuActivity(): AppCompatActivity? {
            return mainMenu
        }

        fun setPlacingActivity(activity: AppCompatActivity?) {
            shipPlacingStep = activity
        }

        fun getPlacingActivity(): AppCompatActivity? {
            return shipPlacingStep
        }

        fun setGameActivity(activity: AppCompatActivity?) {
            game = activity
        }

        fun getGameActivity(): AppCompatActivity? {
            return game
        }

        fun setResultActivity(activity: AppCompatActivity?) {
            result = activity
        }

        fun getResultActivity(): AppCompatActivity? {
            return result
        }
    }
}
