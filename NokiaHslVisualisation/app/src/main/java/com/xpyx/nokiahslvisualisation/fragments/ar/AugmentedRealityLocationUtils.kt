package com.xpyx.nokiahslvisualisation.fragments.ar

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast

import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*
import com.xpyx.nokiahslvisualisation.R

object AugmentedRealityLocationUtils {

    private const val RENDER_MARKER_MIN_DISTANCE = 2//meters
    private const val RENDER_MARKER_MAX_DISTANCE = 7000//meters
    const val INVALID_MARKER_SCALE_MODIFIER = -1F
    const val INITIAL_MARKER_SCALE_MODIFIER = 0.1f

    @Throws(UnavailableException::class)
    fun setupSession(activity: Activity, installRequested: Boolean): Session? {
        when (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                return null
            }
            ArCoreApk.InstallStatus.INSTALLED -> {
                //just continue with session setup
            }
            else -> {
                //just continue with session setup
            }
        }

        val session = Session(activity)
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        // IMPORTANT!!!  ArSceneView requires the `LATEST_CAMERA_IMAGE` non-blocking update mode.

        session.configure(config)
        return session
    }

    fun handleSessionException(activity: Activity, sessionException: UnavailableException) {
        val message = when (sessionException) {
            is UnavailableArcoreNotInstalledException ->
                activity.resources.getString(R.string.arcore_not_installed)

            is UnavailableUserDeclinedInstallationException ->
                activity.resources.getString(R.string.arcore_not_installed)

            is UnavailableApkTooOldException ->
                activity.resources.getString(R.string.arcore_not_updated)

            is UnavailableSdkTooOldException ->
                activity.resources.getString(R.string.arcore_not_supported)

            else -> activity.resources.getString(R.string.arcore_not_supported)
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun getScaleModifierBasedOnRealDistance(distance: Int): Float {
        return when (distance) {
            in Integer.MIN_VALUE..RENDER_MARKER_MIN_DISTANCE -> INVALID_MARKER_SCALE_MODIFIER
            in RENDER_MARKER_MIN_DISTANCE + 1..20 -> 0.8f
            in 21..40 -> 0.55f
            in 41..60 -> 0.5f
            in 61..80 -> 0.45f
            in 81..100 -> 0.4f
            in 101..1000 -> 0.3f
            in 1001..1500 -> 0.25f
            in 1501..2000 -> 0.2f
            in 2001..2500 -> 0.15f
            in 2501..3000 -> 0.1f
            in 3001..RENDER_MARKER_MAX_DISTANCE -> 0.09f
            in RENDER_MARKER_MAX_DISTANCE + 1..Integer.MAX_VALUE -> 0.07f
            else -> -1f
        }
    }
    // TODO something
    fun generateRandomHeightBasedOnDistance(distance: Int): Float {
        return -3f
    }
    // m & km
    fun showDistance(distance: Int): String {
        return if (distance >= 1000)
            String.format("%.2f", (distance.toDouble() / 1000)) + " km"
        else
            "$distance m"
    }
}
