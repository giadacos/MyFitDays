package com.example.myfitdays

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.myfitdays.R
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locationActivity = findViewById<Button>(R.id.location)
        locationActivity.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
        val countPActivity = findViewById<Button>(R.id.ContaPassi)
        countPActivity.setOnClickListener{
            val intent2 = Intent(this, CountPActivity::class.java)
            startActivity(intent2)
        }
    }
}