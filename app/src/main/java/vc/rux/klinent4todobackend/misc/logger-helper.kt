package vc.rux.klinent4todobackend.misc

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun logger(klass: KClass<*>)
    = LoggerFactory.getLogger(klass.java)

inline fun <reified T: Any> logger()
        = logger(T::class)