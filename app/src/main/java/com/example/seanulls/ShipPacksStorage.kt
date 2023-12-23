package com.example.seanulls

/**
 * Объект ShipPacksStorage является хранилищем пакетов кораблей для игроков, используется для сохранения и обмена списками кораблей.
 * */
object ShipPacksStorage {
    //Список кораблей для первого игрока
    @JvmField
    var playerOneShips: ArrayList<Ship>? = null


    //Список кораблей для второго игрока
    @JvmField
    var playerTwoShips: ArrayList<Ship>? = null
}

/**
 * Хранилище пакетов кораблей для первого и второго игрока.
 * Содержит списки кораблей для каждого игрока.
 */