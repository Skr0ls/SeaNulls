package com.example.seanulls

object PlayableShipManager {

    var ships: ArrayList<PlayableShip>? = null // Список игровых кораблей PlayableShip

    // Метод преобразует список кораблей из класса Ship в список игровых кораблей PlayableShip
    fun convert(convertableShips: ArrayList<Ship>): ArrayList<PlayableShip>? {
        ships = ArrayList()
        for (ship in convertableShips) {
            val id = ship.id
            val size = ship.size
            val isHorizontal = ship.isHorizontal
            val positions = ship.positions
            val item = PlayableShip(id, size, isHorizontal, positions) // Создание экземпляра игрового корабля PlayableShip
            ships!!.add(item) // Добавление игрового корабля в список игровых кораблей PlayableShip
        }

        return ships // Возвращает преобразованный список игровых кораблей PlayableShip
    }
}