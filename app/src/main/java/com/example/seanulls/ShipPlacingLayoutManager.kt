package com.example.seanulls

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import com.example.seanulls.managers.AppAssetsManager

/**
 * Объект ShipPlacingLayoutManager отвечает за размещения интерфейса для расстановки кораблей на поле в PreGameActivity
 */
object ShipPlacingLayoutManager {
    private const val CELLS_COUNT_ON_LINE = 11 //Количество ячеек в одной строке с учетом букв и цифр
    const val CELL_COUNT = 10
    private var cellSide = 0 //Длина грани ячейки

    lateinit var buttonField: Array<Array<Button?>>

    var grid: GridLayout? = null //Поле
    var layout: ArrayList<Drawable>? = null


    /**
     * Метод создает представление для размещения кораблей на поле.
     * @param _context Контекст активности
     * @param _grid Поле для размещения
     * @return Возвращает true, если представление успешно создано, иначе false
     */
    fun createPlaceableView(_context: Context, _grid: GridLayout?): Boolean {
        grid = _grid

        //Определение размеров ячейки исходя из размеров экрана
        val width = _context.resources.displayMetrics.widthPixels / 2
        val height = _context.resources.displayMetrics.heightPixels
        val size = if (width < height) width else height
        cellSide = size / 12

        grid!!.columnCount = CELLS_COUNT_ON_LINE
        grid!!.rowCount = CELLS_COUNT_ON_LINE
        layout = AppAssetsManager.layoutSprites

        //Инициализация сетки
        if (initializeGrid()) if (createButtonSet(
                grid!!.context
            )
        ) //Создание набора кнопок
            if (fillGridWithButtons(grid!!.context)) //Заполнения GridLayout кнопками
                return true

        return false
    }

    //Метод отвечающий инициализация сетки
    private fun initializeGrid(): Boolean {
        val params = grid!!.layoutParams

        params.width = cellSide * CELLS_COUNT_ON_LINE
        params.height = cellSide * CELLS_COUNT_ON_LINE
        grid!!.layoutParams = params

        return true
    }

    //Метод отвечающий за создание набора кнопок для поля
    private fun createButtonSet(_gridContext: Context): Boolean {
        buttonField = Array(10) { arrayOfNulls(10) }

        for (i in 0 until CELL_COUNT) {
            for (j in 0 until CELL_COUNT) {
                val gridParams = GridLayout.LayoutParams()

                gridParams.width = cellSide
                gridParams.height = cellSide
                gridParams.setMargins(0, 0, 0, 0)

                buttonField[i][j] = Button(_gridContext)
                buttonField[i][j]!!.id = i * 10 + j
                buttonField[i][j]!!.layoutParams = gridParams
                buttonField[i][j]!!.background = layout!![21]
            }
        }
        return true
    }

    //Метод отвечающий за заполнение сетки кнопками
    private fun fillGridWithButtons(_gridContext: Context): Boolean {
        val gp = GridLayout.LayoutParams()

        gp.width = cellSide
        gp.height = cellSide
        gp.setMargins(0, 0, 0, 0)

        val emptySpace = ImageView(_gridContext)
        emptySpace.layoutParams = gp
        emptySpace.background = layout!![0]

        grid!!.addView(emptySpace) //Левый верхний пустой квадрат
        for (i in 1 until CELLS_COUNT_ON_LINE) { //Верхняя линия разметки
            val gridParams = GridLayout.LayoutParams()
            gridParams.width = cellSide
            gridParams.height = cellSide
            gridParams.setMargins(0, 0, 0, 0)
            val image = ImageView(_gridContext)
            image.layoutParams = gridParams
            image.setImageDrawable(layout!![i])
            grid!!.addView(image)
        }

        for (i in 1 until CELLS_COUNT_ON_LINE) { //Боковая линия разметки
            val gridParams = GridLayout.LayoutParams()
            gridParams.width = cellSide
            gridParams.height = cellSide
            gridParams.setMargins(0, 0, 0, 0)
            val image = ImageView(_gridContext)
            image.layoutParams = gridParams
            image.setImageDrawable(layout!![i + 10])
            grid!!.addView(image)
            for (j in 1 until CELLS_COUNT_ON_LINE) { //Кнопки
                grid!!.addView(buttonField[i - 1][j - 1])
            }
        }

        return true
    }

    /**
     * Получить массив кнопок.
     * @return Возвращает двумерный массив кнопок
     */
    fun getButtonFields(): Array<Array<Button?>> = buttonField
}