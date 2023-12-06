package com.example.seanulls

object PlayableShipManager {

    var ships: ArrayList<PlayableShip>? = null

    fun convert(convertableShips: ArrayList<Ship>): ArrayList<PlayableShip>? {
        ships = ArrayList()
        for (ship in convertableShips) {
            val id = ship.id
            val size = ship.size
            val isHorizontal = ship.isHorizontal
            val positions = ship.positions
            val item = PlayableShip(id, size, isHorizontal, positions)
            ships!!.add(item)
        }

        return ships
    }
}