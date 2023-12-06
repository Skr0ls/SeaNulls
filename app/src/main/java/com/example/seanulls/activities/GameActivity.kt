package com.example.seanulls.activities

import android.os.Bundle
import android.view.View
import android.widget.Chronometer
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.GameStepLogic
import com.example.seanulls.R
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.utils.NavigationPanelRemover

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        AppActivityManager.setGameActivity(this)
        NavigationPanelRemover.remove(this)

        val width = resources.displayMetrics.widthPixels / 2
        val height = resources.displayMetrics.heightPixels
        val size = if (width  < height) width  else height
        val logic = GameStepLogic()
        logic.initializeGameField(findViewById<View>(R.id.field_1) as GridLayout, findViewById<View>(R.id.field_2) as GridLayout, size / 13, this)
        logic.initializeTimer(
            findViewById<View>(R.id.textViewFirstPlayerStep) as TextView,
            findViewById<View>(R.id.textViewSecondPlayerStep) as TextView,
            findViewById<View>(R.id.timer) as Chronometer
        )
    }
}