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

/**
 * Класс ShipPlacingLogic управляет логикой размещения кораблей на игровой сетке
 */
class ShipPlacingLogic : Activity() {
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

    //Метод отвечающий за инициализацию всех элементов интерфейса
    fun fullInitialization(_viewShips: ArrayList<ImageView?>, _context: Context, _textViewPlayerNumber: TextView, btRemove: Button, btTurn: Button, btNext: Button, btBack: Button) {
        initializeButtons(_context, btRemove, btTurn, btNext, btBack)
        initializeTextView(_textViewPlayerNumber)
        initializeViewShips(_viewShips)
        initializeButtonField()

        //Инициализация образцов изображений для горизонтальных и вертикальных кораблей
        horizontalShipAssetSample = AppAssetsManager.horizontalShipAssets
        verticalShipAssetSample = AppAssetsManager.verticalShipAssets
    }

    //Метод отвечающий за инициализацию изображений кораблей
    private fun initializeViewShips(_viewShips: ArrayList<ImageView?>){
        viewShips = _viewShips
        manager!!.initializeShips(viewShips!!)

        val array = initializeImagesOfShips()
        setImagesForShips(array)
    }

    //Метод инициализации изображений кораблей для использования при размещении на поле
    private fun initializeImagesOfShips(): Array<Drawable>{
        val unselectedShipOne = AppAssetsManager.horizontalShipAssets!!.getOnePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipTwo = AppAssetsManager.horizontalShipAssets!!.getTwoPartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipThree = AppAssetsManager.horizontalShipAssets!!.getThreePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipFour = AppAssetsManager.horizontalShipAssets!!.getFourPartShip(ShipAssetSample.shipType.unselected)

        val array = arrayOf(unselectedShipOne, unselectedShipTwo, unselectedShipThree, unselectedShipFour)
        return array
    }


    //Метод установки изображений для кораблей на основе типа и размера
    private fun setImagesForShips(arrayOfShips: Array<Drawable>){
        for (ship in ships!!) {
            ship.viewShip.setOnClickListener(viewShipListener)
            val index = ships!!.indexOf(ship)
            val background = when {
                index <= 3 -> arrayOfShips[0]
                index <= 6 -> arrayOfShips[1]
                index <= 8 -> arrayOfShips[2]
                else -> arrayOfShips[3]
            }
            ship.viewShip.background = background
            ship.setDefaultBackground(background.current)
        }
    }

    //Метод отвечающий за инициализацию надписи обозначающей номер игрока
    private fun initializeTextView(_textViewPlayerNumber: TextView) {
        textViewPlayerNumber = _textViewPlayerNumber
    }

    //Метод отвечающий за инициализацию кнопок
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

    //Метод отвечающий за инициализацию кнопок игрового поля
    private fun initializeButtonField() {
        buttonField = ShipPlacingLayoutManager.getButtonFields()
        for (i in 0 until ShipPlacingLayoutManager.CELL_COUNT) for (j in 0 until ShipPlacingLayoutManager.CELL_COUNT) buttonField[i][j]?.setOnClickListener(
            buttonCellListener)
    }

