package com.daily.dayo.common

import androidx.lifecycle.MutableLiveData

class ListLiveData<T> : MutableLiveData<ArrayList<T>>() {
    init {
        value = ArrayList()
    }

    fun add(item: T) {
        val items: ArrayList<T>? = value
        items!!.add(item)
        value = items
    }

    fun addAll(list: List<T>) {
        val items: ArrayList<T>? = value
        items!!.addAll(list)
        value = items
    }

    fun clear(notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.clear()
        if (notify) {
            value = items
        }
    }

    fun size(): Int {
        return value?.size ?: 0
    }

    fun replaceAll(list: List<T>) {
        val items: ArrayList<T> = arrayListOf()
        items.addAll(list)
        value = items
    }

    fun removeAt(pos: Int) {
        val items: ArrayList<T>? = value
        items!!.removeAt(pos)
        value = items
    }

    fun remove(item: T) {
        val items: ArrayList<T>? = value
        items!!.remove(item)
        value = items
    }

    fun notifyChange() {
        val items: ArrayList<T>? = value
        value = items
    }
}