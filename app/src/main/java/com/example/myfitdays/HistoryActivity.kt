package com.example.myfitdays

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.myfitdays.database.MyDatabaseHelper
import com.example.myfitdays.StepEntry
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        databaseHelper = MyDatabaseHelper(this)

        // Ottieni lo storico dei passi dal database
        val stepHistory = getStepHistoryFromDatabase()

        // Mostra lo storico utilizzando un TextView
        val historyTextView = findViewById<TextView>(R.id.historyTextView)
        historyTextView.text = buildHistoryString(stepHistory)
    }

    private fun getStepHistoryFromDatabase(): List<StepEntry> {
        // Ottieni lo storico dei passi dal database
        return databaseHelper.getStepHistory()
    }

    private fun buildHistoryString(stepHistory: List<StepEntry>): String {
        // Costruisci una stringa rappresentante lo storico
        val stringBuilder = StringBuilder()
        for (entry in stepHistory) {
            val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(entry.date)
            stringBuilder.append("Passi: ${entry.stepCount}, Data: $formattedDate\n")
        }
        return stringBuilder.toString()
    }
}




