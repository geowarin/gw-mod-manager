package com.geowarin.modmanager.db.testUtils

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun Path.write(text: String) {
  Files.createDirectories(this.parent)
  Files.write(this, text.toByteArray())
}

fun Path.mockWith(resource: String) {
  Files.createDirectories(this.parent)
  val baseDir = File(javaClass.getResource(resource).toURI())
  baseDir.walk().forEach { file ->
    val mockResolve = this.resolve(file.relativeTo(baseDir).toPath().toString())
    if (file.isDirectory) {
      Files.createDirectory(mockResolve)
    } else {
      println("Writing file $mockResolve")
      Files.write(mockResolve, file.readBytes())
    }
  }
}
