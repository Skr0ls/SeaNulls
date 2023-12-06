package com.example.seanulls.models

import android.graphics.drawable.Drawable

/**
 * Класс HorizontalShipAssetSample представляет образец изображения горизонтального корабля.
 * Он наследует свойства и методы класса ShipAssetSample для работы с горизонтальными кораблями.
 */
class HorizontalShipAssetSample(
    body: ArrayList<Drawable>,
    front: ArrayList<Drawable>,
    onePartShip: ArrayList<Drawable>,
    twoPartShip: ArrayList<Drawable>,
    threePartShip: ArrayList<Drawable>,
    fourPartShip: ArrayList<Drawable>
) : ShipAssetSample(body, front, onePartShip, twoPartShip, threePartShip, fourPartShip)