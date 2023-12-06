package com.example.seanulls

import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import java.util.Arrays

class Ship(@JvmField val id: Int, @JvmField val size: Int, @JvmField val viewShip: ImageView) {

    private var defaultBackground: Drawable? = null

    @JvmField
    var positions: Array<Point?>

    var isHorizontal: Boolean
        private set

    var isPlaced: Boolean
        private set

    init {
        isHorizontal = HORIZONTAL
        isPlaced = IS_NOT_PLACED
        positions = arrayOfNulls(size)
        Arrays.fill(positions, DEFAULT_POSITION)
    }

    fun setDefaultBackground(_defaultBackground: Drawable?) {
        defaultBackground = _defaultBackground
    }

    fun placeShip(_position: Point) {
        for (i in 0 until size) {
            positions[i] = Point(_position.x, _position.y + i)
        }

        isPlaced = IS_PLACED
        viewShip.visibility = View.INVISIBLE
        viewShip.isEnabled = false
    }

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

    fun setOrientation(orientation: Boolean) {
        isHorizontal = orientation
    }

    fun setViewShipDefaultBackground() {
        viewShip.background = defaultBackground
    }

    companion object {
        const val HORIZONTAL = true
        const val VERTICAL = false

        private const val IS_PLACED = true
        private const val IS_NOT_PLACED = false

        @JvmField
        val DEFAULT_POSITION = Point(-1, -1)
    }
}