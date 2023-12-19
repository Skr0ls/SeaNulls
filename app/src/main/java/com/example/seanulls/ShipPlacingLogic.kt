package com.example.seanulls

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.seanulls.ShipManager.Companion.ships
import com.example.seanulls.activities.GameActivity
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.managers.AppAssetsManager
import com.example.seanulls.models.HorizontalShipAssetSample
import com.example.seanulls.models.ShipAssetSample
import com.example.seanulls.models.VerticalShipAssetSample

class ShipPlacingLogic : Activity() {

    /**
     * Класс, управляющий логикой размещения кораблей на игровой сетке
     */

    private val strChoose = "Необходимо выбрать корабль"
    private val notEnoughSpace = "Недостаточно места"

    private var viewShips: ArrayList<ImageView?>? = null

    private lateinit var buttonField: Array<Array<Button?>>
    private val numberField = Array(12) { IntArray(12) }

    private var selectedShipToPlace: Ship? = null
    private var selectedShipToEdit: Ship? = null

    private var manager: ShipManager? = null

    private var buttonRemove: Button? = null
    private var buttonTurn: Button? = null
    private var buttonNext: Button? = null
    private var buttonBack: Button? = null
    private var textViewPlayerNumber: TextView? = null

    private var isFirstPlayer = true

    private var verticalShipAssetSample: VerticalShipAssetSample? = null
    private var horizontalShipAssetSample: HorizontalShipAssetSample? = null

    companion object {
        private var context: Context? = null
    }

    // Метод инициализации всех элементов интерфейса
    fun fullInitialization(_viewShips: ArrayList<ImageView?>, _context: Context, _textView_playerNumber: TextView, btRemove: Button, btTurn: Button, btNext: Button, btBack: Button) {
        initializeButtons(_context, btRemove, btTurn, btNext, btBack)
        initializeTextView(_textView_playerNumber)
        initializeViewShips(_viewShips)
        initializeButtonField()

        // Инициализация образцов изображений для горизонтальных и вертикальных кораблей
        horizontalShipAssetSample = AppAssetsManager.horizontalShipAssets
        verticalShipAssetSample = AppAssetsManager.verticalShipAssets
    }

    // Инициализация изображений кораблей
    private fun initializeViewShips(_viewShips: ArrayList<ImageView?>) {
        viewShips = _viewShips
        manager!!.initializeShips(viewShips!!)

        val unselectedShipOne = AppAssetsManager.horizontalShipAssets!!.getOnePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipTwo = AppAssetsManager.horizontalShipAssets!!.getTwoPartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipThree = AppAssetsManager.horizontalShipAssets!!.getThreePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipFour = AppAssetsManager.horizontalShipAssets!!.getFourPartShip(ShipAssetSample.shipType.unselected)

        for (ship in ships!!) {
            ship.viewShip.setOnClickListener(viewShipListener)
            if (ships!!.indexOf(ship) <= 3) ship.viewShip.background =
                unselectedShipOne else if (ships!!.indexOf(ship) <= 6) ship.viewShip.background =
                unselectedShipTwo else if (ships!!.indexOf(ship) <= 8) ship.viewShip.background =
                unselectedShipThree else ship.viewShip.background = unselectedShipFour
            ship.setDefaultBackground(ship.viewShip.background.current)
        }
    }

    // Инициализация надписей
    private fun initializeTextView(_textViewPlayerNumber: TextView) {
        textViewPlayerNumber = _textViewPlayerNumber
    }

    // Инициализация кнопок
    private fun initializeButtons(_context: Context, btRemove: Button, btTurn: Button, btNext: Button, btBack: Button){
        context = _context
        manager = ShipManager()
        buttonRemove = btRemove
        buttonTurn = btTurn
        buttonNext = btNext
        buttonBack = btBack
        buttonRemove!!.setOnClickListener(onClickRemove)
        buttonTurn!!.setOnClickListener(onClickTurn)
        buttonNext!!.setOnClickListener(onClickNext)
        buttonBack!!.setOnClickListener(onClickBack)
        buttonRemove!!.isEnabled = false
        buttonTurn!!.isEnabled = false
        buttonNext!!.isEnabled = false
    }

