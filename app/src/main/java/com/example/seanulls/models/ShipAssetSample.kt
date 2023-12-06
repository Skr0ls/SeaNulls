package com.example.seanulls.models

import android.graphics.drawable.Drawable

open class ShipAssetSample internal constructor(
    var _body: ArrayList<Drawable>,
    var _front: ArrayList<Drawable>,
    var _onePartShip: ArrayList<Drawable>,
    var _twoPartShip: ArrayList<Drawable>,
    var _threePartShip: ArrayList<Drawable>,
    var _fourPartShip: ArrayList<Drawable>
) {
    enum class shipPartType {
        common, selected, destroyed
    }

    enum class shipType {
        selected, unselected
    }

    fun getBody(type: shipPartType): Drawable {
        return if (type == shipPartType.common) _body[0] else if (type == shipPartType.selected) _body[1] else _body[2]
    }

    fun getFront(type: shipPartType): Drawable {
        return if (type == shipPartType.common) _front[0] else if (type == shipPartType.selected) _front[1] else _front[2]
    }

    fun getOnePartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _onePartShip[0] else _onePartShip[1]
    }

    fun getTwoPartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _twoPartShip[0] else _twoPartShip[1]
    }

    fun getThreePartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _threePartShip[0] else _threePartShip[1]
    }

    fun getFourPartShip(type: shipType): Drawable {
        return if (type == shipType.unselected) _fourPartShip[0] else _fourPartShip[1]
    }
}