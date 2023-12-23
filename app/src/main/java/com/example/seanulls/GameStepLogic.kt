package com.example.seanulls

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
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

/**
 * Класс GameStepLogic реализует логику игрового процесса.
 * Он отвечает за управление ходами игроков, обновление таймера, инициализацию игрового поля и обработку нажатий на кнопки игровых полей.
 */
class GameStepLogic {
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
        //Игровые поля
        private var gridFieldOne: GridLayout? = null
        private var gridFieldTwo: GridLayout? = null

        //Списки кораблей для каждого игрока
        private var shipsOne: ArrayList<PlayableShip>? = null
        private var shipsTwo: ArrayList<PlayableShip>? = null

        private val fieldOne = Array(12) { IntArray(12) }
        private val fieldTwo = Array(12) { IntArray(12) }

        //Размер ячейки игрового поля
        private var cellSide = 0

        private var context: Context? = null

        //Флаг для опредления очереди игрока
        var isFirstPlayerStep = true

        var layout: ArrayList<Drawable> = ArrayList()
    }

    //Метод обновляющий ход, если игрок попал по противнику
    private fun updateStep() {
        timer!!.stop()
        seconds = 15
        timer!!.start()
    }

    //Метод меняющий ход, если игрок промахнулся или истекло время
    private fun changeStep() {
        seconds = 15
        isFirstPlayerStep = !isFirstPlayerStep //Смена текущего игрока
        setGridFieldsEnabled(isFirstPlayerStep) //Установка доступности игрового поля для другого игрока
    }

    //Метод для установки доступности полей, в зависимости от текущего игрока
    private fun setGridFieldsEnabled(isFirstPlayerStep: Boolean){
        //Видимость TextView обозночающего ход
        textFirstPlayerStep?.visibility = if (isFirstPlayerStep) View.VISIBLE else View.INVISIBLE
        textSecondPlayerStep?.visibility = if (isFirstPlayerStep) View.INVISIBLE else View.VISIBLE

        //Установка доступности игровых полей для каждого игрока
        val fieldOneEnabled = !isFirstPlayerStep
        val fieldTwoEnabled = isFirstPlayerStep

        for (i in 0 until gridFieldOne!!.childCount) {
            gridFieldOne!!.getChildAt(i).isEnabled = fieldOneEnabled
            gridFieldTwo!!.getChildAt(i).isEnabled = fieldTwoEnabled
        }
    }

    //Инициализация таймер
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
    /*fun initializeTimer(_textFirstPlayerStep: TextView?, _textSecondPlayerStep: TextView?, _timer: Chronometer?) {
        if (_textFirstPlayerStep == null || _textSecondPlayerStep == null || _timer == null) {
            return
        }

        setupTimer(_textFirstPlayerStep, _textSecondPlayerStep, _timer)
        disableGridChildren()
        setStepTextViewVisibility()

        timer?.start()
    }

    private fun setupTimer(_textFirstPlayerStep: TextView, _textSecondPlayerStep: TextView, _timer: Chronometer) {
        textFirstPlayerStep = _textFirstPlayerStep
        textSecondPlayerStep = _textSecondPlayerStep
        timer = _timer

        timer!!.base = SystemClock.elapsedRealtime()
        timer!!.onChronometerTickListener = onTickListener
    }

    private fun disableGridChildren() {
        val grids = listOf(gridFieldOne, gridFieldTwo)

        grids.forEach { grid ->
            grid?.children?.forEach { child ->
                child.isEnabled = false
            }
        }
    }

    private fun setStepTextViewVisibility() {
        textFirstPlayerStep?.visibility = View.VISIBLE
        textSecondPlayerStep?.visibility = View.INVISIBLE
    }*/

    //Метод инициализирующий игровое поле
    fun initializeGameField(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
        //Установка переменных для поля и кнопок
        setupFieldsAndButtons(_fieldOne, _fieldTwo, _cellSide, _context)

        //Инициализация полей игры
        initializeGameFields()

        //Конвертация кораблей и установка их позиций на полях
        convertAndSetShips()
    }

    //Метод устанавливающий поля и кнопки дял игрового поля
    private fun setupFieldsAndButtons(_fieldOne: GridLayout?, _fieldTwo: GridLayout?, _cellSide: Int, _context: Context?){
        gridFieldOne = _fieldOne
        gridFieldTwo = _fieldTwo
        cellSide = _cellSide
        context = _context

        //Инициализация массивов кнопок для каждого поля
        buttonFieldOne = Array(12) { arrayOfNulls(12) }
        buttonFieldTwo = Array(12) { arrayOfNulls(12) }

        //Получение спрайтов из AppAssetsManager
        layout = AppAssetsManager.layoutSprites
    }

    //Инициализация игровых полей
    private fun initializeGameFields(){
        //Инициализация игровых полей
        initializeField(1, gridFieldOne, buttonFieldOne)
        initializeField(2, gridFieldTwo, buttonFieldTwo)
    }

    //Метод преобразующий корабли в играбельные и устанавливающий их в поля
    private fun convertAndSetShips(){
        //Преоброазование кораблей в "играбельные"
        shipsOne = convert(ShipPacksStorage.playerOneShips!!)
        shipsTwo = convert(ShipPacksStorage.playerTwoShips!!)

        //Установка позиций кораблей на полях
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

    //Инициализация игрового поля для определенного игрока
    private fun initializeField(player: Int, field: GridLayout?, buttonField: Array<Array<CustomButton?>>): Boolean{
        //Инициализация кнопочного поля
        initializeButtonField(buttonField)

        //Настройка размера поля
        setupFieldSize(field)

        //Создание пустого пространства
        val nullSpace = createNullSpace()
        field?.addView(nullSpace)

        //Добавление элементов в GridLayout
        addSymbolsToGridLayout(field)

        //Добавление кнопок и элементов для двух игроков
        addButtonsAndSymbolsForPlayers(player, field, buttonField)

        return true
    }

    //Инициализация поля кнопок
    private fun initializeButtonField(buttonField: Array<Array<CustomButton?>>) {
        for (i in 0..11) {
            for (j in 0..11) {
                buttonField[i][j] = CustomButton(context!!)
            }
        }
    }

    //Установка размера поля GridLayout
    private fun setupFieldSize(field: GridLayout?) {
        field?.apply {
            rowCount = 11
            columnCount = 11
        }
    }

    //Создание пустого пространства в игровом поле
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

    //Добавление элементов в GridLayout
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

    //Добавление кнопок и символов для игроков в GridLayout
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

    //Метод для отражения состояния корабля на игровом поле
    fun reflector(ship: PlayableShip, buttonField: Array<Array<CustomButton?>>, field: Array<IntArray>): Boolean {
        val body: Drawable //Тело корабля
        val front: Drawable //Нос корабля
        val lineCount: Int //
        val columnCount: Int
        val i = ship.positions[0]!!.x //Координата x позиции корабля на поле
        val j = ship.positions[0]!!.y //Координата н позиции корабля на поле

        //Определение отображаемых частей корабля в зависимости от его ориентации (горизонтальная или вертикальная)
        if (ship.isHorizontal) {
            //Горизонтальный
            //Получение спрайтов корабля
            body = AppAssetsManager.horizontalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)!!
            front = AppAssetsManager.horizontalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)!!

            lineCount = 3
            columnCount = ship.size + 2
        }
        else {
            //Вертикальный
            //Получение спрайтов корабля
            body = AppAssetsManager.verticalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed)!!
            front = AppAssetsManager.verticalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)!!

            lineCount = ship.size + 2
            columnCount = 3
        }

        //Обновление информации на поле и кнопках для отображения корабля
        for (line in 0 until lineCount) {
            for (column in 0 until columnCount) {
                field[+line][j + column] = -1 //Пометка соответствующих клеток поля, что они заняты кораблем
                buttonField[i + line][j + column]!!.background = layout!![22]
                buttonField[i + line][j + column]!!.setPressedStatus()
            }
        }

        //Установка спрайтов уничтоженного корабля на его позиции на игровом поле
        for (position in 0 until ship.size) {
            buttonField[ship.positions[position]!!.x + 1][ship.positions[position]!!.y + 1]!!.background = body
            if (position + 1 == ship.size) {
                buttonField[ship.positions[position]!!.x + 1][ship.positions[position]!!.y + 1]!!.background = front
            }
        }
        return true
    }

    //Метод вызвается для завершения игры, и перехода на активность результатов
    private fun handleGameEnd(winner: Int){
        resultIntent = Intent(context, ResultActivity::class.java).putExtra("winner", winner)
        context!!.startActivity(resultIntent)
        AppActivityManager.getGameActivity()?.finish()
    }

    //Метод проверки соответсвия всех кнопок заданным критериям
    private fun allButtonsMatchCriteria(): Boolean {
        val allButtonsOneFilled = buttonFieldOne.flatten().all { button -> checkButtonAssets(button) }
        val allButtonsTwoFilled = buttonFieldTwo.flatten().all { button -> checkButtonAssets(button) }

        return allButtonsOneFilled || allButtonsTwoFilled
    }

    //Проверка кнопки на соответсвие нужному фону
    private fun checkButtonAssets(button: CustomButton?): Boolean {
        button ?: return false //Проверка, не является ли кнопка пустой

        val buttonBackground = button.background ?: return false //Получение фона кнопки

        return when {
            isDefaultBackground(buttonBackground) -> {
                Log.d("CheckAssets", "Button has a standard background")
                false
            }
            isDestroyedShipPart(buttonBackground) ||
                    isHit(buttonBackground) ||
                    isMiss(buttonBackground) || isRippleDrawable(buttonBackground) -> true
            else -> {
                Log.d("CheckAssets", "Button failed criteria check: $buttonBackground $button")
                false
            }
        }
    }

    //Метод проверяющий является ли фон кнопки уничтоженным кораблем
    private fun isDestroyedShipPart(drawable: Drawable): Boolean {
        return drawable == AppAssetsManager.horizontalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed) ||
                drawable == AppAssetsManager.horizontalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed) ||
                drawable == AppAssetsManager.verticalShipAssets.getBody(ShipAssetSample.shipPartType.destroyed) ||
                drawable == AppAssetsManager.verticalShipAssets.getFront(ShipAssetSample.shipPartType.destroyed)
    }

    //Метод проверяющий является ли кнопка попаданием
    private fun isHit(drawable: Drawable): Boolean {
        if (drawable is ColorDrawable) {
            val color = drawable.color
            return color == Color.RED // Проверяем, является ли цвет красным
        }
        return false
    }

    //Метод проверяющий является ли кнопка промахом
    private fun isMiss(drawable: Drawable): Boolean {
        return drawable == layout[22]
    }

    //Метод проверяющий является ли фон кнопки стандартным фоном игрового поля(вода)
    private fun isDefaultBackground(drawable: Drawable): Boolean{
        return drawable == layout[21]
    }

    //Метод проверяющий является ли кнопка попаданием(второй метод)
    private fun isRippleDrawable(drawable: Drawable): Boolean {
        return drawable is RippleDrawable
    }


    //Метод обрабатывабщий нажатие на игровое поле
    private fun handleButtonClick(isFirstPlayer: Boolean, x: Int, y :Int) {
        //Определение игрового поля, поля кораблей и кораблей для текущего игрока
        val buttonField = if (isFirstPlayer) buttonFieldOne else buttonFieldTwo //Игровое поле кнопок для текущего игрока
        val field = if (isFirstPlayer) fieldOne else fieldTwo //Поле кораблей текущего игрока
        val ships = if (isFirstPlayer) shipsOne else shipsTwo //Список кораблей текущего игрока

        val currentButton = buttonField[x + 1][y + 1] //Получение текущей кнопки по координатам x и y

        //Проверка, не нажата ли кнопка, иначе продолжить обработку
        if (!currentButton!!.isButtonPressed) {
            if (!currentButton!!.isButtonPressed) {
                var currentShip: PlayableShip? = null

                //Поиск корабля, соответствующего нажатой кнопке
                for (ship in ships!!) {
                    if (ship.id == field[x + 1][y + 1]) {
                        currentShip = ship
                        break
                    }
                }
                /*ships?.forEach{ship ->
                    if(ship.id == field[x + 1][y + 1]){
                        currentShip = ship
                        return@forEach
                    }
                }*/
                //Если корабль найден
                if (currentShip != null) {
                    currentButton.setPressedStatus() //Установка статуса нажатия для текущей кнопки
                    currentButton.setBackgroundColor(hitColor) //Установка цвета попадания на кнопке
                    currentShip.incrementDestroyedParts() //Увеличение количество уничтоженных частей корабля
                    field[x + 1][y + 1] = -1 //Отметка, что клетка поля занята уничтоженной частью корабля

                    //Если корабль полностью уничтожен, отражаем его состояние на поле
                    if (currentShip.isDestroyed) {
                        reflector(
                            currentShip,
                            if (isFirstPlayer) buttonFieldOne else buttonFieldTwo,
                            if (isFirstPlayer) fieldOne else fieldTwo
                        )
                    }
                    updateStep()//Обновляем ход текущего игрока, чтобы он мог продолжить атаковать

                    //Проверка, уничтожены ли все корабли текущего игрока
                    var n = 0
                    /*for (ship in ships) {
                        if (ship.isDestroyed) n++
                    }*/
                    ships.forEach {ship ->
                        if (ship.isDestroyed) n++
                    }

                    Log.d("Clicks", "$n")
                    //Если все корабли уничтожены, завершаем игру
                    if (n == 10) handleGameEnd(if (isFirstPlayer) 2 else 1)
                }

                //Если нажата пустая клетка
                if (currentShip == null) {
                    currentButton.setPressedStatus() // Установка статуса нажатия для текущей кнопки
                    currentButton.background = layout!![22] // Установка фона кнопки на пустую клетку
                    field[x + 1][y + 1] = -1 //Отметка, что клетка поля пустая
                    changeStep()//Смена хода игры

                    Log.d("Clicks", "${currentButton.x}: ${currentButton.y}")
                }

                //Проверка, удовлетворяют ли все кнопки заданным критериям
                if (allButtonsMatchCriteria()) handleGameEnd(if (isFirstPlayer) 2 else 1)
            }
        }
    }

    //Слушатель события тика для таймера
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

    //Слушатель нажатия на кнопку игрового поля первого игрока
    private val firstFieldListener = View.OnClickListener { v ->
        val x = v.id.toString()[1].code - 48
        val y = v.id.toString()[2].code - 48
        handleButtonClick(true, x, y)
    }

    //Слушатель нажатия на кнопку игрового поля второго игрока
    private val secondFieldListener = View.OnClickListener { v ->
        val x = v.id.toString()[1].code - 48
        val y = v.id.toString()[2].code - 48
        handleButtonClick(false, x, y)
    }
}