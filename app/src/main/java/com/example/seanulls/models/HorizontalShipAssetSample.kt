package com.example.seanulls.models

import android.graphics.drawable.Drawable

/**
 * Класс HorizontalShipAssetSample представляет собой моедль для хранения горизотнального корабля.
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