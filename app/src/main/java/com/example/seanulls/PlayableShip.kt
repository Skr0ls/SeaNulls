package com.example.seanulls

import android.graphics.Point
import android.util.Log

class PlayableShip(@JvmField val id: Int, @JvmField val size: Int, @JvmField val isHorizontal: Boolean, @JvmField val positions: Array<Point?>) {
    private var countOfDestroyedParts = 0
    var isDestroyed = false
        private set


    fun incrementDestroyedParts() {
        countOfDestroyedParts++
        if (countOfDestroyedParts == size) isDestroyed = true
        Log.d("CountOfDestroyedParts", "" + countOfDestroyedParts)
    }
}