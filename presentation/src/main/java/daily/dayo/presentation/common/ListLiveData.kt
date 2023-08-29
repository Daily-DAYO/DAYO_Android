package daily.dayo.presentation.common

import androidx.lifecycle.MutableLiveData

class ListLiveData<T> : MutableLiveData<ArrayList<T>>() {
    init {
        value = ArrayList()
    }

    fun add(item: T, notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.add(item)
        if (notify) {
            value = items
        }
    }

    fun addAll(list: List<T>, notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.addAll(list)
        if (notify) {
            value = items
        }
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

    fun replaceAll(list: List<T>, notify: Boolean) {
        val items: ArrayList<T> = arrayListOf()
        items.addAll(list)
        if (notify) {
            value = items
        }
    }

    fun removeAt(pos: Int, notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.removeAt(pos)
        if (notify) {
            value = items
        }
    }

    fun remove(item: T, notify: Boolean) {
        val items: ArrayList<T>? = value
        items!!.remove(item)
        if (notify) {
            value = items
        }
    }

    fun notifyChange() {
        val items: ArrayList<T>? = value
        value = items
    }
}