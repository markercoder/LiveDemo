package com.kingsley.livedemo

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.LiveData


/**
 * Project: LiveDemo
 * Package: com.kingsley.livedemo
 * @date : 2019/8/14 17:05
 * @author : Kingsley
 * info: https://tech.meituan.com/2018/07/26/android-livedatabus.html
 */
class LiveDataBus private constructor() {

    private val bus: MutableMap<String, BusMutableLiveData<*>>

    init {
        bus = HashMap()
    }

    private object SingletonHolder {
        internal val DEFAULT_BUS = LiveDataBus()
    }

    fun <T> with(key: String, type: Class<T>): MutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData<T>()
        }
        return (bus[key] as MutableLiveData<T>?)!!
    }

    fun with(key: String): MutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>?) : Observer<T> {

        private val isCallOnObserve: Boolean
            get() {
                val stackTrace = Thread.currentThread().stackTrace
                if (stackTrace.isNotEmpty()) {
                    for (element in stackTrace) {
                        if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                            return true
                        }
                    }
                }
                return false
            }

        override fun onChanged(t: T?) {
            if (observer != null) {
                if (isCallOnObserve) {
                    return
                }
                observer.onChanged(t)
            }
        }
    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {

        private val observerMap = HashMap<Observer<T>, LiveDataBus.ObserverWrapper<T>>()

        override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun observeForever(observer: Observer<T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap.put(observer, ObserverWrapper(observer))
            }
            super.observeForever(observerMap.get(observer)!!)
        }

        override fun removeObserver(observer: Observer<T>) {
            val realObserver: Observer<T>?
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer)
            } else {
                realObserver = observer
            }
            super.removeObserver(realObserver!!)
        }

        @Throws(Exception::class)
        private fun hook(observer: Observer<T>) {
            //get wrapper's version
            val classLiveData = LiveData::class.java
            val fieldObservers = classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers.get(this)
            val classObservers = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            val classObserverWrapper = objectWrapper.javaClass.superclass
            val fieldLastVersion = classObserverWrapper!!.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            //get livedata's version
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion.get(this)
            //set wrapper's version
            fieldLastVersion.set(objectWrapper, objectVersion)
        }
    }

    companion object {

        fun get(): LiveDataBus {
            return SingletonHolder.DEFAULT_BUS
        }
    }
}