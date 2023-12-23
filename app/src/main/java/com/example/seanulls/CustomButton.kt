package com.example.seanulls

import android.content.Context
import androidx.appcompat.widget.AppCompatButton

/**
 * Класс CustomButton представляет кастомную кнопку используемую для полей кораблей, наследующуюся от AppCompatButton.
 * Он добавляет свойство isButtonPressed для отслеживания статуса нажатия кнопки.
 */
class CustomButton(context: Context) : AppCompatButton(context) {
    /**
     * Свойство isButtonPressed используется для отслеживания статуса нажатия кнопки.
     * Установка статуса происходит через метод setPressedStatus().
     */
    var isButtonPressed = false
        private set

    /**
     * Метод setPressedStatus() используется для установки статуса нажатия кнопки.
     */
    fun setPressedStatus() {
        isButtonPressed = true
    }
}