    //Метод отвечает за проверку, имеется ли достаточно места на игровой сетке для размещения корабля по указанным координатам
    private fun checkAreaForPlacement(line: Int, column: Int): Boolean { //Проверка на поле 12*12, line и column получаются как с поля 10*10
        try {
            //Проверяем, не выходит ли корабль за пределы игрового поля
            if (column + selectedShipToPlace!!.size > 10) {
                Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                return false
            }
            //Создаем область для размещения корабля на игровой сетке
            val lineCount = 3
            val columnCount = selectedShipToPlace!!.size + 2
            val area = Array(lineCount) { IntArray(columnCount) }

            //Проверяем, свободна ли область для размещения корабля на игровой сетке
            for (i in 0 until lineCount) {
                for (j in 0 until columnCount) {
                    area[i][j] = numberField[line + i][column + j]
                    if (area[i][j] != 0) {
                        Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }

            //Если область свободна, размещаем корабль на игровой сетке
            shipPlacer(line, column)
        } catch (e: NullPointerException) {
            Toast.makeText(context, strChoose, Toast.LENGTH_SHORT).show()
        }
        return true
    }

    //Метод отвечающий за размещения корабля на игровой сетке по заданным координатам
    private fun shipPlacer(line: Int, column: Int): Boolean {
        try {
            //Размещение корабля на игровой сетке
            selectedShipToPlace!!.placeShip(Point(line, column))

            //Определение позиций корабля на игровой сетке и установка их в соответствующие клетки поля
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

    //Метод отображающий размещенный корабль на игровой сетке
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

    //Метод выделяет выбранный корабль на игровой сетке
    private fun shipSelector(currentShipToEdit: Ship?): Boolean {
        val body: Drawable
        val front: Drawable

        //Определяем изображения для выделения тела и носа корабля в зависимости от его ориентации
        if (currentShipToEdit!!.isHorizontal) {
            body = horizontalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.selected)
            front = horizontalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.selected)
        }
        else {
            body = verticalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.selected)
            front = verticalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.selected)
        }

        //Устанавливаем изображения на игровую сетку для каждой части выбранного корабля
        for (position in 0 until currentShipToEdit.size) {
            buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = body
            if (position + 1 == currentShipToEdit.size) buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = front
        }
        return true
    }

    //Метод снимает выделение с выбранного корабля на игровой сетке
    private fun shipDeselector(currentShipToEdit: Ship): Boolean {
        val body: Drawable
        val front: Drawable

        //Определяем изображения для корпуса и носа корабля в зависимости от его ориентации
        if (currentShipToEdit.isHorizontal) {
            body = horizontalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = horizontalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }
        else {
            body = verticalShipAssetSample!!.getBody(ShipAssetSample.shipPartType.common)
            front = verticalShipAssetSample!!.getFront(ShipAssetSample.shipPartType.common)
        }

        //Устанавливаем изображения корпуса и носа на игровую сетку для каждой части корабля
        for (position in 0 until currentShipToEdit.size) {
            buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = body
            if (position + 1 == currentShipToEdit.size) buttonField[currentShipToEdit.positions[position]!!.x][currentShipToEdit.positions[position]!!.y]?.background = front
        }
        return true
    }

    //Метод удаляет изображение корабля из определенных клеток на игровой сетке
    private fun shipSpriteRemover(): Boolean {
        for (i in 0 until selectedShipToEdit!!.size) {
            buttonField[selectedShipToEdit!!.positions[i]!!.x][selectedShipToEdit!!.positions[i]!!.y]?.background = AppAssetsManager.layoutSprites!![21]
        }
        return true
    }

