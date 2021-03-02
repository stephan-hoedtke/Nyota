package com.stho.nyota

interface ISelectable<T> {
    fun select(item: T)
    fun unselect()
}

