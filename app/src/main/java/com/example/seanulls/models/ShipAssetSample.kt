package com.example.seanulls.models

import android.graphics.drawable.Drawable

/**
 * Класс ShipAssetSample представляет собой моедль для хранения изображения корабля.
 */
open class ShipAssetSample internal constructor(
    var _body: ArrayList<Drawable>,
    var _front: ArrayList<Drawable>,
    var _onePartShip: ArrayList<Drawable>,
    var _twoPartShip: ArrayList<Drawable>,
    var _threePartShip: ArrayList<Drawable>,
    var _fourPartShip: ArrayList<Drawable>
) {

    // Перечисление типов частей корабля
    enum class shipPartType {
        common,
        selected,
        destroyed
    }

    // Перечисление типов кораблей
    enum class shipType {
        selected,
        unselected
    }

    /**
     * Метод getBody возвращает соответствующую часть тела корабля в зависимости от типа.
     */
    fun getBody(type: shipPartType): Drawable {
        return if (type == shipPartType.common) _body[0] else if (type == shipPartType.selected) _body[1] else _body[2]
    }

    /**
     * Метод getFront возвращает соответствующую переднюю часть корабля в зависимости от типа.
     */
    fun getFront(type: shipPartType): Drawable {
        return if (type == shipPartType.common) _front[0] else if (type == shipPartType.selected) _front[1] else _front[2]
    }

    /**
     * Метод getOnePartShip возвращает изображение одноклеточного корабля в зависимости от типа.
     */
    fun getOnePartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _onePartShip[0] else _onePartShip[1]
    }

    /**
     * Метод getTwoPartShip возвращает изображение двухклеточного корабля в зависимости от типа.
     */
    fun getTwoPartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _twoPartShip[0] else _twoPartShip[1]
    }

    /**
     * Метод getThreePartShip возвращает изображение трехклеточного корабля в зависимости от типа.
     */
    fun getThreePartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _threePartShip[0] else _threePartShip[1]
    }

    /**
     * Метод getFourPartShip возвращает изображение четырехклеточного корабля в зависимости от типа.
     */
    fun getFourPartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _fourPartShip[0] else _fourPartShip[1]
    }
}