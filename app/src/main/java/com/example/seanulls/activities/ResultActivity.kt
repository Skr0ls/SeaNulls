package com.example.seanulls.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.utils.MusicService
import com.example.seanulls.utils.NavigationPanelRemover

class ResultActivity : AppCompatActivity() {

    private var container: GridLayout? = null
    private var inscription: ArrayList<TextView>? = null
    private var inscriptionOne = "Победа первого игрока"
    private var inscriptionTwo = "Победа второго игрока"
    private var size_one = 0
    private var size_two = 0
    private var winner = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        AppActivityManager.setResultActivity(this)
        NavigationPanelRemover.remove(this)

        startMusic()

        container = findViewById<View>(R.id.containerForInscription) as GridLayout
        winner = intent.extras!!.getInt("winner")
        size_one = inscriptionOne.length
        size_two = inscriptionTwo.length
        initializeInscription()

        val buttonBack: Button = findViewById(R.id.buttonBackToMainMenu)

        buttonBack.setOnClickListener{
            AppActivityManager.getPlacingActivity()?.finish()
            AppActivityManager.getGameActivity()?.finish()
            AppActivityManager.getResultActivity()?.finish()
        }
    }

    private fun initializeInscription() {
        val size: Int
        val str: String
        if (winner == 1) {
            size = size_one
            str = inscriptionOne
        } else {
            size = size_two
            str = inscriptionTwo
        }
        container!!.rowCount = 1
        container!!.columnCount = 1
        inscription = ArrayList()
        val grid = GridLayout(container!!.context)
        grid.rowCount = 1
        grid.columnCount = size
        for (i in 0 until size) {
            val params = GridLayout.LayoutParams()
            params.setMargins(0, 0, 0, 0)
            params.setGravity(Gravity.CENTER)
            val symbol = TextView(this)
            symbol.text = str[i].toString()
            symbol.textSize = 24f
            val color = getColor(R.color.white)
            symbol.setTextColor(color)
            symbol.layoutParams = params
            inscription!!.add(symbol)
            grid.addView(symbol)
        }
        val params = GridLayout.LayoutParams()
        params.setMargins(0, 0, 0, 0)
        params.setGravity(Gravity.CENTER)
        grid.layoutParams = params
        container!!.addView(grid)
    }


    private fun startMusic() {
        val musicIntent = Intent(this, MusicService::class.java)
        startService(musicIntent)
    }

    private fun stopMusic() {
        val musicIntent = Intent(this, MusicService::class.java)
        stopService(musicIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
}