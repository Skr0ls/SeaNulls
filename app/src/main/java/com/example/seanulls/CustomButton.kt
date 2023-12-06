package com.example.seanulls

import android.content.Context
import androidx.appcompat.widget.AppCompatButton

class CustomButton(context: Context) : AppCompatButton(context) {

    var isButtonPressed = false
        private set

    fun setPressedStatus() {
        isButtonPressed = true
    }
}