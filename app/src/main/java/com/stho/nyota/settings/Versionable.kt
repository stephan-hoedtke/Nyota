package com.stho.nyota.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface IVersionable {
    val versionLD: LiveData<Long>
    fun touch()
}

abstract class Versionable: IVersionable {

    private val versionLiveData: MutableLiveData<Long> = MutableLiveData<Long>().apply { value = 0 }

    override val versionLD: LiveData<Long>
        get() = versionLiveData

    override fun touch() {
        incrementVersion()
    }

    private fun incrementVersion() =
        versionLiveData.postValue(version + 1)

    private val version: Long
        get() = versionLiveData.value ?: 0
}