    // Инициализация кнопок игрового поля
    private fun initializeButtonField() {
        buttonField = ShipPlacingLayoutManager.getButtonFields()
        for (i in 0 until ShipPlacingLayoutManager.CELL_COUNT) for (j in 0 until ShipPlacingLayoutManager.CELL_COUNT) buttonField[i][j]?.setOnClickListener(
            buttonCellListener)
    }

    // Проверяет, имеется достаточно места на игровой сетке для размещения корабля по указанным координатам
    private fun checkAreaForPlacement(line: Int, column: Int): Boolean { //Проверка на поле 12*12, line и column получаются как с поля 10*10
        try {
            // Проверяем, не выходит ли корабль за пределы игрового поля
            if (column + selectedShipToPlace!!.size > 10) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }
            // Создаем область для размещения корабля на игровой сетке
            val lineCount = 3
            val columnCount = selectedShipToPlace!!.size + 2
            val area = Array(lineCount) { IntArray(columnCount) }

            // Проверяем, свободна ли область для размещения корабля на игровой сетке
            for (i in 0 until lineCount) {
                for (j in 0 until columnCount) {
                    area[i][j] = numberField[line + i][column + j]
                    if (area[i][j] != 0) {
                        Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }

            // Если область свободна, размещаем корабль на игровой сетке
            shipPlacer(line, column)
        } catch (e: NullPointerException) {
            Toast.makeText(context, strChoose, Toast.LENGTH_SHORT).show()
        }
        return true
    }

    // Метод для размещения корабля на игровой сетке по заданным координатам
    private fun shipPlacer(line: Int, column: Int): Boolean {
        try {
            // Размещение корабля на игровой сетке
            selectedShipToPlace!!.placeShip(Point(line, column))

            // Определение позиций корабля на игровой сетке и установка их в соответствующие клетки поля
            for (j in 0 until selectedShipToPlace!!.size) {
                numberField[line + 1][column + 1 + j] = selectedShipToPlace!!.id
            }

            // Отображение корабля на игровой сетке
            shipReflector(selectedShipToPlace)
            selectedShipToPlace!!.setViewShipDefaultBackground()
            selectedShipToPlace = null
        } catch (e: Exception) {
            Toast.makeText(context, "ОШИБКА", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    // Отображает размещенный корабль на игровой сетке
    private fun shipReflector(currentShip: Ship?): Boolean {
        val body: Drawable
        val front: Drawable

        // Определяем изображения для тела и носа корабля в зависимости от его ориентации
        if (currentShip!!.isHorizontal) {
            body = horizontalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = horizontalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }
        else {
            body = verticalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = verticalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }

        // Устанавливаем изображения на игровую сетку для каждой части корабля
        for (position in 0 until currentShip.size) {
            buttonField[currentShip.positions[position]!!.x][currentShip.positions[position]!!.y]?.background = body
            if (position + 1 == currentShip.size) buttonField[currentShip.positions[position]!!.x][currentShip.positions[position]!!.y]?.background = front
        }
        return true
    }

    // Выделяет выбранный корабль на игровой сетке
    private fun shipSelector(currentShipToEdit: Ship?): Boolean {
        val body: Drawable
        val front: Drawable

        // Определяем изображения для выделения тела и носа корабля в зависимости от его ориентации
        if (currentShipToEdit!!.isHorizontal) {
            body = horizontalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.selected)
            front = horizontalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.selected)
        }
        else {
            body = verticalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.selected)
            front = verticalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.selected)
        }

        // Устанавливаем изображения на игровую сетку для каждой части выбранного корабля
        for (position in 0 until currentShipToEdit.size) {
            buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = body
            if (position + 1 == currentShipToEdit.size) buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = front
        }
        return true
    }

    // Снимает выделение с выбранного корабля на игровой сетке
    private fun shipDeselector(currentShipToEdit: Ship): Boolean {
        val body: Drawable
        val front: Drawable

        // Определяем изображения для корпуса и носа корабля в зависимости от его ориентации
        if (currentShipToEdit.isHorizontal) {
            body = horizontalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = horizontalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }
        else {
            body = verticalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = verticalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }

        // Устанавливаем изображения корпуса и носа на игровую сетку для каждой части корабля
        for (position in 0 until currentShipToEdit.size) {
            buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = body
            if (position + 1 == currentShipToEdit.size) buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = front
        }
        return true
    }

