package com.stho.nyota

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var orientationFilter: OrientationAccelerationFilter
    private lateinit var orientationSensorListener: OrientationSensorListener
    private lateinit var locationFilter: LocationFilter
    private lateinit var locationServiceListener: LocationServiceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        handler = Handler(Looper.getMainLooper())
        viewModel = createViewModel(MainViewModel::class.java)

        orientationFilter = OrientationAccelerationFilter()
        orientationSensorListener = OrientationSensorListener(this, orientationFilter)

        locationFilter = LocationFilter()
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
//                R.id.nav_satellites,
//                R.id.nav_planets,
//                R.id.nav_constellations,
//                R.id.nav_stars,
//                // R.id.nav_sky,
            )
            , drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // TODO: rename: openSettings or onSettings...
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> { openSettings(); true }
            R.id.action_cities -> { openCities(); true }
            R.id.action_time -> { openMomentTime(); true }
            R.id.action_location-> { openMomentLocation(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openMomentTime() =
        findNavController().navigate(R.id.action_global_nav_moment_time)

    private fun openMomentLocation() =
        findNavController().navigate(R.id.action_global_nav_moment_location)

    private fun openCities() =
        findNavController().navigate(R.id.action_global_nav_city_picker)

    private fun openSettings() =
        findNavController().navigate(R.id.action_global_nav_settings)

    private fun findNavController(): NavController =
        findNavController(R.id.nav_host_fragment)

    override fun onSupportNavigateUp(): Boolean =
        findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onResume() {
        super.onResume()
        executeHandlerToSaveChanges()
        executeHandlerToUpdateUniverse()
        executeHandlerToUpdateOrientation()

        if (viewModel.updateOrientationAutomatically) {
            orientationSensorListener.onResume()
        }

        if (viewModel.updateLocationAutomatically) {
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
        PermissionManager(this).onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    internal fun enableLocationServiceListener() {
        PermissionManager(this).checkPermissionToObtainLocation()
        if (locationFilter.isInActive) {
            locationServiceListener.onResume()
        }
    }

    private fun executeHandlerToUpdateUniverse() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    if (locationFilter.isActive) {
                        viewModel.updateForNow(locationFilter.currentLocation)
                        disableLocationListenerWhenCurrentLocationIsSure()
                    }
                    else {
                        viewModel.updateForNow()
                    }
                }
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun executeHandlerToSaveChanges() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    if (viewModel.isDirty)
                        viewModel.saveChanges()
                }
                handler.postDelayed(this, 10000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }


    private fun disableLocationListenerWhenCurrentLocationIsSure() {
        if (locationFilter.isStable) {
            locationServiceListener.onPause()
        }
    }

    private fun executeHandlerToUpdateOrientation() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Default).launch {
                    viewModel.updateOrientation(orientationFilter.currentOrientation)
                }
                handler.postDelayed(this, DEFAULT_ORIENTATION_REFRESH_DELAY)
            }
        }
        handler.postDelayed(runnableCode, DEFAULT_ORIENTATION_REFRESH_DELAY)
    }

    override fun onStop() {
        super.onStop()
        stopHandler()
    }

    private fun stopHandler() =
        handler.removeCallbacksAndMessages(null)

    internal fun showSnackbar(message: String) {
        val container: View = findViewById<View>(R.id.drawer_layout)
        Snackbar.make(container, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.colorSignalBackground))
            .setTextColor(getColor(R.color.colorSecondaryText))
            .show()
    }

    companion object {
        private const val DEFAULT_ORIENTATION_REFRESH_DELAY = 90L
    }
}

