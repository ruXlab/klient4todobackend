package vc.rux.klinent4todobackend.misc

import kotlin.reflect.KClass
import org.slf4j.LoggerFactory

fun logger(klass: KClass<*>) =
    LoggerFactory.getLogger(klass.java)

inline fun <reified T : Any> logger() =
        logger(T::class)
