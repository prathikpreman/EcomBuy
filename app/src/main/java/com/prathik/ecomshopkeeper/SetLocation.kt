package com.prathik.ecomshopkeeper

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.prathik.ecomshopkeeper.utils.PreferenceManager
import kotlinx.android.synthetic.main.activity_set_location.*
import kotlinx.android.synthetic.main.white_toolbar.*
import java.util.*


class SetLocation : AppCompatActivity(), OnMapReadyCallback {


    val TAG ="SETLOCATION :"
    val REQUEST_CHECK_SETTINGS=102
    val AUTOCOMPLETE_REQUEST_CODE=234

    private var mMap: GoogleMap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private  var locationRequest: LocationRequest? =null
    private var builder: LocationSettingsRequest.Builder?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location)
        supportActionBar?.hide()
        setTitle()
        setSearchAndBackButton()
        checkForSavedLocation()
        setLocationBtn()
        setLocationButtonEnable(false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //== GOOGLEE DOC



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if(location!=null){
                        Log.d(TAG,"lat: "+location.latitude+" Lon : "+location.longitude)
                    }
                }


        createLocationRequest()

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }



        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.d(TAG,"lat: "+location.latitude+ " long:"+location.longitude)
                    val newLoc = LatLng(location.latitude, location.longitude)
                    mMap?.clear()
                    mMap?.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title("My Place").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)))
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),18F))
                  // mMap?.animateCamera(CameraUpdateFactory.zoomTo(18f));
                    getPlaceName(newLoc)

                    mapProgress.visibility=View.GONE
                    setLocationButtonEnable(true)

                }
            }
        }

      //  initPlaceSearch()

    }

    private fun setTitle(){
        whiteToolbarText.text="Set Location"
    }

    private fun setSearchAndBackButton(){
        whiteToolbarSearch.visibility= View.VISIBLE
        whiteToolbarSearch.setOnClickListener {
            onSearchCalled()
        }

        whiteToolbarBack.visibility=View.VISIBLE
        whiteToolbarBack.setOnClickListener {
            finish()
        }
    }

    fun createLocationRequest() {
         locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 30000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

         builder = locationRequest?.let { LocationSettingsRequest.Builder().addLocationRequest(it) }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    private fun getPlaceName( latlng:LatLng){
        mapProgress.visibility=View.VISIBLE
        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = gcd.getFromLocation(latlng.latitude,latlng.longitude, 1)
        if (addresses.isNotEmpty() ) {
            Log.d(TAG,"current place: "+addresses[0].locality)
            Log.d(TAG,"current place: "+addresses[0].adminArea)
            Log.d(TAG,"current place: "+addresses[0].postalCode)
            Log.d(TAG,"current place: "+addresses[0].extras)
            Log.d(TAG,"current place: "+addresses[0].featureName)
            Log.d(TAG,"current place: "+addresses[0].maxAddressLineIndex)
            Log.d(TAG,"current place: "+addresses[0].premises)
            Log.d(TAG,"current place: "+addresses[0].subAdminArea)
            Log.d(TAG,"current place: "+addresses[0].subLocality)
            mapProgress.visibility=View.GONE

            var pinnedLocation=""
            if(addresses[0].featureName!=null){
                pinnedLocation+=addresses[0].featureName
            }
            if(addresses[0].postalCode!=null){
                pinnedLocation+=", "+addresses[0].postalCode
            }
            if(addresses[0].subLocality!=null){
                pinnedLocation+=", "+addresses[0].subLocality
            }

            address_locality.setText(pinnedLocation)



        } else {

        }
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
       // mMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f), 5000, null)
        mMap?.isMyLocationEnabled = true

        mMap?.setOnMapClickListener {
            mMap?.clear()

            mMap?.addMarker(MarkerOptions()
                    .position(it)
                    .title("My Place")
                   // .snippet("This is my spot!")
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)))

            mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude,it.longitude)))
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,it.longitude), 18f), 5000, null)
            getPlaceName(LatLng(it.latitude,it.longitude))

            PreferenceManager.setString(PreferenceManager.ADDRESS_LATITUDE,""+it.latitude.toString())
            PreferenceManager.setString(PreferenceManager.ADDRESS_LONGITUDE,""+it.longitude.toString())
        }


      //  displayLocationSettingsRequest(this)
    }


    public override fun onStart() {
        super.onStart()
       //  displayLocationSettingsRequest(this)
    }

    private fun setLocationBtn(){

        setCurrentLocationBtn.setOnClickListener {
            PreferenceManager.setString(PreferenceManager.ADDRESS_ROOM,""+address_room.text.toString())
            PreferenceManager.setString(PreferenceManager.ADDRESS_LOCALITY,""+address_locality.text.toString())
            PreferenceManager.setString(PreferenceManager.ADDRESS_LANDMARK,""+address_landmark.text.toString())
            finish()
        }
    }


    private fun checkForSavedLocation(){
           address_room.setText(PreferenceManager.getString(PreferenceManager.ADDRESS_ROOM,""))
           address_locality.setText(PreferenceManager.getString(PreferenceManager.ADDRESS_LOCALITY,""))
           address_landmark.setText(PreferenceManager.getString(PreferenceManager.ADDRESS_LANDMARK,""))
    }


  /*  private fun initPlaceSearch(){
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
           override fun onPlaceSelected(place:Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId())
            }
            override fun onError(status:Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status)
            }
        })
    }
*/




    fun onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        val fields =
            Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        // Start the autocomplete intent.
        val intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).setCountry("NG") //NIGERIA
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    fun setLocationButtonEnable(isEnable:Boolean){

        setCurrentLocationBtn.isEnabled=isEnable
        setCurrentLocationBtn.isClickable=isEnable

        if(isEnable){
            setCurrentLocationBtn.alpha=1.0F
        }else{
            setCurrentLocationBtn.alpha=0.4F
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                Log.i(TAG, "Place: " + place?.getName() + ", " + place?.getId() + ", " + place?.getAddress())
                Toast.makeText(this, "ID: " + place?.getId() + "address:" + place?.getAddress() + "Name:" + place?.getName() + " latlong: " + place?.getLatLng(), Toast.LENGTH_LONG).show()
                val address = place?.getAddress()
                // do query with address
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR)
            {
                // TODO: Handle the error.
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                Toast.makeText(this, "Error: " + status?.statusMessage, Toast.LENGTH_LONG).show()
                Log.i(TAG, status?.statusMessage)
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
        }
    }






    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2.toLong()
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.getStatusCode()) {

                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(TAG, "All location settings are satisfied.")
                }

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        TAG,
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this@SetLocation, REQUEST_CHECK_SETTINGS)
                    } catch (e: SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                    TAG,
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }










}
