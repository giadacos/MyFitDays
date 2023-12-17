package com.example.myfitdays

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myfitdays.R.id.contapassi

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
        val countPActivity = findViewById<Button>(contapassi)
        countPActivity.setOnClickListener{
            val intent2 = Intent(this, CountPActivity::class.java)
            startActivity(intent2)
        }
        val infoActivity = findViewById<Button>(R.id.info)
        infoActivity.setOnClickListener {
            val intent3 = Intent(this, InfoActivity::class.java)
            startActivity(intent3)
        }
    }
}