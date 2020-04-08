package vc.rux.klinent4todobackend.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Exception
import kotlinx.coroutines.*
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


/**
 * Debounce operator
 * Thanks to https://gist.github.com/mirmilad/f7feb8007d6b572150cb84fef0b65879
 */
fun <T> LiveData<T>.debounce(duration: Long = 1000L, coroutineScope: CoroutineScope) = MediatorLiveData<T>().also { mld ->
    val source = this
    var job: Job? = null

    mld.addSource(source) {
        job?.cancel()
        job = coroutineScope.launch {
            delay(duration)
            mld.value = source.value
        }
    }
}
