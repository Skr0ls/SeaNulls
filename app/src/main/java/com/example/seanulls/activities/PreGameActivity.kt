package com.example.seanulls.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seanulls.R
import com.example.seanulls.ShipPlacingLayoutManager
import com.example.seanulls.ShipPlacingLogic
import com.example.seanulls.managers.AppActivityManager
import com.example.seanulls.utils.NavigationPanelRemover

class PreGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregame)
        AppActivityManager.setPlacingActivity(this)
        NavigationPanelRemover.remove(this)

        if (ShipPlacingLayoutManager.createPlaceableView(this, findViewById<View>(R.id.grid) as GridLayout)) { //Делим пополам и на 12 элементов чтобы был отспут по половине
            val list = ArrayList<ImageView?>()
            list.add(findViewById<View>(R.id.view_ship_11) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_12) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_13) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_14) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_21) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_22) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_23) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_31) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_32) as ImageView)
            list.add(findViewById<View>(R.id.view_ship_41) as ImageView)
            Logic(list)
        }
    }

    private fun Logic(viewShips: ArrayList<ImageView?>) {
        val logic = ShipPlacingLogic()
        logic.fullInitialization(viewShips, this, findViewById<View>(R.id.textViewPlayer) as TextView, findViewById<View>(R.id.buttonRemove) as Button, findViewById<View>(R.id.buttonRotate) as Button, findViewById<View>(R.id.buttonNext) as Button, findViewById<View>(R.id.buttonBack) as Button,)
    }
}