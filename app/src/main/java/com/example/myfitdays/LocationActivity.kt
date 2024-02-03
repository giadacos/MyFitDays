package com.example.myfitdays

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
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
    private val locationPermissionCodeFINE = 2
    //viene utilizzato come identificatore univoco per una particolare richiesta. Usato in onRequestPermissionsResult
    private val locationPermissionCodeCOARSE = 3
    private var latitudine: Double = 0.0
    private var longitudine: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCodeFINE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCodeCOARSE)
       else
           requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        try {
            val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
            locationManager.requestLocationUpdates(provider, 5000, 5f, this)
        } catch (e: SecurityException) {
            //Può essere lanciata una SecurityException se il permesso viene revocato
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        val tvGpsLocation = findViewById<TextView>(R.id.location)
        val geocoder = Geocoder(this, Locale.getDefault())
        lateinit var indirizzo: String
        try {
            latitudine = location.latitude
            longitudine = location.longitude
            //il metodo non deprecato richiede per forza l'API TIRAMISU ed un geocodeListener
            val addresses = geocoder.getFromLocation(latitudine, longitudine, 1)
            if (addresses != null && addresses.size > 0) {
                indirizzo = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        tvGpsLocation.text = "${indirizzo}\nLe tue coordinate sono:\nLatitudine: %.5f,\nLongitudine: %.5f".format(latitudine, longitudine)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCodeFINE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "non ci sono i permessi necessari!", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == locationPermissionCodeCOARSE) {
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            }
            else {
                Toast.makeText(this, "non ci sono i permessi necessari!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openMap(view: View) {
        val gmmIntentUri = Uri.parse("geo:$latitudine,$longitudine?q=$latitudine,$longitudine")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent) //si controlla se è presente l'app
        } else {
            Toast.makeText(this, "Applicazione delle mappe non trovata", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}