package com.example.seanulls

import android.graphics.Point
import android.util.Log

/**
 * Класс PlayableShip представляет собой "играбельный" корабль
 * */
class PlayableShip( val id: Int,
                    val size: Int,
                    val isHorizontal: Boolean,
                    val positions: Array<Point?>) {
    private var countOfDestroyedParts = 0 //Кол-во разрушенных частей корабля
    var isDestroyed = false //Флаг обозначающий, что корабль разрушен
        private set//Приветный сеттер для этого флага


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