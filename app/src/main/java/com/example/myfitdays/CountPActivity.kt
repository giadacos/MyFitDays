package com.example.myfitdays

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
import kotlin.math.sqrt

class CountPActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCount = 0
    private lateinit var stepCountTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var accelerometer: Sensor? = null
    private val STEPSKEY = "steps_count"
    private var midnightResetHandler: Handler? = null
    private lateinit var periodicResetHandler: Handler
    private val CHECKINTERVAL = 60000
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countp)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        stepCountTextView = findViewById(R.id.step_count_text_view)
        sharedPreferences = getSharedPreferences("MyFitDaysPrefs", Context.MODE_PRIVATE)

        //Recupera il conteggio dei passi salvato o impostalo a 0 se non presente
        stepCount = sharedPreferences.getInt(STEPSKEY, 0)
        stepCountTextView.text = "Passi compiuti: $stepCount"

        //Reset del conteggio se è mezzanotte in punto
        resetStepCountAtMidnightOrNewDay()
        periodicResetHandler = Handler(Looper.getMainLooper())
        periodicResetHandler.postDelayed({
            checkForMidnightOrNewDay()
        }, CHECKINTERVAL.toLong())

    }

    private fun checkForMidnightOrNewDay() {
        resetStepCountAtMidnightOrNewDay()

        //Richiama il controllo periodicamente
        periodicResetHandler.postDelayed({
            checkForMidnightOrNewDay()
        }, CHECKINTERVAL.toLong())
    }

    private fun resetStepCountAtMidnightOrNewDay() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentSeconds = calendar.get(Calendar.SECOND)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (currentHour == 0 && currentMinute == 0 && currentSeconds == 0) { // Mezzanotte in punto
            resetStepCount()
        } else {
            val lastSavedDay = sharedPreferences.getInt("LAST_SAVED_DAY", -1)
            if (currentDay != lastSavedDay) { // se è un nuovo giorno
                resetStepCount()
            } else {
                // Calcola il tempo fino a mezzanotte e imposta un handler per azzerare i passi
                val midnightResetTime = calculateTimeUntilMidnight()

                //Aggiornamento del conteggio poco prima della mezzanotte

                midnightResetHandler = Handler(Looper.getMainLooper())
                midnightResetHandler?.postDelayed({
                    resetStepCountAtMidnightOrNewDay()
                }, midnightResetTime)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun resetStepCount() {
        stepCount = 0
        stepCountTextView.text = "Steps taken: $stepCount"
        sharedPreferences.edit().putInt(STEPSKEY, stepCount).apply()

        // Salva il giorno corrente per il controllo del nuovo giorno
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        sharedPreferences.edit().putInt("LAST_SAVED_DAY", currentDay).apply()
        //salvo il giorno corrente ricordarmi quale sia
    }

    override fun onResume() {
        super.onResume()
        // Ripristina il conteggio o lo azzera se è mezzanotte in punto o se è un nuovo giorno al momento del resume
        resetStepCountAtMidnightOrNewDay()
    }


    private fun calculateTimeUntilMidnight(): Long {
        val calendar = Calendar.getInstance() //istanza del giorno corrente
        val currentTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val midnight = calendar.timeInMillis
        return midnight - currentTime //tempo corrente - mezzanotte
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val acceleration = event.values
            val magnitude = sqrt(
                acceleration[0].toDouble().pow(2.0) +
                        acceleration[1].toDouble().pow(2.0) +
                        acceleration[2].toDouble().pow(2.0)
            )
            if (magnitude > 22) { //valore che permette un conteggio reale più o meno accurato
                stepCount++
                stepCountTextView.text = "Passi compiuti: $stepCount"
                // Salva il conteggio aggiornato in SharedPreferences
                sharedPreferences.edit().putInt(STEPSKEY, stepCount).apply()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    //metodo obbligatiorio da sovrascrivere anche se non implementato

    override fun onPause() {
        super.onPause()
        midnightResetHandler?.removeCallbacksAndMessages(null)
        /*La chiamata al metodo rimuove tutti i messaggi e i callback associati a midnightResetHandler,
        garantendo che non vengano più eseguite azioni relative all'azzeramento del conteggio
        dei passi quando l'Activity è in pausa.*/
    }

    override fun onDestroy() {
        super.onDestroy()
        midnightResetHandler?.removeCallbacksAndMessages(null)
    }
}