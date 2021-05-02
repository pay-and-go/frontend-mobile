package com.example.payandgo

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.payandgo.databinding.ActivityInRouteBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.ArrayList


class InRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    //To get Location
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    private val PERMISSION_REQUEST = 10
    lateinit var locationManager: LocationManager
    private var hasGPS = false
    private var hasNetwork = false
    private var locationGPS: Location? = null
    private var locationNetwork: Location? = null
    //To create notification
    private val CHANNEL_ID = "channel_location"
    private var notificationId = 100
    //for maps
    private lateinit var mMap: GoogleMap
    private lateinit var geocoder:Geocoder
    private  val ACCESS_LOCATION_REQUEST_CODE = 1001
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var userLocationMarker: Marker? = null


    //Others
    private lateinit var bindingInRouteActivity: ActivityInRouteBinding
    private lateinit var locHandler: Handler
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInRouteActivity = ActivityInRouteBinding.inflate(layoutInflater)
        setContentView(bindingInRouteActivity.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geocoder = Geocoder(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 500
        locationRequest.fastestInterval = 500
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                println("onLocationResult ${locationResult.lastLocation}")
                if(mMap != null){
                    setUserLocationMarker(locationResult.lastLocation)
                }
            }

        }
            //Handler for the repeating code
        locHandler = Handler()
        //Notification Channel for the notifications
        createNotificationChannel()
        disableView()
        mContext = this
        bindingInRouteActivity.btnStopLocation.setOnClickListener { stopGetLocation() }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permissions)){
                enableView(this)
            }else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
    }

    private fun disableView(){
        bindingInRouteActivity.btnStartLocation.isEnabled = false
        bindingInRouteActivity.btnStartLocation.alpha = 0.5F
    }

    private fun enableView(ctx: Context){
        bindingInRouteActivity.btnStartLocation.isEnabled = true
        bindingInRouteActivity.btnStartLocation.alpha = 1F
        bindingInRouteActivity.btnStartLocation.setOnClickListener {
            startGetLocation(ctx)
        }
        Toast.makeText(this, "Hecho", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGPS || hasNetwork){
            if (hasGPS){
                println("tiene GPS")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGPS = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

                val localGPSlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGPSlocation != null){
                    locationGPS = localGPSlocation
                }
            }
            if (hasNetwork){
                println("tiene Network")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null){
                    locationNetwork = localNetworkLocation
                }
            }
            if (locationGPS != null && locationNetwork != null){
                if(locationGPS!!.accuracy > locationNetwork!!.accuracy){
                    println("Network latitud ${locationNetwork!!.latitude}")
                    println("Network longitud ${locationNetwork!!.longitude}")
                    bindingInRouteActivity.txtLocation.append("\n Network")
                    bindingInRouteActivity.txtLocation.append("\n Latitud " + locationNetwork!!.latitude)
                    bindingInRouteActivity.txtLocation.append("\n Longitud " + locationNetwork!!.longitude)
                }else {
                    println("GPS latitud ${locationGPS!!.latitude}")
                    println("GPS longitud ${locationGPS!!.longitude}")
                    bindingInRouteActivity.txtLocation.append("\n GPS")
                    bindingInRouteActivity.txtLocation.append("\n Latitud " + locationGPS!!.latitude)
                    bindingInRouteActivity.txtLocation.append("\n Longitud " + locationGPS!!.longitude)
                }
            }
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>):Boolean {
        var allSuccess = true
        for (i in permissionArray.indices){
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    private var notRunnable = object: Runnable {
        override  fun run() {
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        val response = apolloClient.mutate(FindTollMutation(-25.0,73.0)).await()
//                        println("respuesta ${response.data?.findToll?.distance}")
//                        //if distance < x create notification
//                    }  catch (e: Exception){
//                        println("exception $e")
//                    }
//                }
            Toast.makeText(mContext, "Texto prueba", Toast.LENGTH_SHORT)
            getLocation()
            sendNotification()
            locHandler.postDelayed(this, 5000)
        }
    }

    private fun startGetLocation(ctx: Context) {
        mContext = ctx
        notRunnable.run()
    }

    private fun stopGetLocation() {
        locHandler.removeCallbacks(notRunnable)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Peaje cerca"
            val descriptionT = "Estas a x metros del próximo peaje"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionT
            }
            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, MapsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pay_and_go_logo)
                .setContentTitle("Peaje cerca")
                .setContentText("Estas a x metros del próximo peaje")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val location1 = LatLng(4.606004, -74.101538)
        val location2 = LatLng(6.258191, -75.575722)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permissions)){
                enableUserLocation()
                val URL = getDirectionUrl(location1,location2)
                println("GoogleMap URL : $URL")
                GetDirection(URL).execute()
//                zoomToUserLocation()
            }else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
    }

    private fun setUserLocationMarker(location: Location){
        val position = LatLng(location.latitude,location.longitude)
        if(userLocationMarker == null) {
            //Create a new Marker
            var markerOptions = MarkerOptions()
            markerOptions.position(position)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))
            markerOptions.rotation(location.bearing)
            markerOptions.anchor(0.5f, 0.5f)
            userLocationMarker = mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17f))
        }else {
            //User the previously created marker
            userLocationMarker?.position = position
            userLocationMarker?.rotation = location.bearing
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17f))
        }
    }

    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permissions)){
                startLocationUpdates()
            }else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun enableUserLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permissions)){
                mMap.isMyLocationEnabled = true
            }else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }

    }

    private fun zoomToUserLocation(){
        var locationTask:Task<Location>
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission(permissions)){
                locationTask = fusedLocationProviderClient.lastLocation
                locationTask.addOnSuccessListener {location: Location? ->
                    location?.let {
                        val position = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,20f))

                    }
                }
            }else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
    }

    fun getDirectionUrl(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=AIzaSyDo7sKo0TqTd3IFySC7osydHTi1Kyrammo"
    }

    inner class GetDirection(var url:String): AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>>? {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble(),
//                            respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble(),
//                            respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
//                    path.add(endLatLng)
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e: Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }

    }


    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}