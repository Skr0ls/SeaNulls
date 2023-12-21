package com.example.seanulls.managers

import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import com.example.seanulls.models.HorizontalShipAssetSample
import com.example.seanulls.models.ShipAssetSample
import com.example.seanulls.models.VerticalShipAssetSample
import java.io.IOException

object AppAssetsManager {
    /**
     * Объект AppAssetsManager отвечает за загрузку ресурсов из ассетов приложения.
     * Он используется для работы с изображениями кораблей, различными элементами интерфейса и другими ресурсами.
     */

    private var context: Context? = null
    private var assets: AssetManager? = null
    private const val THEME_DEFAULT = "themes/default"

    private const val VIEW_SHIPS_UNSELECTED = "ship/preview/unselected"
    private const val VIEW_SHIPS_SELECTED = "ship/preview/selected"
    private const val LAYOUT = "layout"
    private const val SHIP_PARTS_COMMON = "ship/parts/common"
    private const val SHIP_PARTS_SELECTED = "ship/parts/selected"
    private const val SHIP_PARTS_DESTROYED = "ship/parts/destroyed"
    private const val THEME = THEME_DEFAULT

    var ships: ArrayList<ShipAssetSample> = ArrayList()
    var layoutSprites: ArrayList<Drawable> = ArrayList()
        private set


    /**
     * Метод loadAssets загружает необходимые ресурсы из ассетов.
     * Это включает в себя загрузку изображений кораблей и других элементов интерфейса.
     */
    fun loadAssets(_context: Context?) {
        context = _context
        assets = context!!.assets

        loadLayoutSprites()
        loadShipSprites()
    }

