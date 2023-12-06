package com.example.seanulls.models

import android.graphics.drawable.Drawable

/**
 * Класс VerticalShipAssetSample представляет образец изображения вертикального корабля.
 * Он наследует свойства и методы класса ShipAssetSample для работы с вертикальными кораблями.
 */
class VerticalShipAssetSample(
    body: ArrayList<Drawable>,
    front: ArrayList<Drawable>,
    onePartShip: ArrayList<Drawable>,
    twoPartShip: ArrayList<Drawable>,
    threePartShip: ArrayList<Drawable>,
    fourPartShip: ArrayList<Drawable>
) : ShipAssetSample(body, front, onePartShip, twoPartShip, threePartShip, fourPartShip)