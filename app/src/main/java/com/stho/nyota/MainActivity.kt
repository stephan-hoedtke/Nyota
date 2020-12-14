package com.stho.nyota

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.stho.nyota.repository.Repository
import com.stho.nyota.ui.finder.IOrientationFilter
import com.stho.nyota.ui.finder.OrientationAccelerationFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch

// TODO Play protect doesn't recognize app's developer ...
// TODO Play Protect hasn't seen this app before... security scan...
// TODO Cities: delete, create a new, pick from a predefined, add all predefined, change automatic to manual
// TODO Settings: automatic: location, compass, time --> 3 options can be stopped anytime
// TODO Click on star
// Star View...

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var repository: Repository
    private lateinit var handler: Handler
    private lateinit var locationManager: LocationManager
    private var currentLocation: com.stho.nyota.sky.utilities.Location? = null
    private lateinit var orientationFilter: IOrientationFilter
    private lateinit var orientationSensorListener: OrientationSensorListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        orientationFilter = OrientationAccelerationFilter()
        orientationSensorListener = OrientationSensorListener(this, orientationFilter)

//  TODO: cleanup code or comment
//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_satellites,
                R.id.nav_planets,
                R.id.nav_constellations,
                R.id.nav_stars
            )
            , drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        handler = Handler()
        repository = Repository.requireRepository(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> openSettings()
            R.id.action_cities -> openCities()
            R.id.action_moment -> openMoment()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openMoment(): Boolean {
        findNavController().navigate(R.id.action_global_nav_moment)
        return true
    }

    private fun openCities(): Boolean {
        findNavController().navigate(R.id.action_global_nav_city_picker)
        return true
    }

    private fun openSettings(): Boolean {
        findNavController().navigate(R.id.action_global_nav_settings)
        return true
    }

    private fun findNavController(): NavController =
        findNavController(R.id.nav_host_fragment)

    override fun onSupportNavigateUp(): Boolean =
        findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onResume() {
        super.onResume()
        executeHandlerToUpdateUniverse()
        executeHandlerToUpdateOrientation()
        enableLocationListener()
        orientationSensorListener.onResume()
    }

    override fun onPause() {
        super.onPause()
        disableLocationListener()
        orientationSensorListener.onPause()
        stopHandler()
    }

    private fun enableLocationListener() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        } catch (ex: SecurityException) {
            //ignore
        }
    }

    private fun disableLocationListener() {
        try {
            locationManager.removeUpdates(this)
        } catch (ex: SecurityException) {
            //ignore
        }
    }

    private fun executeHandlerToUpdateUniverse() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    repository.updateForNow(currentLocation)
                }
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun executeHandlerToUpdateOrientation() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    repository.updateOrientation(orientationFilter.orientation)
                }
                handler.postDelayed(this, 200)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    override fun onStop() {
        super.onStop()
        stopHandler()
    }

    private fun stopHandler() =
        handler.removeCallbacksAndMessages(null)

    override fun onLocationChanged(location: android.location.Location) {
        currentLocation = com.stho.nyota.sky.utilities.Location.fromAndroidLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Do nothing
    }

    override fun onProviderEnabled(provider: String?) {
        // Do nothing
    }

    override fun onProviderDisabled(provider: String?) {
        // Do nothing
    }
}

