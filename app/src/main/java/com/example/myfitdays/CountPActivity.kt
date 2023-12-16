package com.example.myfitdays

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.math.sqrt

class CountPActivity: AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCount=0
    private lateinit var stepCountTextView: TextView
    private var accelerometer: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countp)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        stepCountTextView = findViewById(R.id.step_count_text_view)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val acceleration = event.values
            val magnitude = sqrt(acceleration[0].pow(2) + acceleration[1].pow(2) + acceleration[2].pow(2))
            if (magnitude > 10) {
                stepCount++
                stepCountTextView.text = "Steps taken: $stepCount"
            }
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Gestisci cambiamenti di accuratezza se necessario
    }
}
