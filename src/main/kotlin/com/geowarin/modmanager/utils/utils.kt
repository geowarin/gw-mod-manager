package com.geowarin.modmanager.utils

import java.io.File

private fun getCacheHome(): File {
  val xdgCache: String = System.getenv("XDG_CACHE_HOME")
    ?: return File(System.getProperty("user.home"), ".cache")
  return File(xdgCache)
}

fun getCacheDir(): File {
  return File(getCacheHome(), "gw-mod-manager")
}
