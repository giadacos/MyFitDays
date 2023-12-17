package com.example.myfitdays

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Recupera le SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Trova gli EditText nel layout
        val nome = findViewById<EditText>(R.id.nome)
        val cognome = findViewById<EditText>(R.id.cognome)
        val sex = findViewById<EditText>(R.id.sesso)
        val data = findViewById<EditText>(R.id.birthdate)
        val h = findViewById<EditText>(R.id.altezza)
        val peso = findViewById<EditText>(R.id.peso)
        val gsang = findViewById<EditText>(R.id.gsanguigno)

        // Carica i valori salvati nelle SharedPreferences e imposta gli EditText
        nome.setText(sharedPreferences.getString("NOME", ""))
        cognome.setText(sharedPreferences.getString("COGNOME", ""))
        sex.setText(sharedPreferences.getString("SESSO", ""))
        data.setText(sharedPreferences.getString("DATA_NASCITA", ""))
        h.setText(sharedPreferences.getString("ALTEZZA", ""))
        peso.setText(sharedPreferences.getString("PESO", ""))
        gsang.setText(sharedPreferences.getString("GRUPPO_SANGUIGNO", ""))

        // Aggiungi un listener agli EditText per salvare i valori nelle SharedPreferences
        nome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("NOME", nome.text.toString()).apply()
            }
        })
        cognome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("COGNOME", cognome.text.toString()).apply()
            }
        })
        sex.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("SESSO", sex.text.toString()).apply()
            }
        })
        data.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("DATA_NASCITA", data.text.toString()).apply()
            }
        })
        h.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("ALTEZZA", h.text.toString()).apply()
            }
        })
        peso.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("PESO", peso.text.toString()).apply()
            }
        })
        gsang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit().putString("GRUPPO_SANGUIGNO", gsang.text.toString()).apply()
            }
        })
    }
}
