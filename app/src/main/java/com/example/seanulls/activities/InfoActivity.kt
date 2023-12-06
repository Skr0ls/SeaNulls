package com.example.seanulls.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R
import com.example.seanulls.utils.NavigationPanelRemover

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        NavigationPanelRemover.remove(this)

        val buttonBack = findViewById<Button>(R.id.buttonBackToMainMenuFromInfo)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}