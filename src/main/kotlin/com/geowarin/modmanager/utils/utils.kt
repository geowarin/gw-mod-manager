package com.geowarin.modmanager.utils

import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.andTry
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

fun getCacheDir(fs: FileSystem = FileSystems.getDefault()): Path {
  return fs.getPath(System.getenv("XDG_CACHE_HOME")).resolve("gw-mod-manager")
    ?: fs.getPath(System.getProperty("user.home"), "gw-mod-manager", "cache")
}

fun getConfigDir(fs: FileSystem = FileSystems.getDefault()): Path {
  return fs.getPath(System.getenv("XDG_CONFIG_HOME")).resolve("gw-mod-manager")
    ?: fs.getPath(System.getProperty("user.home"), "gw-mod-manager", "config")
}

fun Path.toURI(): URI = this.toUri()
fun Path.exists(): Boolean = Files.exists(this)
fun Path.konsumeXml(): Konsumer = Files.newInputStream(this).andTry { it.buffered().konsumeXml(this.toString()) }
