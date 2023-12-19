package com.example.seanulls

import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import java.util.Arrays

class Ship(@JvmField val id: Int, @JvmField val size: Int, @JvmField val viewShip: ImageView) {

    companion object {
        const val HORIZONTAL = true
        const val VERTICAL = false

        private const val IS_PLACED = true
        private const val IS_NOT_PLACED = false

        @JvmField
        val DEFAULT_POSITION = Point(-1, -1)
    }

    private var defaultBackground: Drawable? = null

    @JvmField
    var positions: Array<Point?> // Массив позиций корабля на игровом поле

    var isHorizontal: Boolean // Ориентация корабля: горизонтальная или вертикальная
        private set

    var isPlaced: Boolean // Размещён ли корабль на игровом поле
        private set

    // Инициализация корабля при создании объекта
    init {
        isHorizontal = HORIZONTAL // Устанавливаем начальную ориентацию - горизонтальную
        isPlaced = IS_NOT_PLACED // Устанавливаем начальное положение - не размещённый
        positions = arrayOfNulls(size) // Инициализация массива позиций корабля заданного размера
        Arrays.fill(positions, DEFAULT_POSITION) // Заполнение массива значениями по умолчанию (неверной позицией)
    }

    // Установка изображения корабля
    fun setDefaultBackground(_defaultBackground: Drawable?) {
        defaultBackground = _defaultBackground
    }

    // Размещение корабля на игровом поле по указанной позиции
    fun placeShip(_position: Point) {
        for (i in 0 until size) {
            positions[i] = Point(_position.x, _position.y + i)
        }

        isPlaced = IS_PLACED
        viewShip.visibility = View.INVISIBLE
        viewShip.isEnabled = false
    }

    // Удаление корабля с игрового поля
    fun removeShip() {
        for (i in 0 until size) {
            positions[i] = DEFAULT_POSITION
        }
        isPlaced = IS_NOT_PLACED
        isHorizontal = HORIZONTAL
        viewShip.background = defaultBackground
        viewShip.visibility = View.VISIBLE
        viewShip.isEnabled = true
    }

    // Установка ориентации корабля (горизонтальной или вертикальной)

    fun setOrientation(orientation: Boolean) {
        isHorizontal = orientation
    }

    // Восстановление изображения по умолчанию для представления корабля
    fun setViewShipDefaultBackground() {
        viewShip.background = defaultBackground
    }
}