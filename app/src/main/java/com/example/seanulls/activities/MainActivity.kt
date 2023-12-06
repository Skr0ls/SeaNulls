package com.example.seanulls.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.managers.AppAssetsManager
import com.example.seanulls.utils.NavigationPanelRemover

class MainActivity : AppCompatActivity() {

    private var intent: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavigationPanelRemover.remove(this)
        AppAssetsManager.loadAssets(this@MainActivity)

        val textViewInfo = findViewById<TextView>(R.id.textViewInfo)
        val buttonPlay = findViewById<Button>(R.id.buttonMainMenuPlay)

        buttonPlay.setOnClickListener{
            intent = Intent(this, PreGameActivity::class.java)
            startActivity(intent)
            AppActivityManager.setMenuActivity(this)
        }

        textViewInfo.setOnClickListener {
            intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

    }
}