package vc.rux.todoclient

internal fun ensureUrlEndsWithSlash(url: String) =
    if (url.endsWith("/")) url else "$url/"
