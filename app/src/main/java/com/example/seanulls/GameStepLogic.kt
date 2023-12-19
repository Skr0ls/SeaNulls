package com.example.seanulls

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.SystemClock
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
    private var seconds = 15
    private var timer: Chronometer? = null

    // Текстовые поля для отображения ходов игроков
    private var textFirstPlayerStep: TextView? = null
    private var textSecondPlayerStep: TextView? = null

    private val hitColor = Color.RED

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

    private fun updateStep() {
        timer!!.stop()
        seconds = 15
        timer!!.start()
    }

    private fun changeStep() {
        seconds = 15

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
    /*fun initializeGameField(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
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
    }*/

    fun initializeGameField(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
        setupFieldsAndButtons(_fieldOne, _fieldTwo, _cellSide, _context)

        initializeGameFields()

        convertAndSetShips()
    }

    private fun setupFieldsAndButtons(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
        gridFieldOne = _fieldOne
        gridFieldTwo = _fieldTwo
        cellSide = _cellSide
        context = _context

        buttonFieldOne = Array(12) { arrayOfNulls(12) }
        buttonFieldTwo = Array(12) { arrayOfNulls(12) }

        layout = AppAssetsManager.layoutSprites
    }

    private fun initializeGameFields(){
        initializeField(1, gridFieldOne, buttonFieldOne)
        initializeField(2, gridFieldTwo, buttonFieldTwo)
    }

    private fun convertAndSetShips(){
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

    private fun initializeField(player: Int, field: GridLayout?, buttonField: Array<Array<CustomButton?>>): Boolean{
        // Этап 1: Инициализация переменных и настройка размеров поля
        initializeButtonField(buttonField)
        setupFieldSize(field)

        val nullSpace = createNullSpace()
        field?.addView(nullSpace)

        // Этап 2: Добавление элементов в GridLayout
        addSymbolsToGridLayout(field)

        // Этап 3: Добавление кнопок и элементов для обоих игроков
        addButtonsAndSymbolsForPlayers(player, field, buttonField)

        // Возвращается значение true в конце метода
        return true
    }

    private fun initializeButtonField(buttonField: Array<Array<CustomButton?>>) {
        for (i in 0..11) {
            for (j in 0..11) {
                buttonField[i][j] = CustomButton(context!!)
            }
        }
    }

    private fun setupFieldSize(field: GridLayout?) {
        field?.apply {
            rowCount = 11
            columnCount = 11
        }
    }

    private fun createNullSpace(): ImageView{
        val nullSpace = ImageView(context)
        val params = GridLayout.LayoutParams()

        params.width = cellSide
        params.height = cellSide
        params.setMargins(0, 0, 0, 0)

        nullSpace.layoutParams = params
        nullSpace.background = AppAssetsManager.layoutSprites!![0]

        return nullSpace
    }

    private fun addSymbolsToGridLayout(field: GridLayout?) {
        field?.apply {
            for (i in 0..9) {
                val symbol = ImageView(context)
                val topParams = GridLayout.LayoutParams()

                topParams.width = cellSide
                topParams.height = cellSide
                topParams.setMargins(0, 0, 0, 0)

                symbol.background = layout!![i + 1]
                symbol.layoutParams = topParams

                addView(symbol)
            }
        }
    }

    private fun addButtonsAndSymbolsForPlayers(player: Int, field: GridLayout?, buttonField: Array<Array<CustomButton?>>) {
        field?.apply {
            for (i in 0..9) {
                val symbol = ImageView(context)
                val leftParams = GridLayout.LayoutParams()

                leftParams.width = cellSide
                leftParams.height = cellSide
                leftParams.setMargins(0, 0, 0, 0)

                symbol.background = layout!![i + 10 + 1]
                symbol.layoutParams = leftParams

                addView(symbol)
                buttonField[i][0] = CustomButton(context!!)

                for (j in 0 until 10) {
                    // Остальные операции по добавлению кнопок и слушателей оставлены здесь
                    // для добавления кнопок и слушателей для игроков
                    buttonField[i + 1][j + 1] = CustomButton(context!!)
                    val gridParams = GridLayout.LayoutParams()

                    gridParams.width = cellSide
                    gridParams.height = cellSide
                    gridParams.setMargins(0, 0, 0, 0)

                    buttonField[i + 1][j + 1]!!.id = 100 * player + i * 10 + j
                    buttonField[i + 1][j + 1]!!.layoutParams = gridParams
                    buttonField[i + 1][j + 1]!!.background = layout!![21]

                    // Установка слушателей для разных игроков
                    if (player == 1) buttonField[i + 1][j + 1]!!.setOnClickListener(firstFieldListener)
                    else if (player == 2) buttonField[i + 1][j + 1]!!.setOnClickListener(secondFieldListener)

                    field.addView(buttonField[i + 1][j + 1])
                }
                buttonField[i][11] = CustomButton(context!!)
            }
        }
    }


    /*private fun initializeField(player: Int, field: GridLayout?, buttonField: Array<Array<CustomButton?>>): Boolean {
        for (i in 0..11)
            for (j in 0..11) buttonField[i][j] = CustomButton(context!!)
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
    }*/

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

    private fun handleGameEnd(winner: Int){
        resultIntent = Intent(context, ResultActivity::class.java).putExtra("winner", winner)
        context!!.startActivity(resultIntent)
        AppActivityManager.getGameActivity()?.finish()
    }

    private fun allButtonsMatchCriteria(): Boolean {
        for (i in 1 until fieldOne.size - 1) {
            for (j in 1 until fieldOne[i].size - 1) {
                val buttonOne = buttonFieldOne[i][j]
                val buttonTwo = buttonFieldTwo[i][j]
                //Проверяем ассеты кнопки
                if (!checkAssets(buttonOne!!)) return false

                if(!checkAssets(buttonTwo!!)) return false
            }
        }
        return true
    }

    //Метод для проверки ассетов кнопки
    private fun checkAssets(button: CustomButton): Boolean{
        val buttonBackground = button.background

        return buttonBackground?.let {
            it.equals(AppAssetsManager.horizontalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)) ||
                    it.equals(AppAssetsManager.horizontalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)) ||
                    it.equals(AppAssetsManager.verticalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)) ||
                    it.equals(AppAssetsManager.verticalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)) ||
                    it.equals(hitColor) ||
                    it.equals(layout[22])
        } ?: false
    }

    private fun handleButtonClick(isFirstPlayer: Boolean, x: Int, y :Int) {
        val buttonField = if (isFirstPlayer) buttonFieldOne else buttonFieldTwo
        val field = if (isFirstPlayer) fieldOne else fieldTwo
        val ships = if (isFirstPlayer) shipsOne else shipsTwo
        val textPlayerStep = if (isFirstPlayer) textFirstPlayerStep else textSecondPlayerStep

        val currentButton = buttonField[x + 1][y + 1]

        if (!currentButton!!.isButtonPressed) {
            if (!currentButton!!.isButtonPressed) {
                var currentShip: PlayableShip? = null

                for (ship in ships!!) {
                    if (ship.id == field[x + 1][y + 1]) {
                        currentShip = ship
                        break
                    }
                }
                if (currentShip != null) {
                    currentButton.setPressedStatus()
                    currentButton.setBackgroundColor(hitColor)
                    currentShip.incrementDestroyedParts()
                    field[x + 1][y + 1] = -1

                    if (currentShip.isDestroyed) {
                        reflector(
                            currentShip,
                            if (isFirstPlayer) buttonFieldOne else buttonFieldTwo,
                            if (isFirstPlayer) fieldOne else fieldTwo
                        )
                    }
                    updateStep()

                    var n = 0
                    for (ship in ships) {
                        if (ship.isDestroyed) n++
                    }

                    if (n == 10) handleGameEnd(if (isFirstPlayer) 2 else 1)
                }

                if (currentShip == null) {
                    currentButton.setPressedStatus()
                    currentButton.background = layout!![22]
                    field[x + 1][y + 1] = -1
                    changeStep()

                    if (allButtonsMatchCriteria()) handleGameEnd(if (isFirstPlayer) 2 else 1)
                }
            }
        }
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
        val x = v.id.toString()[1].code - 48
        val y = v.id.toString()[2].code - 48
        handleButtonClick(true, x, y)
    }

    // Обработка нажатия на кнопку игрового поля второго игрока
    private val secondFieldListener = View.OnClickListener { v ->
        val x = v.id.toString()[1].code - 48
        val y = v.id.toString()[2].code - 48
        handleButtonClick(false, x, y)
    }
}