    // Удаляет изображение корабля из определенных клеток на игровой сетке
    private fun shipSpriteRemover(): Boolean {
        for (i in 0 until selectedShipToEdit!!.size) {
            buttonField[selectedShipToEdit!!.positions[i]!!.x][selectedShipToEdit!!.positions[i]!!.y]?.background = AppAssetsManager.layoutSprites!![21]
        }
        return true
    }

    private fun shipNumberRemover(): Boolean {
        for (i in 0 until selectedShipToEdit!!.size) numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] = 0
        return true
    }

    // Удаляет выбранный корабль с игровой сетки и сбрасывает его параметры
    private fun shipRemover(): Boolean {
        buttonRemove!!.isEnabled = false
        buttonTurn!!.isEnabled = false
        shipNumberRemover() // Удаляет численные обозначения корабля с игровой сетки
        shipSpriteRemover() // Удаляет графическое отображение корабля с игровой сетки
        selectedShipToEdit!!.removeShip() // Удаляет корабль
        selectedShipToEdit = null // Сбрасывает выбранный корабль
        return false
    }

    // Поворачивает выбранный корабль на игровой сетке
    private fun shipTurner(): Boolean {
        if (selectedShipToEdit == null) return false // Если корабль не выбран, возвращает false

        // Если корабль горизонтально размещен
        if (selectedShipToEdit!!.isHorizontal) {
            // Проверяет, есть ли достаточно места для поворота корабля
            if (selectedShipToEdit!!.positions[0]!!.x + selectedShipToEdit!!.size > 10) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }

            // Подготовка области для проверки на возможность поворота корабля
            val line = selectedShipToEdit!!.positions[0]!!.x
            val column = selectedShipToEdit!!.positions[0]!!.y
            val lineCount = selectedShipToEdit!!.size + 2
            val columnCount = 3
            val area = Array(lineCount) { IntArray(columnCount) }
            var countOfNotNullPoints = 0

            // Проверка области на пустоту и подсчет занятых точек
            for (i in 0 until lineCount) {
                for (j in 0 until columnCount) {
                    if (i == 0) area[i][j] = numberField[line + i][column + j]
                    if (i == 1) area[i][j] = numberField[line + i][column + j]
                    if (i == 2) area[i][j] = numberField[line + i][column + j]
                    if (i == 3) area[i][j] = numberField[line + i][column + j]
                    if (i == 4) area[i][j] = numberField[line + i][column + j]
                    if (i == 5) area[i][j] = numberField[line + i][column + j]
                    if (area[i][j] != 0 && area[i][j] != selectedShipToEdit!!.id) countOfNotNullPoints++
                }
            }
            // Если обнаружены занятые точки в области, выводит сообщение об ошибке
            if (countOfNotNullPoints != 0) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }
            // Устанавливает новую ориентацию корабля
            selectedShipToEdit!!.setOrientation(Ship.VERTICAL)
            val poses = arrayOfNulls<Point>(selectedShipToEdit!!.size)

            // Удаляет старое расположение корабля
            shipSpriteRemover()
            for (i in 0 until selectedShipToEdit!!.size) { //Очищаем старое расположение
                numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] =
                    0
                poses[i] = Point(
                    selectedShipToEdit!!.positions[i]!!.x + i,
                    selectedShipToEdit!!.positions[i]!!.y - i
                )
                selectedShipToEdit!!.positions[i] = Ship.DEFAULT_POSITION
            }

            // Устанавливает новое расположение корабля и его значения на игровой сетке
            selectedShipToEdit!!.positions = poses //Указываем новое расположение
            for (i in 0 until selectedShipToEdit!!.size) { //Устанавливаем значения в массив-поле
                numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] =
                    selectedShipToEdit!!.id
            }

            // Отображает повернутый корабль
            shipSelector(selectedShipToEdit)
        }
        else if (!selectedShipToEdit!!.isHorizontal) {
            if (selectedShipToEdit!!.positions[0]!!.y + selectedShipToEdit!!.size > 10) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }

            val line = selectedShipToEdit!!.positions[0]!!.x
            val column = selectedShipToEdit!!.positions[0]!!.y
            val lineCount = 3
            val columnCount = selectedShipToEdit!!.size + 2
            val area = Array(lineCount) { IntArray(columnCount) }
            var countOfNotNullPoints = 0

            for (i in 0 until lineCount) {
                for (j in 0 until columnCount) {
                    if (i == 0) area[i][j] = numberField[line + i][column + j]
                    if (i == 1) area[i][j] = numberField[line + i][column + j]
                    if (i == 2) area[i][j] = numberField[line + i][column + j]
                    if (area[i][j] != 0 && area[i][j] != selectedShipToEdit!!.id) countOfNotNullPoints++
                }
            }

            if (countOfNotNullPoints != 0) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }

            selectedShipToEdit!!.setOrientation(Ship.HORIZONTAL)
            val poses = arrayOfNulls<Point>(
                selectedShipToEdit!!.size
            )

            shipSpriteRemover()

            for (i in 0 until selectedShipToEdit!!.size) {
                numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] =
                    0
                poses[i] = Point(
                    selectedShipToEdit!!.positions[i]!!.x - i,
                    selectedShipToEdit!!.positions[i]!!.y + i
                )
                selectedShipToEdit!!.positions[i] = Ship.DEFAULT_POSITION
            }

            selectedShipToEdit!!.positions = poses

            for (i in 0 until selectedShipToEdit!!.size) {
                numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] =
                    selectedShipToEdit!!.id
            }

            val item = selectedShipToPlace

            selectedShipToPlace = selectedShipToEdit
            shipReflector(selectedShipToPlace)
            selectedShipToPlace = item
            shipSelector(selectedShipToEdit)
        }
        return true
    }


    // Очищает игровое поле и сбрасывает его для новой игры
    private fun layoutCleaner(): Boolean {
        // Очищает игровое поле и скрывает все корабли на нем
        for (i in 0..11) {
            for (j in 0..11) {
                numberField[i][j] = 0 // Обнуляет каждую ячейку поля
                if (i < 10 && j < 10) buttonField[i][j]?.background = AppAssetsManager.layoutSprites!![21]
            }
        }
        // Восстанавливает видимость и доступность кнопок-кораблей
        for (ship in ships!!) {
            ship.viewShip.visibility = View.VISIBLE // Устанавливает видимость корабля
            ship.viewShip.isEnabled = true // Включает доступность корабля для выбора
        }
        return true
    }



    private val buttonCellListener = View.OnClickListener { v ->
        var isOccupied = false
        val i = v.id.toString().toInt() / 10
        val j = v.id.toString().toInt() % 10
        var reselect = 0
        for (ship in ships!!) {
            for (position in ship.positions) {
                if (position == Point(i, j)) isOccupied = true
            }
        }
        if (!isOccupied && selectedShipToPlace != null) {
            checkAreaForPlacement(i, j)
        }
        else if (!isOccupied && selectedShipToPlace == null) {
            Toast.makeText(context, strChoose, Toast.LENGTH_SHORT).show()
        }
        else if (isOccupied && selectedShipToEdit == null) {
            for (ship in ships!!) {
                for (n in 0 until ship.size) {
                    if (ship.positions[n] == Point(i, j)) {
                        selectedShipToEdit = ship
                        buttonRemove!!.isEnabled = true
                        buttonTurn!!.isEnabled = true
                        shipSelector(selectedShipToEdit)
                        break
                    }
                }
            }
        }
        else if (isOccupied && selectedShipToEdit != null) {
            for (n in 0 until selectedShipToEdit!!.size) {
                if (v.id == selectedShipToEdit!!.positions[n]!!.x * 10 + selectedShipToEdit!!.positions[n]!!.y) {
                    shipDeselector(selectedShipToEdit!!)
                    selectedShipToEdit = null
                    buttonRemove!!.isEnabled = false
                    buttonTurn!!.isEnabled = false
                    break
                }
                else {
                    reselect++
                    if (reselect == selectedShipToEdit!!.size) {
                        shipDeselector(selectedShipToEdit!!)
                        for (ship in ships!!) {
                            for (m in 0 until ship.size) {
                                if (v.id == ship.positions[m]!!.x * 10 + ship.positions[m]!!.y) {
                                    selectedShipToEdit = ship
                                    break
                                }
                            }
                        }
                        shipSelector(selectedShipToEdit)
                        break
                    }
                }
            }
        }

        var countOfPlacedShips = 0
        for (ship in ships!!) {
            if (ship.isPlaced) {
                countOfPlacedShips++
            }
        }

        if (countOfPlacedShips == 10) buttonNext!!.isEnabled = true else buttonNext!!.isEnabled = false
    }

    private val onClickRemove = View.OnClickListener {
        shipRemover()
        buttonNext!!.isEnabled = false
    }

    private val onClickTurn = View.OnClickListener { shipTurner() }

    private val onClickBack = View.OnClickListener {
        if (isFirstPlayer) AppActivityManager.getPlacingActivity()?.finish()
        else if (!isFirstPlayer) {
            layoutCleaner()
            buttonNext!!.isEnabled = true
            isFirstPlayer = true
            selectedShipToEdit = null
            selectedShipToPlace = null
            ShipManager.loadShipPack(ShipPacksStorage.playerOneShips)
            val ships = ships
            for (ship in ships!!) {
                val positions = ship.positions
                for (pos in positions) numberField[pos!!.x + 1][pos.y + 1] = ship.id
                shipReflector(ship)
                ship.viewShip.visibility = View.INVISIBLE
            }
            textViewPlayerNumber!!.text = "Первый игрок"
        }
    }
    private val onClickNext = View.OnClickListener {
        if (isFirstPlayer) {
            ShipPacksStorage.playerOneShips = ships
            manager!!.initializeShips(viewShips!!)
            layoutCleaner()
            buttonNext!!.isEnabled = false
            isFirstPlayer = false
            selectedShipToEdit = null
            selectedShipToPlace = null
            textViewPlayerNumber!!.text = "Второй игрок"
        }
        else {
            ShipPacksStorage.playerTwoShips = ships
            val intent = Intent(context, GameActivity::class.java)
            context!!.startActivity(intent)
            AppActivityManager.getPlacingActivity()?.finish()
        }
    }
    private val viewShipListener = View.OnClickListener { view ->
        val unselectedShipOne = horizontalShipAssetSample!!.getOnePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipTwo = horizontalShipAssetSample!!.getTwoPartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipThree = horizontalShipAssetSample!!.getThreePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipFour = horizontalShipAssetSample!!.getFourPartShip(ShipAssetSample.shipType.unselected)

        for (ship in ships!!) {
            if (ships!!.indexOf(ship) <= 3) ship.viewShip.background =
                unselectedShipOne else if (ships!!.indexOf(ship) <= 6) ship.viewShip.background =
                unselectedShipTwo else if (ships!!.indexOf(ship) <= 8) ship.viewShip.background =
                unselectedShipThree else ship.viewShip.background = unselectedShipFour
        }

        val selectedShipOne = horizontalShipAssetSample!!.getOnePartShip(ShipAssetSample.shipType.selected)
        val selectedShipTwo = horizontalShipAssetSample!!.getTwoPartShip(ShipAssetSample.shipType.selected)
        val selectedShipThree = horizontalShipAssetSample!!.getThreePartShip(ShipAssetSample.shipType.selected)
        val selectedShipFour = horizontalShipAssetSample!!.getFourPartShip(ShipAssetSample.shipType.selected)

        for (ship in ships!!) {
            if (view.id == ship.viewShip.id && selectedShipToPlace != ship) {
                selectedShipToPlace = ship
                val currentShipIndex = ships!!.indexOf(ship)
                if (currentShipIndex <= 3) ship.viewShip.background =
                    selectedShipOne else if (currentShipIndex <= 6) ship.viewShip.background =
                    selectedShipTwo else if (currentShipIndex <= 8) ship.viewShip.background =
                    selectedShipThree else ship.viewShip.background = selectedShipFour
                break
            }
            else if (view.id == ship.viewShip.id && selectedShipToPlace == ship) {
                selectedShipToPlace = null
                break
            }
        }
    }
}