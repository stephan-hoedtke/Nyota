package com.stho.nyota

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.utilities.City
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
// TODO currentLocation, repository, ... --> ViewModel
// TODO locationManager, ... --> LocationSensorListener


// TODO: Request for Permission to access location, if permission wasn't granted. And tell the user
// TODO: Review calculation of distance for cities
// TODO: Constellation Sky View: Update for the moment
// TODO: Automatic city: select automatic city or (last) non automatic city
//

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var handler: Handler
    private lateinit var viewModel: MainViewModel
    private lateinit var orientationFilter: IOrientationFilter
    private lateinit var orientationSensorListener: OrientationSensorListener
    private lateinit var locationFilter: ILocationFilter
    private lateinit var locationServiceListener: LocationServiceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        handler = Handler()
        viewModel = createViewModel(MainViewModel::class.java)

        orientationFilter = OrientationAccelerationFilter()
        orientationSensorListener = OrientationSensorListener(this, orientationFilter)

        locationFilter = SimpleLocationFilter()
        locationServiceListener = LocationServiceListener(this, locationFilter)


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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> openSettings()
            R.id.action_cities -> openCities()
            R.id.action_time -> openMomentTime()
            R.id.action_location-> openMomentLocation()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openMomentTime(): Boolean {
        findNavController().navigate(R.id.action_global_nav_moment_time)
        return true
    }

    private fun openMomentLocation(): Boolean {
        findNavController().navigate(R.id.action_global_nav_moment_location)
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

        if (viewModel.updateOrientationAutomatically) {
            orientationSensorListener.onResume()
        }

        if (viewModel.updateLocationAutomatically) {
            locationServiceListener.onError = { viewModel.disableAutomaticLocation() }
            locationServiceListener.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        stopHandler()
        orientationSensorListener.onPause()
        locationServiceListener.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionManager = PermissionManager(this)
        permissionManager.onMessage = { message -> showSnackBar(message) }
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun executeHandlerToUpdateUniverse() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    viewModel.updateForNow(locationFilter.currentLocation)
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
                    viewModel.updateOrientation(orientationFilter.currentOrientation)
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

    private fun showSnackBar(message: String) {
        val container: View = findViewById<View>(R.id.drawer_layout)
        Snackbar.make(container, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .show()
    }
}

