package com.geowarin.modmanager.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.andTry
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

fun Path.toURI(): URI = this.toUri()
fun Path.exists(): Boolean = Files.exists(this)
fun Path.konsumeXml(): Konsumer = Files.newInputStream(this).andTry { it.buffered().konsumeXml(this.toString()) }

fun Path.parseJson(): JsonObject {
  return Klaxon().parseJsonObject(Files.newBufferedReader(this))
}
