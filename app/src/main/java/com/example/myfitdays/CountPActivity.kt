package com.example.myfitdays

//package com.example.passsssssi
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.pow

class CountPActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCount = 0
    private lateinit var stepCountTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var accelerometer: Sensor? = null
    private val STEPS_KEY = "steps_count"
    private var midnightResetHandler: Handler? = null
    private lateinit var periodicResetHandler: Handler
    private val CHECK_INTERVAL = 60000
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countp)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        stepCountTextView = findViewById(R.id.step_count_text_view)
        sharedPreferences = getSharedPreferences("MyFitDaysPrefs", Context.MODE_PRIVATE)

        // Recupera il conteggio dei passi salvato o impostalo a 0 se non presente
        stepCount = sharedPreferences.getInt(STEPS_KEY, 0)
        stepCountTextView.text = "Steps taken: $stepCount"

        // Reset del conteggio se è mezzanotte in punto
        resetStepCountAtMidnightOrNewDay()
        periodicResetHandler = Handler(Looper.getMainLooper())
        periodicResetHandler.postDelayed({
            checkForMidnightOrNewDay()
        }, CHECK_INTERVAL.toLong())
    }

    private fun checkForMidnightOrNewDay() {
        resetStepCountAtMidnightOrNewDay()

        // Richiama il controllo periodicamente
        periodicResetHandler.postDelayed({
            checkForMidnightOrNewDay()
        }, CHECK_INTERVAL.toLong())
    }

    private fun resetStepCountAtMidnightOrNewDay() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentSeconds = calendar.get(Calendar.SECOND)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (currentHour == 0 && currentMinute == 0 && currentSeconds == 0) { // Mezzanotte
            resetStepCount()
        } else {
            val lastSavedDay = sharedPreferences.getInt("LAST_SAVED_DAY", -1)
            if (currentDay != lastSavedDay) { // Nuovo giorno
                resetStepCount()
            } else {
                // Calcola il tempo fino a mezzanotte e imposta un handler per azzerare i passi
                val midnightResetTime = calculateTimeUntilMidnight()
                midnightResetHandler = Handler(Looper.getMainLooper())
                midnightResetHandler?.postDelayed({
                    resetStepCountAtMidnightOrNewDay()
                }, midnightResetTime)
            }
        }
    }

    private fun resetStepCount() {
        stepCount = 0
        stepCountTextView.text = "Steps taken: $stepCount"
        sharedPreferences.edit().putInt(STEPS_KEY, stepCount).apply()

        // Salva il giorno corrente per il controllo del nuovo giorno
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        sharedPreferences.edit().putInt("LAST_SAVED_DAY", currentDay).apply()
    }

    override fun onResume() {
        super.onResume()
        // Ripristina il conteggio se è mezzanotte in punto o se è un nuovo giorno al momento del resume
        resetStepCountAtMidnightOrNewDay()
    }


    private fun calculateTimeUntilMidnight(): Long {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val midnight = calendar.timeInMillis
        return midnight - currentTime
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val acceleration = event.values
            val magnitude = Math.sqrt(
                acceleration[0].toDouble().pow(2.0) +
                        acceleration[1].toDouble().pow(2.0) +
                        acceleration[2].toDouble().pow(2.0)
            )
            if (magnitude > 10) {
                stepCount++
                stepCountTextView.text = "Steps taken: $stepCount"
                // Salva il conteggio aggiornato in SharedPreferences
                sharedPreferences.edit().putInt(STEPS_KEY, stepCount).apply()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        // Rimuove il callback per azzerare i passi se è mezzanotte durante il pause
        midnightResetHandler?.removeCallbacksAndMessages(null)
    }
    override fun onDestroy() {
        super.onDestroy()
        //sensorManager.unregisterListener(this)
    }
}