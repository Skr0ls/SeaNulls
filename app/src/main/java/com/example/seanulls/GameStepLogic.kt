package com.example.seanulls

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.seanulls.PlayableShipManager.convert
import com.example.seanulls.activities.ResultActivity
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.managers.AppAssetsManager
import com.example.seanulls.models.ShipAssetSample

class GameStepLogic {
    /**
     * Класс GameStepLogic реализует логику игрового процесса.
     * Он отвечает за управление ходами игроков, обновление таймера, инициализацию игрового поля и обработку нажатий на кнопки игровых полей.
     */

    private var resultIntent: Intent? = null

    // Массивы кнопок для игровых полей двух игроков
    private lateinit var buttonFieldOne: Array<Array<CustomButton?>>
    private lateinit var buttonFieldTwo: Array<Array<CustomButton?>>

    // Переменные для управления таймером
    private var seconds = 20
    private var timer: Chronometer? = null

    // Текстовые поля для отображения ходов игроков
    private var textFirstPlayerStep: TextView? = null
    private var textSecondPlayerStep: TextView? = null

    // Цвет попадания
    private val redСolor = ColorStateList.valueOf(Color.parseColor("#f24e1e"))
    private val hitColor: Int = redСolor.getColorForState(intArrayOf(R.color.red), Color.TRANSPARENT)

    private fun updateStep() {
        timer!!.stop()
        seconds = 20
        timer!!.start()
    }

    private fun changeStep() {
        seconds = 20

        isFirstPlayerStep = !isFirstPlayerStep

        if (isFirstPlayerStep) {
            for (i in 0 until gridFieldOne!!.childCount) {
                gridFieldOne!!.getChildAt(i).isEnabled = false
                gridFieldTwo!!.getChildAt(i).isEnabled = true
            }
            textFirstPlayerStep!!.visibility = View.VISIBLE
            textSecondPlayerStep!!.visibility = View.INVISIBLE
        }
        else {
            for (i in 0 until gridFieldOne!!.childCount) {
                gridFieldOne!!.getChildAt(i).isEnabled = true
                gridFieldTwo!!.getChildAt(i).isEnabled = false
            }

            textFirstPlayerStep!!.visibility = View.INVISIBLE
            textSecondPlayerStep!!.visibility = View.VISIBLE
        }
    }

    // Инициализация таймера и начальных параметров игры
    fun initializeTimer(_textFirstPlayerStep: TextView?, _textSecondPlayerStep: TextView?, _timer: Chronometer?) {
        textFirstPlayerStep = _textFirstPlayerStep
        textSecondPlayerStep = _textSecondPlayerStep

        timer = _timer

        timer!!.base = SystemClock.elapsedRealtime()
        timer!!.onChronometerTickListener = onTickListener

        for (i in 0 until gridFieldOne!!.childCount) {
            gridFieldOne!!.getChildAt(i).isEnabled = false
            gridFieldTwo!!.getChildAt(i).isEnabled = true
        }

        textFirstPlayerStep!!.visibility = View.VISIBLE
        textSecondPlayerStep!!.visibility = View.INVISIBLE

        timer!!.start()
    }

    // Инициализация игрового поля
    fun initializeGameField(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
        gridFieldOne = _fieldOne
        gridFieldTwo = _fieldTwo
        cellSide = _cellSide
        context = _context

        buttonFieldOne = Array(12) { arrayOfNulls(12) }
        buttonFieldTwo = Array(12) { arrayOfNulls(12) }

        layout = AppAssetsManager.layoutSprites

        initializeField(1, gridFieldOne, buttonFieldOne)
        initializeField(2, gridFieldTwo, buttonFieldTwo)

        shipsOne = convert(ShipPacksStorage.playerOneShips!!)
        shipsTwo = convert(ShipPacksStorage.playerTwoShips!!)

        for (ship in shipsOne!!) {
            for (pos in ship.positions) {
                fieldOne[pos!!.x + 1][pos.y + 1] = ship.id
            }
        }

        for (ship in shipsTwo!!) {
            for (pos in ship.positions) {
                fieldTwo[pos!!.x + 1][pos.y + 1] = ship.id
            }
        }
    }