    //Метод удаляет корабль из определенных клеток на игровой сетке
    private fun shipNumberRemover(): Boolean {
        for (i in 0 until selectedShipToEdit!!.size) numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] = 0
        return true
    }

    //Метод удаляет выбранный корабль с игровой сетки и сбрасывает его параметры
    private fun shipRemover(): Boolean {
        buttonRemove!!.isEnabled = false
        buttonTurn!!.isEnabled = false
        shipNumberRemover() //Удаляет численные обозначения корабля с игровой сетки
        shipSpriteRemover() //Удаляет графическое отображение корабля с игровой сетки
        selectedShipToEdit!!.removeShip() //Удаляет корабль
        selectedShipToEdit = null //Сбрасывает выбранный корабль
        return false
    }

    //Метод поворачивает выбранный корабль на игровой сетке
    private fun shipTurner(): Boolean{
        if (selectedShipToEdit == null) return false

        if (selectedShipToEdit!!.isHorizontal) {
            if (!canRotateHorizontally(selectedShipToEdit!!)) return false
            rotateHorizontally(selectedShipToEdit!!)
        }
        else {
            if (!canRotateVertically(selectedShipToEdit!!)) return false
            rotateVertically(selectedShipToEdit!!)
        }

        return true
    }

    //Метод проверяет возможность повернуть выбранный корабль горизонтально
    private fun canRotateHorizontally(ship: Ship): Boolean{
        //Проверяет, есть ли достаточно места для поворота корабля
        if (ship!!.positions[0]!!.x + ship!!.size > 10) {
            Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
            return false
        }

        //Подготовка области для проверки на возможность поворота корабля
        val line = ship!!.positions[0]!!.x
        val column = ship!!.positions[0]!!.y
        val lineCount = ship!!.size + 2
        val columnCount = 3
        val area = Array(lineCount) { IntArray(columnCount) }
        var countOfNotNullPoints = 0

        //Проверка области на пустоту и подсчет занятых точек
        for (i in 0 until lineCount) {
            for (j in 0 until columnCount) {
                if (i == 0) area[i][j] = numberField[line + i][column + j]
                if (i == 1) area[i][j] = numberField[line + i][column + j]
                if (i == 2) area[i][j] = numberField[line + i][column + j]
                if (i == 3) area[i][j] = numberField[line + i][column + j]
                if (i == 4) area[i][j] = numberField[line + i][column + j]
                if (i == 5) area[i][j] = numberField[line + i][column + j]
                if (area[i][j] != 0 && area[i][j] != ship!!.id) countOfNotNullPoints++
            }
        }
        //Если обнаружены занятые точки в области, выводит сообщение об ошибке
        if (countOfNotNullPoints != 0) {
            Toast.makeText(context, notEnoughSpace, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Метод поворачивает выбранный корабль горизонтально
    private fun rotateHorizontally(ship: Ship){
        //Устанавливает новую ориентацию корабля
        ship!!.setOrientation(Ship.VERTICAL)
        val poses = arrayOfNulls<Point>(ship!!.size)

        //Удаляет старое расположение корабля
        shipSpriteRemover()
        for (i in 0 until ship!!.size) { //Очищаем старое расположение
            numberField[ship!!.positions[i]!!.x + 1][ship!!.positions[i]!!.y + 1] =
                0
            poses[i] = Point(
                ship!!.positions[i]!!.x + i,
                ship!!.positions[i]!!.y - i
            )
            ship!!.positions[i] = Ship.DEFAULT_POSITION
        }

        //Устанавливает новое расположение корабля и его значения на игровой сетке
        ship!!.positions = poses //Указываем новое расположение
        for (i in 0 until ship!!.size) { //Устанавливаем значения в массив-поле
            numberField[ship!!.positions[i]!!.x + 1][ship!!.positions[i]!!.y + 1] = ship!!.id
        }

        //Отображает повернутый корабль
        shipSelector(ship)
    }

    //Метод проверяет возможность повернуть выбранный корабль вертикально
    private fun canRotateVertically(ship: Ship): Boolean{
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

        return true
    }

    //Метод поворачивает выбранный корабль вертикально
    private fun rotateVertically(ship: Ship){
        selectedShipToEdit!!.setOrientation(Ship.HORIZONTAL)
        val poses = arrayOfNulls<Point>(
            selectedShipToEdit!!.size
        )

        shipSpriteRemover()

        for (i in 0 until selectedShipToEdit!!.size) {
            numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] = 0
            poses[i] = Point(selectedShipToEdit!!.positions[i]!!.x - i, selectedShipToEdit!!.positions[i]!!.y + i)
            selectedShipToEdit!!.positions[i] = Ship.DEFAULT_POSITION
        }

        selectedShipToEdit!!.positions = poses

        for (i in 0 until selectedShipToEdit!!.size) {
            numberField[selectedShipToEdit!!.positions[i]!!.x + 1][selectedShipToEdit!!.positions[i]!!.y + 1] = selectedShipToEdit!!.id
        }

        val item = selectedShipToPlace

        selectedShipToPlace = selectedShipToEdit
        shipReflector(selectedShipToPlace)
        selectedShipToPlace = item
        shipSelector(selectedShipToEdit)
    }

    //Метод очищает игровое поле и сбрасывает его для новой игры
    private fun layoutCleaner(): Boolean {
        //Очищает игровое поле и скрывает все корабли на нем
        for (i in 0..11) {
            for (j in 0..11) {
                numberField[i][j] = 0 //Обнуляет каждую ячейку поля
                if (i < 10 && j < 10) buttonField[i][j]?.background = AppAssetsManager.layoutSprites!![21]
            }
        }
        //Восстанавливает видимость и доступность кнопок-кораблей
        ships?.forEach { ship ->
            ship.viewShip.apply {
                visibility = View.VISIBLE
                isEnabled = true
            }
        }
        return true
    }

    //Метод проверяющий занята ли данная ячейка
    private fun isCellOccupied(i: Int, j: Int): Boolean{
        ships?.forEach{ship ->
            ship.positions.forEach { position ->
                if(position == Point(i, j)) return true
            }
        }
        return false
    }

    //Метод обрабатывает нажатие на занятую ячейку, когда не выбран ни один корабль для редактирования
    private fun handleOccupiedCellWithoutSelectedShip(i: Int, j: Int){
        ships?.forEach { ship ->
            for(n in 0 until ship.size){
                if(ship.positions[n] == Point(i, j)){
                    selectedShipToEdit = ship
                    buttonRemove!!.isEnabled = true
                    buttonTurn!!.isEnabled = true
                    shipSelector(selectedShipToEdit)
                    return
                }
            }
        }
    }

    //Метод обрабатывает нажатие на занятую ячейку, когда выбран корабль для редактирования
    private fun handleOccupiedCellWithSelectedShip(i: Int, j: Int, v: View){
        var reselect = 0

        for(n in 0 until selectedShipToEdit!!.size){
            if(v.id == selectedShipToEdit!!.positions[n]!!.x * 10 + selectedShipToEdit!!.positions[n]!!.y){
                shipDeselector(selectedShipToEdit!!)
                selectedShipToEdit = null
                buttonRemove!!.isEnabled = false
                buttonTurn!!.isEnabled = false
                return
            }
            else{
                reselect++
                if(reselect == selectedShipToEdit!!.size){
                    shipDeselector(selectedShipToEdit!!)
                    ships?.forEach{ship ->
                        for(m in 0 until ship.size){
                            if(v.id == ship.positions[m]!!.x * 10 + ship.positions[m]!!.y){
                                selectedShipToEdit = ship
                                shipSelector(selectedShipToEdit)
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    //Слушатель обрабатывает нажатия на кнопки ячеек поля
    private val buttonCellListener = View.OnClickListener { v ->
        val i = v.id.toString().toInt() / 10
        val j = v.id.toString().toInt() % 10

        val isOccupied = isCellOccupied(i, j)

        when {
            !isOccupied && selectedShipToPlace != null -> checkAreaForPlacement(i, j)
            !isOccupied && selectedShipToPlace == null -> Toast.makeText(context, strChoose, Toast.LENGTH_SHORT).show()
            isOccupied && selectedShipToEdit == null -> handleOccupiedCellWithoutSelectedShip(i, j)
            isOccupied && selectedShipToEdit != null -> handleOccupiedCellWithSelectedShip(i, j, v)
        }

        buttonNext?.isEnabled = ships?.count { it.isPlaced } == 10
    }

    /*private val buttonCellListener = View.OnClickListener { v ->
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
    }*/

    //Слушатель обрабатывабщий нажатие на кнопку удаления корабля с поля
    private val onClickRemove = View.OnClickListener {
        shipRemover()
        buttonNext!!.isEnabled = false
    }

    //Слушатель обрабатывабщий нажатие на кнопку поворота корабля на поле
    private val onClickTurn = View.OnClickListener { shipTurner() }

    //Слушатель обрабатывабщий нажатие на кнопку назад
    private val onClickBack = View.OnClickListener {
        if (isFirstPlayer) {
            AppActivityManager.getPlacingActivity()?.finish()
        } else {
            layoutCleaner()
            buttonNext!!.isEnabled = true
            isFirstPlayer = true
            selectedShipToEdit = null
            selectedShipToPlace = null
            ShipManager.loadShipPack(ShipPacksStorage.playerOneShips)

            for (ship in ships!!) {
                val positions = ship.positions
                for (pos in positions) numberField[pos!!.x + 1][pos.y + 1] = ship.id
                shipReflector(ship)
                ship.viewShip.visibility = View.INVISIBLE
            }
            textViewPlayerNumber!!.text = "Первый игрок"
        }
    }

    //Метод устанавливает второго игрока, сохраняет его корабли и запускает активность игры для начала игры.
    private fun setSecondPlayer() {
        ShipPacksStorage.playerTwoShips = ships
        val intent = Intent(context, GameActivity::class.java)
        context?.startActivity(intent)
        AppActivityManager.getPlacingActivity()?.finish()
    }

    //Слушатель обрабатывающий нажатие на кнопку далее, устанавливает корабли для первого игрока и готовит интерфейс для второго
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
            setSecondPlayer()
        }
    }

    //Метод отвечающий за отображение кораблей в неактивном состоянии
    private fun unselectedShipListener(){
        val unselectedShipOne = horizontalShipAssetSample!!.getOnePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipTwo = horizontalShipAssetSample!!.getTwoPartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipThree = horizontalShipAssetSample!!.getThreePartShip(ShipAssetSample.shipType.unselected)
        val unselectedShipFour = horizontalShipAssetSample!!.getFourPartShip(ShipAssetSample.shipType.unselected)

        //Установка изображений неактивных кораблей для каждого корабля на поле
        ships?.forEach { ship ->
            val shipIndex = ships?.indexOf(ship)
            if(shipIndex != null){
                when{
                    shipIndex <= 3 -> ship.viewShip.background = unselectedShipOne
                    shipIndex <= 6 -> ship.viewShip.background = unselectedShipTwo
                    shipIndex <= 8 -> ship.viewShip.background = unselectedShipThree
                    else -> ship.viewShip.background = unselectedShipFour
                }
            }
        }
    }

    //Метод отвечающий за отображение выбранного корабля
    private fun selectedShipListener(view: View){
        //Получение изображений выбранных кораблей разной длины
        val selectedShipOne = horizontalShipAssetSample!!.getOnePartShip(ShipAssetSample.shipType.selected)
        val selectedShipTwo = horizontalShipAssetSample!!.getTwoPartShip(ShipAssetSample.shipType.selected)
        val selectedShipThree = horizontalShipAssetSample!!.getThreePartShip(ShipAssetSample.shipType.selected)
        val selectedShipFour = horizontalShipAssetSample!!.getFourPartShip(ShipAssetSample.shipType.selected)

        //Установка изображений выбранных кораблей в зависимости от выбора игрока
        ships?.forEach { ship ->
            if(view.id == ship.viewShip.id){
                when{
                    selectedShipToPlace != ship ->{
                        selectedShipToPlace = ship
                        val currentShipIndex = ships!!.indexOf(ship)
                        when{
                            currentShipIndex <= 3 -> ship.viewShip.background = selectedShipOne
                            currentShipIndex <= 6 -> ship.viewShip.background = selectedShipTwo
                            currentShipIndex <= 8 -> ship.viewShip.background = selectedShipThree
                            else -> ship.viewShip.background = selectedShipFour
                        }
                        return@forEach
                    }
                    selectedShipToPlace == ship ->{
                        selectedShipToPlace = null
                        return@forEach
                    }
                }
            }
        }
    }

    //Слушатель обрабатывающий нажатия на представления кораблей на экране
    private val viewShipListener = View.OnClickListener { view ->
        unselectedShipListener()
        selectedShipListener(view)
    }
}