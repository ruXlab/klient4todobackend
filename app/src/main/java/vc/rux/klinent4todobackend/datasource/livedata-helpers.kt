package vc.rux.klinent4todobackend.datasource

import androidx.lifecycle.MutableLiveData
import vc.rux.klinent4todobackend.misc.Loadable
import java.lang.Exception

fun <R> MutableLiveData<Loadable<R>>.postLoadable(cb: () -> R) {
    try {
        postValue(Loadable.Success(cb()))
    } catch (ex: Exception) {
        postValue(Loadable.Error(ex))
    }
}

/**
 * Emits "loading" state and starts loadable block in cb
 * State diagram:
 *            Loading
 *           /      \
 *   Success<R>    Error
 */
suspend fun <R> MutableLiveData<Loadable<R>>.startLoadable(cb: suspend () -> R) {
    postValue(Loadable.Loading)
    try {
        postValue(Loadable.Success(cb()))
    } catch (ex: Exception) {
        postValue(Loadable.Error(ex))
    }
}