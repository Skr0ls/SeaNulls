package com.example.seanulls

object ShipPacksStorage {
    /**
     * Хранилище пакетов кораблей для первого и второго игрока.
     * Содержит списки кораблей для каждого игрока.
     */

    // Список кораблей для первого игрока
    @JvmField
    var playerOneShips: ArrayList<Ship>? = null


    // Список кораблей для второго игрока
    @JvmField
    var playerTwoShips: ArrayList<Ship>? = null
}