    private fun initializeField(player: Int, field: GridLayout?, buttonField: Array<Array<CustomButton?>>): Boolean {
        for (i in 0..11) for (j in 0..11) buttonField[i][j] = CustomButton(context!!)
        field!!.rowCount = 11
        field.columnCount = 11
        val nullSpace = ImageView(context)
        val params = GridLayout.LayoutParams()
        params.width = cellSide
        params.height = cellSide
        params.setMargins(0, 0, 0, 0)
        nullSpace.layoutParams = params
        nullSpace.background = AppAssetsManager.layoutSprites!![0]
        field.addView(nullSpace)
        for (i in 0..9) {
            val symbol = ImageView(context)
            val topParams = GridLayout.LayoutParams()
            topParams.width = cellSide
            topParams.height = cellSide
            topParams.setMargins(0, 0, 0, 0)
            symbol.background = layout!![i + 1]
            symbol.layoutParams = topParams
            field.addView(symbol)
        }
        for (i in 0..9) {
            val symbol = ImageView(context)
            val leftParams = GridLayout.LayoutParams()
            leftParams.width = cellSide
            leftParams.height = cellSide
            leftParams.setMargins(0, 0, 0, 0)
            symbol.background = layout!![i + 10 + 1]
            symbol.layoutParams = leftParams
            field.addView(symbol)
            buttonField[i][0] = CustomButton(context!!)
            for (j in 0..9) {
                buttonField[i + 1][j + 1] = CustomButton(context!!)
                val gridParams = GridLayout.LayoutParams()
                gridParams.width = cellSide
                gridParams.height = cellSide
                gridParams.setMargins(0, 0, 0, 0)
                buttonField[i + 1][j + 1]!!.id = 100 * player + i * 10 + j
                buttonField[i + 1][j + 1]!!.layoutParams = gridParams
                buttonField[i + 1][j + 1]!!.background = layout!![21]
                if (player == 1) buttonField[i + 1][j + 1]!!
                    .setOnClickListener(firstFieldListener) else if (player == 2) buttonField[i + 1][j + 1]!!
                    .setOnClickListener(secondFieldListener)
                field.addView(buttonField[i + 1][j + 1])
            }
            buttonField[i][11] = CustomButton(context!!)
        }
        return true
    }

