package com.stho.nyota

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.INTERNET
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionManager(val activity: FragmentActivity) : ActivityCompat.OnRequestPermissionsResultCallback {

    fun checkPermissionToObtainLocation() =
        checkPermission(ACCESS_COARSE_LOCATION, LOCATION_PERMISSION_NAME, LOCATION_PERMISSION_RQ)

    fun checkPermissionToAccessInternet() =
        checkPermission(INTERNET, INTERNET_PERMISSION_NAME, INTERNET_PERMISSION_RQ)

    private fun checkPermission(permission: String, name: String, requestCode: Int) {
        // Build.VERSION.SDK_INT >= Build.VERSION_CODES.M is ensured by gradle settings

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

            // User may have declined earlier, ask Android if we should show him a reason
            if (shouldShowRequestPermissionRationale(activity, permission)) {

                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                showPermissionRequestDialog(permission, name, requestCode)
            }
            else {

                // request the permission with the correct request code.
                // CALLBACK_NUMBER is a integer constants
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }
    }

    private fun showPermissionRequestDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(activity)

        builder.apply {
            setMessage(activity.getString(R.string.message_permission_is_required, name))
            setTitle(activity.getString(R.string.title_permission_request))
            setPositiveButton(activity.getString(R.string.label_ok)) { _, _ ->
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }

        builder.create().show()
    }

    // called from the callback in the activity, which needs to implement the OnRequestPermissionsResultCallback interface.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                showMessage(activity.getString(R.string.message_permission_was_refused, name))
            else
                showMessage(activity.getString(R.string.message_permission_was_granted, name))
        }

        when (requestCode) {
            LOCATION_PERMISSION_RQ -> innerCheck(LOCATION_PERMISSION_NAME)
            INTERNET_PERMISSION_RQ -> innerCheck(INTERNET_PERMISSION_NAME)
        }
    }

    private fun showMessage(message: String) {
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.showSnackbar(message)
    }

    companion object {
        private const val LOCATION_PERMISSION_RQ: Int = 101
        private const val INTERNET_PERMISSION_RQ: Int = 102
        private const val LOCATION_PERMISSION_NAME = "Location"
        private const val INTERNET_PERMISSION_NAME = "Internet"
    }
}

