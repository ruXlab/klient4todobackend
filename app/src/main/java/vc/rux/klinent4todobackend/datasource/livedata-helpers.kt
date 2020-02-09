package vc.rux.klinent4todobackend.datasource

import androidx.lifecycle.MutableLiveData
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vc.rux.klinent4todobackend.misc.Loadable

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
suspend fun <R> MutableLiveData<Loadable<R>>.startLoadable(cb: suspend () -> R): Loadable<R> = withContext(Dispatchers.Main) {
    postValue(Loadable.Loading)
    val result = try {
        Loadable.Success(cb())
    } catch (ex: Exception) {
        Loadable.Error(ex)
    }
    postValue(result)
    result
}