    //Отоброажает разрушения корабля на игровом поле
    fun reflector(ship: PlayableShip, buttonField: Array<Array<CustomButton?>>, field: Array<IntArray>): Boolean {
        val body: Drawable
        val front: Drawable
        val lineCount: Int
        val columnCount: Int
        val i = ship.positions[0]!!.x
        val j = ship.positions[0]!!.y

        if (ship.isHorizontal) {
            body = AppAssetsManager.horizontalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)!!
            front = AppAssetsManager.horizontalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)!!
            lineCount = 3
            columnCount = ship.size + 2
        }
        else {
            body = AppAssetsManager.verticalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)!!
            front = AppAssetsManager.verticalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)!!
            lineCount = ship.size + 2
            columnCount = 3
        }

        for (line in 0 until lineCount) {
            for (column in 0 until columnCount) {
                field[+line][j + column] = -1
                buttonField[i + line][j + column]!!.background = layout!![22]
                buttonField[i + line][j + column]!!.setPressedStatus()
            }
        }

        for (position in 0 until ship.size) {
            buttonField[ship.positions[position]!!.x + 1][ship.positions[position]!!.y + 1]!!.background =
                body
            if (position + 1 == ship.size) {
                buttonField[ship.positions[position]!!.x + 1][ship.positions[position]!!.y + 1]!!.background =
                    front
            }
        }
        return true
    }

    private val onTickListener = OnChronometerTickListener { chronometer ->
        chronometer.text = "" + --seconds
        if (seconds == 0) {
            if (isFirstPlayerStep) {
                textFirstPlayerStep!!.visibility = View.INVISIBLE
                textSecondPlayerStep!!.visibility = View.VISIBLE
                changeStep()
                chronometer.text = "" + seconds
            } else {
                textFirstPlayerStep!!.visibility = View.VISIBLE
                textSecondPlayerStep!!.visibility = View.INVISIBLE
                changeStep()
                chronometer.text = "" + seconds
            }
        }
    }

    // Обработка нажатия на кнопку игрового поля первого игрока
    private val firstFieldListener = View.OnClickListener { v ->
        val button = v as CustomButton
        if (!button.isButtonPressed) {
            val x = v.getId().toString()[1].code - 48
            val y = v.getId().toString()[2].code - 48
            var currentShip: PlayableShip? = null
            for (ship in shipsOne!!) {
                if (ship.id == fieldOne[x + 1][y + 1]) {
                    currentShip = ship
                    break
                }
            }
            if (currentShip != null) {
                button.setPressedStatus()
                button.setBackgroundColor(hitColor)
                currentShip.incrementDestroyedParts()
                fieldOne[x + 1][y + 1] = -1
                if (currentShip.isDestroyed) {
                    reflector(currentShip, buttonFieldOne, fieldOne)
                }
                updateStep()
                var n = 0
                for (ship in shipsOne!!) {
                    if (ship.isDestroyed) n++
                }
                if (n == 10) {
                    resultIntent = Intent(context, ResultActivity::class.java).putExtra("winner", 2)
                    context!!.startActivity(resultIntent)
                    AppActivityManager.getGameActivity()?.finish()
                }
            }
            if (currentShip == null) {
                button.setPressedStatus()
                button.background = layout!![22]
                fieldOne[x + 1][y + 1] = -1
                changeStep()
            }
        }
    }

    // Обработка нажатия на кнопку игрового поля второго игрока
    private val secondFieldListener = View.OnClickListener { v ->
        val button = v as CustomButton
        if (!button.isButtonPressed) {
            val x = v.getId().toString()[1].code - 48
            val y = v.getId().toString()[2].code - 48
            var currentShip: PlayableShip? = null
            for (ship in shipsTwo!!) {
                if (ship.id == fieldTwo[x + 1][y + 1]) {
                    currentShip = ship
                    break
                }
            }
            if (currentShip != null) {
                button.setPressedStatus()
                button.setBackgroundColor(Color.RED)
                currentShip.incrementDestroyedParts()
                fieldTwo[x + 1][y + 1] = -1
                if (currentShip.isDestroyed) {
                    reflector(currentShip, buttonFieldTwo, fieldTwo)
                }
                updateStep()
                var n = 0
                for (ship in shipsTwo!!) {
                    if (ship.isDestroyed) n++
                }
                if (n == 10) {
                    context!!.startActivity(
                        Intent(
                            context,
                            ResultActivity::class.java
                        ).putExtra("winner", 1)
                    )
                    AppActivityManager.getGameActivity()?.finish()
                }
            }
            if (currentShip == null) {
                button.setPressedStatus()
                button.background = layout!![22]
                fieldTwo[x + 1][y + 1] = -1
                changeStep()
            }
        }
    }


    companion object {
        private var gridFieldOne: GridLayout? = null
        private var gridFieldTwo: GridLayout? = null

        private var shipsOne: ArrayList<PlayableShip>? = null
        private var shipsTwo: ArrayList<PlayableShip>? = null

        private val fieldOne = Array(12) { IntArray(12) }
        private val fieldTwo = Array(12) { IntArray(12) }

        private var cellSide = 0
        private var context: Context? = null

        var isFirstPlayerStep = true
        var layout: ArrayList<Drawable> = ArrayList()
    }
}