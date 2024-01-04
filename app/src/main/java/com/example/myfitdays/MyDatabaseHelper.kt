package com.example.myfitdays.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myfitdays.StepEntry
import java.text.SimpleDateFormat
import java.util.*

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "step_history.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "step_history"
        private const val COL_ID = "id"
        private const val COL_STEP_COUNT = "step_count"
        private const val COL_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crea la tabella dello storico dei passi
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_STEP_COUNT INTEGER, " +
                "$COL_DATE TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Aggiorna il database se necessario
    }

    fun addStepEntry(stepCount: Int) {
        writableDatabase.use { db ->
            val values = ContentValues()
            values.put(COL_STEP_COUNT, stepCount)
            values.put(COL_DATE, getCurrentDate())
            db.insert(TABLE_NAME, null, values)
        }
    }
    fun getStepHistory(): List<StepEntry> {
        val stepHistory = mutableListOf<StepEntry>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(selectQuery, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val idIndex = cursor.getColumnIndex(COL_ID)
                    val stepCountIndex = cursor.getColumnIndex(COL_STEP_COUNT)
                    val dateIndex = cursor.getColumnIndex(COL_DATE)

                    val id = cursor.getInt(idIndex)
                    val stepCount = cursor.getInt(stepCountIndex)
                    val dateString = cursor.getString(dateIndex)

                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString)

                    stepHistory.add(StepEntry(id, stepCount, date))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return stepHistory
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}
