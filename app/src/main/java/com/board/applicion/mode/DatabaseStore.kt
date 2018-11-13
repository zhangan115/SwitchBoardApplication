package com.board.applicion.mode

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.board.applicion.app.App
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import io.objectbox.reactive.DataSubscriptionList

class DatabaseStore<T>(lifecycle: Lifecycle, entityClass: Class<T>) : LifecycleObserver {
    private var box: Box<T>? = null
    private val subscriptions: DataSubscriptionList = DataSubscriptionList()

    init {
        //初始化
        lifecycle.addObserver(this)
        box = App.getBoxStore().boxFor(entityClass)
    }

    fun saveData(t: T): Boolean {
        return box!!.put(t) > 0
    }

    fun deleteData(t: T) {
        box!!.remove(t)
    }

    fun getQueryData(query: Query<T>, callBack: (List<T>) -> Unit) {
        query.subscribe(subscriptions).on(AndroidScheduler.mainThread()).observer {
            callBack(it)
        }
    }

    fun getQueryBuilder(): QueryBuilder<T> {
        return box!!.query()
    }

    fun getBox(): Box<T> {
        return box!!
    }

    /**
     * 取消所有的订阅
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        try {
            subscriptions.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}