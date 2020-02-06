/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vc.rux.klinent4todobackend.misc


/**
 * A generic class that holds a value with its loading status.
 * Taken from https://github.com/android/architecture-samples/blob/858a75ef776cfa87930276b92d995ebb0025cfb4/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/Result.kt
 * @param <T>
 */
sealed class Loadable<out R> {

    data class Success<out T>(val data: T) : Loadable<T>()
    data class Error(val exception: Exception) : Loadable<Nothing>()
    object Loading : Loadable<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

