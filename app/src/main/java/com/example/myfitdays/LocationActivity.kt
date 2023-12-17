package com.example.myfitdays

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.Locale


@Suppress("DEPRECATION")
class LocationActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private val locationPermissionCodeFINE = 2 //viene utilizzato come identificatore univoco per una particolare richiesta. Usato in onRequestPermissionsResult
    private val locationPermissionCodeCOARSE = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCodeFINE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCodeCOARSE)
       else
           requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        } catch (e: SecurityException) {
            // Può essere lanciata una SecurityException se il permesso viene revocato
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        val tvGpsLocation = findViewById<TextView>(R.id.location)
        val geocoder = Geocoder(this, Locale.getDefault())
        lateinit var indirizzo: String
        try {
            //il metodo non deprecato richiede per forza l'API TIRAMISU ed un geocodeListener
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.size > 0) {
                indirizzo = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        tvGpsLocation.text = "${indirizzo}\nLe tue coordinate sono:\nLatitudine: ${location.latitude},\nLongitudine: ${location.longitude}"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2 || requestCode == 3) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}