    /**
     * Метод loadLayoutSprites загружает спрайты для различных элементов интерфейса игрового поля.
     */
     fun loadLayoutSprites() {
        val PATH = "$THEME/$LAYOUT"
        var items: Array<String>?
        val filter = ArrayList<Drawable>()

        try {
            items = assets!!.list(PATH)
            items?.forEach { item ->
                try{
                    val stream = assets!!.open("$PATH/$item")
                    filter.add(Drawable.createFromStream(stream, null)?: return@forEach)
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        layoutSprites = ArrayList()
        layoutSprites!!.add(filter[22]) //top_left_corner

        for (i in 2..21)  //0..9,a..j
            layoutSprites!!.add(filter[i])
        layoutSprites!!.add(filter[1]) //cell_empty
        layoutSprites!!.add(filter[0]) //cell_checked

        //0 - top_left_corner
        //1-10 - symbols 0..9
        //11-20 - symbols a..j
        //21 - cell_empty
        //22 - cell_checked
    }

    /**
     * Метод loadShipSprites загружает спрайты для изображений кораблей.
     */

     fun loadShipSprites(){
        ships = ArrayList()
        val sprites = ArrayList<Drawable>()
        for (i in 0..1) {
            for (partPack in 0..2) {
                loadShipPartsSprites(partPack, sprites, i)
            }
        }

        for (shipsIndex  in 0..1) {
            loadShipViewSprites(shipsIndex , sprites)
        }

        ships.addAll(createShipAssetsSamples(sprites))

    }

     fun loadShipPartsSprites(partPack: Int, sprites: ArrayList<Drawable>, i: Int){
        var PATH = THEME
        var items: Array<String>?
        //Загрузка частей корабля для игры
        try {
            when(partPack){
                0 ->{
                    PATH += "/$SHIP_PARTS_COMMON"
                    items = assets!!.list(PATH)
                }
                1 ->{
                    PATH += "/$SHIP_PARTS_SELECTED"
                    items = assets!!.list(PATH)
                }
                else ->{
                    PATH += "/$SHIP_PARTS_DESTROYED"
                    items = assets!!.list(PATH)
                }
            }
            val pack = ArrayList<Drawable>()

            for (item in 0..1) {
                try {
                    val stream = assets!!.open("$PATH/${items!![item + item / 1 + i]}")
                    pack.add(Drawable.createFromStream(stream, null)?:continue)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            val filter = ArrayList<Drawable>()
            filter.add(pack[0]) //body
            filter.add(pack[1]) //front
            sprites.addAll(filter)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

     fun loadShipViewSprites(shipIndex: Int, sprites: ArrayList<Drawable>){
        var PATH = THEME
        var items: Array<String>?

        try {
            if (shipIndex == 0) {
                PATH += "/$VIEW_SHIPS_UNSELECTED"
                items = assets!!.list(PATH)
            } else {
                PATH += "/$VIEW_SHIPS_SELECTED"
                items = assets!!.list(PATH)
            }

            val pack = ArrayList<Drawable>()

            for (item in 0..3) {
                try {
                    val stream = assets!!.open("$PATH/${items!![item]}")
                    pack.add(Drawable.createFromStream(stream, null)?:continue)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            sprites.addAll(pack)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

     fun createShipAssetsSamples(sprites: ArrayList<Drawable>): List<ShipAssetSample>{
        val body = ArrayList<Drawable>()
        body.add(sprites[0])
        body.add(sprites[2])
        body.add(sprites[4])

        val front = ArrayList<Drawable>()
        front.add(sprites[1])
        front.add(sprites[3])
        front.add(sprites[5])

        val onePart = ArrayList<Drawable>()
        onePart.add(sprites[12])
        onePart.add(sprites[16])

        val twoPart = ArrayList<Drawable>()
        twoPart.add(sprites[13])
        twoPart.add(sprites[17])

        val threePart = ArrayList<Drawable>()
        threePart.add(sprites[14])
        threePart.add(sprites[18])

        val fourPart = ArrayList<Drawable>()
        fourPart.add(sprites[15])
        fourPart.add(sprites[19])

        ships!!.add(HorizontalShipAssetSample(body, front, onePart, twoPart, threePart, fourPart))

        val _body = ArrayList<Drawable>()
        _body.add(sprites[6])
        _body.add(sprites[8])
        _body.add(sprites[10])

        val _front = ArrayList<Drawable>()
        _front.add(sprites[7])
        _front.add(sprites[9])
        _front.add(sprites[11])

        ships!!.add(VerticalShipAssetSample(_body, _front, onePart, twoPart, threePart, fourPart))

        return ships
    }

    val horizontalShipAssets: HorizontalShipAssetSample
        get() = ships!![0] as HorizontalShipAssetSample
    val verticalShipAssets: VerticalShipAssetSample
        get() = ships!![1] as VerticalShipAssetSample
    
    /*private fun loadShipSprites() {
        ships = ArrayList()
        val sprites = ArrayList<Drawable>()
        for (i in 0..1) {
            for (partPack in 0..2) {
                var PATH = THEME
                var items: Array<String>?
                //Загрузка частей корабля для игры
                try {
                    when(partPack){
                        0 ->{
                            PATH += "/$SHIP_PARTS_COMMON"
                            items = assets!!.list(PATH)
                        }
                        1 ->{
                            PATH += "/$SHIP_PARTS_SELECTED"
                            items = assets!!.list(PATH)
                        }
                        else ->{
                            PATH += "/$SHIP_PARTS_DESTROYED"
                            items = assets!!.list(PATH)
                        }
                    }
                    val pack = ArrayList<Drawable>()

                    for (item in 0..1) {
                        try {
                            val stream = assets!!.open("$PATH/${items!![item + item / 1 +i]}")
                            pack.add(Drawable.createFromStream(stream, null)?:continue)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    val filter = ArrayList<Drawable>()
                    filter.add(pack[0]) //body
                    filter.add(pack[1]) //front
                    sprites.addAll(filter)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //Загрузка полных кораблей для отображения на этапе постановки
        for (ships in 0..1) {
            var PATH = THEME
            var items: Array<String>?

            try {
                if (ships == 0) {
                    PATH += "/$VIEW_SHIPS_UNSELECTED"
                    items = assets!!.list(PATH)
                } else {
                    PATH += "/$VIEW_SHIPS_SELECTED"
                    items = assets!!.list(PATH)
                }

                val pack = ArrayList<Drawable>()

                for (item in 0..3) {
                    try {
                        val stream = assets!!.open("$PATH/${items!![item]}")
                        pack.add(Drawable.createFromStream(stream, null)?:continue)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                sprites.addAll(pack)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        
        val body = ArrayList<Drawable>()
        body.add(sprites[0])
        body.add(sprites[2])
        body.add(sprites[4])

        val front = ArrayList<Drawable>()
        front.add(sprites[1])
        front.add(sprites[3])
        front.add(sprites[5])

        val onePart = ArrayList<Drawable>()
        onePart.add(sprites[12])
        onePart.add(sprites[16])

        val twoPart = ArrayList<Drawable>()
        twoPart.add(sprites[13])
        twoPart.add(sprites[17])

        val threePart = ArrayList<Drawable>()
        threePart.add(sprites[14])
        threePart.add(sprites[18])

        val fourPart = ArrayList<Drawable>()
        fourPart.add(sprites[15])
        fourPart.add(sprites[19])

        ships!!.add(HorizontalShipAssetSample(body, front, onePart, twoPart, threePart, fourPart))

        val _body = ArrayList<Drawable>()
        _body.add(sprites[6])
        _body.add(sprites[8])
        _body.add(sprites[10])

        val _front = ArrayList<Drawable>()
        _front.add(sprites[7])
        _front.add(sprites[9])
        _front.add(sprites[11])

        ships!!.add(VerticalShipAssetSample(_body, _front, onePart, twoPart, threePart, fourPart))
    }*/
}