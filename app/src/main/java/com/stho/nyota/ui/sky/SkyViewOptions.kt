package com.stho.nyota.ui.sky

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Constellations
import com.stho.nyota.sky.utilities.projections.Projection

// TODO: make ISkyViewSettings readonly (the view reads the data) and IMutableSkyViewSettings available to the fragment...

/*
    Implement by settings and stored in DB
 */
interface IViewOptions
{
    val displaySymbols: Boolean
    val displayStarNames: Boolean
    val displayPlanetNames: Boolean
    val displayConstellations: Boolean
    val displayConstellationNames: Boolean
    val displayGrid: Boolean
    val displayHints: Boolean
    val displayTargets: Boolean
    val displayEcliptic: Boolean
    val displaySatellites: Boolean
    val sphereProjection: Projection
    val gamma: Double
    val lambda: Double
    val radius: Double
    val magnitude: Double
    val style: Settings.Style
}

interface IConstellationViewOptions: IViewOptions
{
    override var displaySymbols: Boolean
    override var displayStarNames: Boolean
    override val displayPlanetNames: Boolean
    override var displayConstellations: Boolean
    override val displayConstellationNames: Boolean
    override val displayGrid: Boolean
    override val displayHints: Boolean
    override val displayTargets: Boolean
    override val displayEcliptic: Boolean
    override val displaySatellites: Boolean
    override val sphereProjection: Projection
    override val gamma: Double
    override val lambda: Double
    override val radius: Double
    override val magnitude: Double
    fun toggleStyle()
    val versionLD: LiveData<Long>
}

interface ISkyViewOptions: IViewOptions
{
    override var displaySymbols: Boolean
    override var displayStarNames: Boolean
    override var displayPlanetNames: Boolean
    override var displayConstellations: Boolean
    override var displayConstellationNames: Boolean
    override var displayGrid: Boolean
    override var displayHints: Boolean
    override var displayTargets: Boolean
    override var displayEcliptic: Boolean
    override var displaySatellites: Boolean
    override var sphereProjection: Projection
    override var gamma: Double
    override var lambda: Double
    override var radius: Double
    override var magnitude: Double
    fun toggleStyle()
    val versionLD: LiveData<Long>
}

