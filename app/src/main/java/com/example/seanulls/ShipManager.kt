package com.example.seanulls

import android.app.Activity
import android.widget.ImageView

class ShipManager : Activity() {
    /**
     * Менеджер кораблей. Управляет созданием и загрузкой кораблей в игре.
     */


    /**
     * Инициализирует корабли с помощью предоставленных представлений [views].
     * @param views Представления ImageView, представляющие корабли.
     * @return true, если корабли успешно инициализированы, иначе false.
     */

    companion object {
        // Список кораблей
        @JvmStatic
        var ships: ArrayList<Ship>? = null
            private set

        /**
         * Загружает пакет кораблей [shipPack].
         * @param shipPack Пакет кораблей для загрузки.
         */
        fun loadShipPack(shipPack: ArrayList<Ship>?) {
            ships = shipPack
        }
    }

    fun initializeShips(views: ArrayList<ImageView?>): Boolean {
        ships = ArrayList()
        ships!!.add(Ship(10, 1, views[0]!!))
        ships!!.add(Ship(11, 1, views[1]!!))
        ships!!.add(Ship(12, 1, views[2]!!))
        ships!!.add(Ship(13, 1, views[3]!!))
        ships!!.add(Ship(20, 2, views[4]!!))
        ships!!.add(Ship(21, 2, views[5]!!))
        ships!!.add(Ship(22, 2, views[6]!!))
        ships!!.add(Ship(30, 3, views[7]!!))
        ships!!.add(Ship(31, 3, views[8]!!))
        ships!!.add(Ship(4, 4, views[9]!!))
        return true
    }
}