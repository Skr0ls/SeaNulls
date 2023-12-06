package com.example.seanulls

import android.graphics.Point
import android.util.Log

class PlayableShip(@JvmField val id: Int, @JvmField val size: Int, @JvmField val isHorizontal: Boolean, @JvmField val positions: Array<Point?>) {
    private var countOfDestroyedParts = 0
    var isDestroyed = false
        private set


    /*
    Метод увеличивает счетчик разрушенных частей корабля на 1
    Если количество разрушенных частей равно размеру корабля (size),
    устанавливает флаг isDestroyed в true, обозначая, что корабль разрушен
    */
    fun incrementDestroyedParts() {
        countOfDestroyedParts++
        if (countOfDestroyedParts == size) isDestroyed = true
        Log.d("CountOfDestroyedParts", "" + countOfDestroyedParts)
    }
}