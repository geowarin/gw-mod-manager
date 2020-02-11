package com.geowarin.modmanager.utils

import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.andTry
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private fun getCacheHome(fs: FileSystem): Path {
  val xdgCache: String = System.getenv("XDG_CACHE_HOME")
    ?: return fs.getPath(System.getProperty("user.home"), ".cache")
  return fs.getPath(xdgCache)
}

fun getCacheDir(fs: FileSystem = FileSystems.getDefault()): Path {
  return getCacheHome(fs).resolve("gw-mod-manager")
}

fun Path.toURI(): URI = this.toUri()
fun Path.exists(): Boolean = Files.exists(this)
fun Path.konsumeXml(): Konsumer = Files.newInputStream(this).andTry { it.buffered().konsumeXml(this.toString()) }
