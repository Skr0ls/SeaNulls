package com.example.seanulls

import android.content.Context
import androidx.appcompat.widget.AppCompatButton

class CustomButton(context: Context) : AppCompatButton(context) {

    /**
     * Класс CustomButton представляет кастомизированную кнопку, наследующуюся от AppCompatButton.
     * Он добавляет свойство isButtonPressed для отслеживания статуса нажатия кнопки.
     */

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